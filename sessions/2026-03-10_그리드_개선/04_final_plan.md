# 그리드 개선 최종 구현 계획서

## Critic 반영 사항

| # | 이슈 | 관점 | 반영 내용 |
|---|------|------|----------|
| 1 | BillStdPage 그리드 헤더 변경 누락 가능성 | 요구사항 대비 누락 | Step 7-4에 그리드 columns의 header도 "서비스코드" → "서비스"로 변경하도록 명시 추가 |
| 2 | SubscriptionServiceImpl Specification에 `SUBS_NM` 검색 분기 누락 | 단계 간 의존성 모순 | Step 4-2 Specification에 `SUBS_NM` 분기 추가. 프론트엔드 검색 드롭다운과 백엔드 분기가 1:1 대응되도록 보정 |

---

## 1. 구현 전략 개요

5개 요구사항을 의존성 순서에 따라 3단계로 나눠 진행한다.

1. **DB/Entity/DTO 기반 변경** (F3): `svc_nm/fee_prod_nm` → `svc_cd/fee_prod_cd` 변환. 모든 후속 작업의 기초.
2. **백엔드 페이징 전환** (F1): 3개 화면의 Controller/Service/Repository를 `Page<T>` 반환으로 변경.
3. **프론트엔드 일괄 적용** (F1+F2+F3+F4+F5): 그리드 페이징 props, 콤보 확인, 필드명 변경, 레이블 "코드" 제거, `subsNm` 버그 수정.

핵심 원칙: 기존 UserPage 페이징 패턴(`Specification` + `Pageable`)을 그대로 따른다.

---

## 2. 변경 파일 목록

| # | 파일 | 변경 유형 |
|---|------|----------|
| 1 | `src/main/resources/schema.sql` | 컬럼 변경 |
| 2 | `src/main/resources/data.sql` | `fee_prod_cd` 공통코드 추가 |
| 3 | `src/.../Subscription.java` | 필드 `svcNm/feeProdNm` → `svcCd/feeProdCd` |
| 4 | `src/.../SubscriptionRequestDto.java` | 동일 필드 변경 |
| 5 | `src/.../SubscriptionResponseDto.java` | 동일 필드 변경 |
| 6 | `src/.../SubscriptionRepository.java` | 쿼리 메서드 변경 + `JpaSpecificationExecutor` 추가 |
| 7 | `src/.../SubscriptionService.java` | `search` → `searchPage` 시그니처 변경 |
| 8 | `src/.../SubscriptionServiceImpl.java` | 페이징 + 필드 매핑 변경 |
| 9 | `src/.../SubscriptionController.java` | `Page<T>` 반환 + `Pageable` 파라미터 |
| 10 | `src/.../SubscriptionDataInitializer.java` | 코드값으로 변경 + 샘플 데이터 30건으로 증설 |
| 11 | `src/.../SubscriptionMainListResponseDto.java` | `svcNm/feeProdNm` → `svcCd/feeProdCd` + `subsNm` 추가 |
| 12 | `src/.../SubscriptionMainRepository.java` | 네이티브 쿼리 컬럼 변경 + `subsNm` 추가 + 페이징 |
| 13 | `src/.../SubscriptionMainService.java` | 시그니처 변경 (`Page<T>`) |
| 14 | `src/.../SubscriptionMainServiceImpl.java` | 쿼리 결과 매핑 변경 + 페이징 |
| 15 | `src/.../SubscriptionMainController.java` | `Page<T>` 반환 + `Pageable` 파라미터 + `svcNm` → `svcCd` |
| 16 | `src/.../SpecialSubscriptionRepository.java` | `JpaSpecificationExecutor` 추가 |
| 17 | `src/.../SpecialSubscriptionService.java` | `findAll` → `findPage` 시그니처 변경 |
| 18 | `src/.../SpecialSubscriptionServiceImpl.java` | `Specification` 기반 페이징 |
| 19 | `src/.../SpecialSubscriptionController.java` | `Page<T>` 반환 + `Pageable` 파라미터 |
| 20 | `frontend-vue/src/api/subscriptionApi.js` | 페이징 API 함수 추가 |
| 21 | `frontend-vue/src/api/subscriptionMainApi.js` | 페이징 API 함수 추가 |
| 22 | `frontend-vue/src/api/specialSubscriptionApi.js` | 페이징 API 함수 추가 |
| 23 | `frontend-vue/src/pages/SubscriptionPage.vue` | 페이징 + `svcCd/feeProdCd` 콤보 + 레이블 변경 |
| 24 | `frontend-vue/src/pages/SubscriptionMainPage.vue` | 페이징 + `svcCd/feeProdCd` + `subsNm` + `SVC_MAP` 제거 |
| 25 | `frontend-vue/src/pages/SpecialSubscriptionPage.vue` | 페이징 + "서비스코드" → "서비스" 레이블 |
| 26 | `frontend-vue/src/pages/BillStdPage.vue` | "서비스코드" → "서비스" 레이블 (그리드 헤더 + 폼 레이블) |

---

## 3. 단계별 구현 순서

```
Step 1: DB + 공통코드 (schema.sql, data.sql)
Step 2: Entity/DTO 필드 변경 (Subscription, DTO 3개)
Step 3: Repository 변경 (Subscription, SubscriptionMain, SpecialSubscription)
Step 4: Service/Controller 페이징 전환 (3개 도메인)
Step 5: DataInitializer 수정
Step 6: 프론트엔드 API 레이어 변경
Step 7: 프론트엔드 화면 변경 (4개 화면)
Step 8: 콤보 글자 깨짐 확인 (런타임 검증)
```

---

## 4. 각 단계별 상세 내용

### Step 1: DB + 공통코드

#### 1-1. schema.sql — tb_subscription 컬럼 변경

**Before:**
```sql
    svc_nm                  VARCHAR(100)   NULL,                   -- 서비스명
    fee_prod_nm             VARCHAR(100)   NULL,                   -- 요금상품명
```

**After:**
```sql
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    fee_prod_cd             VARCHAR(10)    NULL,                   -- 요금상품코드
```

#### 1-2. data.sql — fee_prod_cd 공통코드 헤더 추가

기존 `svc_cd` 헤더 뒤에 추가한다.

**After (추가):**
```sql
MERGE INTO tb_common_code (common_code, common_code_nm, remark, created_by, created_dt) KEY (common_code) VALUES
('fee_prod_cd',             '요금상품코드',              'tb_subscription.fee_prod_cd',                     'SYSTEM', CURRENT_TIMESTAMP);
```

#### 1-3. data.sql — fee_prod_cd 상세코드 추가 (6개)

기존 서비스코드 상세 뒤에 추가한다.

**After (추가):**
```sql
-- 요금상품코드
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('fee_prod_cd', 'FP_A', '요금상품 A', 1, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('fee_prod_cd', 'FP_B', '요금상품 B', 2, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('fee_prod_cd', 'FP_C', '요금상품 C', 3, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('fee_prod_cd', 'FP_D', '요금상품 D', 4, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('fee_prod_cd', 'FP_E', '요금상품 E', 5, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_common_dtl_code (common_code, common_dtl_code, common_dtl_code_nm, sort_order, eff_start_dt, eff_end_dt, created_by, created_dt) KEY (common_code, common_dtl_code) VALUES
('fee_prod_cd', 'FP_F', '요금상품 F', 6, TIMESTAMP '2000-01-01 00:00:00', TIMESTAMP '9999-12-31 23:59:59', 'SYSTEM', CURRENT_TIMESTAMP);
```

---

### Step 2: Entity/DTO 필드 변경

#### 2-1. Subscription.java

**Before:**
```java
    @Column(name = "svc_nm", length = 100)
    private String svcNm;

    @Column(name = "fee_prod_nm", length = 100)
    private String feeProdNm;
```
```java
    public String getSvcNm() { return svcNm; }
    public void setSvcNm(String svcNm) { this.svcNm = svcNm; }

    public String getFeeProdNm() { return feeProdNm; }
    public void setFeeProdNm(String feeProdNm) { this.feeProdNm = feeProdNm; }
```

**After:**
```java
    @Column(name = "svc_cd", length = 10)
    private String svcCd;

    @Column(name = "fee_prod_cd", length = 10)
    private String feeProdCd;
```
```java
    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getFeeProdCd() { return feeProdCd; }
    public void setFeeProdCd(String feeProdCd) { this.feeProdCd = feeProdCd; }
```

#### 2-2. SubscriptionRequestDto.java

**Before:**
```java
    private String svcNm;
    private String feeProdNm;
```
```java
    public String getSvcNm() { return svcNm; }
    public void setSvcNm(String svcNm) { this.svcNm = svcNm; }

    public String getFeeProdNm() { return feeProdNm; }
    public void setFeeProdNm(String feeProdNm) { this.feeProdNm = feeProdNm; }
```

**After:**
```java
    private String svcCd;
    private String feeProdCd;
```
```java
    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }

    public String getFeeProdCd() { return feeProdCd; }
    public void setFeeProdCd(String feeProdCd) { this.feeProdCd = feeProdCd; }
```

#### 2-3. SubscriptionResponseDto.java

`svcNm` → `svcCd`, `feeProdNm` → `feeProdCd` 동일 패턴. getter/setter도 일괄 변경.

#### 2-4. SubscriptionMainListResponseDto.java

**Before:**
```java
public class SubscriptionMainListResponseDto {
    private String subsId;
    private String svcNm;
    private String feeProdNm;
    private String mainSubsYn;
    private String mainSubsId;
```

**After:**
```java
public class SubscriptionMainListResponseDto {
    private String subsId;
    private String subsNm;
    private String svcCd;
    private String feeProdCd;
    private String mainSubsYn;
    private String mainSubsId;
```

생성자, getter/setter도 `svcNm` → `svcCd`, `feeProdNm` → `feeProdCd`, `subsNm` 추가.

---

### Step 3: Repository 변경

#### 3-1. SubscriptionRepository.java

**Before:**
```java
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findBySubsIdContainingIgnoreCase(String subsId);
    List<Subscription> findBySvcNmContainingIgnoreCase(String svcNm);
    List<Subscription> findByFeeProdNmContainingIgnoreCase(String feeProdNm);
    List<Subscription> findBySubsStatusCd(String subsStatusCd);

    long countBySubsId(String subsId);
}
```

**After:**
```java
public interface SubscriptionRepository extends JpaRepository<Subscription, String>,
        JpaSpecificationExecutor<Subscription> {

    long countBySubsId(String subsId);
}
```

기존 `findBy*` 메서드 삭제 → `Specification` 기반 동적 검색으로 대체.

#### 3-2. SubscriptionMainRepository.java — 네이티브 쿼리 변경

**Before:**
```sql
SELECT s.subs_id, s.svc_nm, s.fee_prod_nm,
       sm.main_subs_yn, sm.main_subs_id
FROM tb_subscription s
LEFT JOIN tb_subscription_main sm
  ON s.subs_id = sm.subs_id
 AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
WHERE (:svcNm IS NULL OR s.svc_nm = :svcNm)
  AND (
    (:searchType = '서비스'    AND LOWER(s.svc_nm)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
    (:searchType = '상품'      AND LOWER(s.fee_prod_nm)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
    (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
    (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
  )
ORDER BY s.subs_id
```

**After:**
```sql
SELECT s.subs_id, s.subs_nm, s.svc_cd, s.fee_prod_cd,
       sm.main_subs_yn, sm.main_subs_id
FROM tb_subscription s
LEFT JOIN tb_subscription_main sm
  ON s.subs_id = sm.subs_id
 AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
WHERE (:svcCd IS NULL OR s.svc_cd = :svcCd)
  AND (
    (:searchType = '서비스'    AND LOWER(s.svc_cd)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
    (:searchType = '상품'      AND LOWER(s.fee_prod_cd)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
    (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
    (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
  )
ORDER BY s.subs_id
```

변경 사항: `s.svc_nm` → `s.svc_cd`, `s.fee_prod_nm` → `s.fee_prod_cd`, `:svcNm` → `:svcCd`, `s.subs_nm` SELECT에 추가.

> **참고**: SubscriptionMain의 페이징은 네이티브 쿼리 특성상 `countQuery`를 별도 정의하고 `Pageable` 파라미터를 추가하는 방식으로 구현한다. `Page<Object[]>` 반환.

네이티브 쿼리 페이징을 위해 countQuery 추가:

```java
@Query(value = """
    SELECT s.subs_id, s.subs_nm, s.svc_cd, s.fee_prod_cd,
           sm.main_subs_yn, sm.main_subs_id
    FROM tb_subscription s
    LEFT JOIN tb_subscription_main sm
      ON s.subs_id = sm.subs_id
     AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
    WHERE (:svcCd IS NULL OR s.svc_cd = :svcCd)
      AND (
        (:searchType = '서비스'    AND LOWER(s.svc_cd)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
        (:searchType = '상품'      AND LOWER(s.fee_prod_cd)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
        (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
        (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
      )
    ORDER BY s.subs_id
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM tb_subscription s
    LEFT JOIN tb_subscription_main sm
      ON s.subs_id = sm.subs_id
     AND :now BETWEEN sm.eff_start_dt AND sm.eff_end_dt
    WHERE (:svcCd IS NULL OR s.svc_cd = :svcCd)
      AND (
        (:searchType = '서비스'    AND LOWER(s.svc_cd)        LIKE LOWER(CONCAT(:keyword, '%'))) OR
        (:searchType = '상품'      AND LOWER(s.fee_prod_cd)   LIKE LOWER(CONCAT(:keyword, '%'))) OR
        (:searchType = '가입ID'    AND LOWER(s.subs_id)       LIKE LOWER(CONCAT(:keyword, '%'))) OR
        (:searchType = '대표가입ID' AND LOWER(sm.main_subs_id) LIKE LOWER(CONCAT(:keyword, '%')))
      )
    """,
    nativeQuery = true)
Page<Object[]> findListRaw(
    @Param("svcCd") String svcCd,
    @Param("searchType") String searchType,
    @Param("keyword") String keyword,
    @Param("now") LocalDateTime now,
    Pageable pageable);
```

#### 3-3. SpecialSubscriptionRepository.java

**Before:**
```java
public interface SpecialSubscriptionRepository extends JpaRepository<SpecialSubscription, SpecialSubscriptionId> {
    List<SpecialSubscription> findByIdSubsBillStdIdContaining(String subsBillStdId);
    List<SpecialSubscription> findBySubsIdContaining(String subsId);
    List<SpecialSubscription> findByIdSubsBillStdIdContainingAndSubsIdContaining(String subsBillStdId, String subsId);
}
```

**After:**
```java
public interface SpecialSubscriptionRepository extends JpaRepository<SpecialSubscription, SpecialSubscriptionId>,
        JpaSpecificationExecutor<SpecialSubscription> {
}
```

기존 `findBy*` 메서드 삭제 → `Specification` 기반 동적 검색으로 대체.

---

### Step 4: Service/Controller 페이징 전환

#### 4-1. SubscriptionService.java (인터페이스)

**Before:**
```java
    List<SubscriptionResponseDto> search(String type, String keyword);
```

**After:**
```java
    Page<SubscriptionResponseDto> searchPage(String type, String keyword, Pageable pageable);
```

#### 4-2. SubscriptionServiceImpl.java

**Before:**
```java
    @Override
    public List<SubscriptionResponseDto> search(String type, String keyword) {
        List<Subscription> result;
        String kw = (keyword == null) ? "" : keyword.trim();

        if ("SUBS_STATUS_CD".equals(type)) {
            result = repository.findBySubsStatusCd(kw);
        } else if ("SVC_NM".equals(type)) {
            result = repository.findBySvcNmContainingIgnoreCase(kw);
        } else if ("FEE_PROD_NM".equals(type)) {
            result = repository.findByFeeProdNmContainingIgnoreCase(kw);
        } else {
            // SUBS_ID (기본)
            result = repository.findBySubsIdContainingIgnoreCase(kw);
        }
        return result.stream().map(this::toDto).toList();
    }
```

**After (Critic #2 반영 -- `SUBS_NM` 분기 추가):**
```java
    @Override
    public Page<SubscriptionResponseDto> searchPage(String type, String keyword, Pageable pageable) {
        String kw = (keyword == null) ? "" : keyword.trim();

        Specification<Subscription> spec = (root, query, cb) -> {
            if (kw.isEmpty()) return cb.conjunction();
            if ("SUBS_STATUS_CD".equals(type)) {
                return cb.equal(root.get("subsStatusCd"), kw);
            } else if ("SVC_CD".equals(type)) {
                return cb.like(cb.lower(root.get("svcCd")), "%" + kw.toLowerCase() + "%");
            } else if ("FEE_PROD_CD".equals(type)) {
                return cb.like(cb.lower(root.get("feeProdCd")), "%" + kw.toLowerCase() + "%");
            } else if ("SUBS_NM".equals(type)) {
                return cb.like(cb.lower(root.get("subsNm")), "%" + kw.toLowerCase() + "%");
            } else {
                // SUBS_ID (기본)
                return cb.like(cb.lower(root.get("subsId")), "%" + kw.toLowerCase() + "%");
            }
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }
```

**create/update 메서드 내 매핑 변경 (Before/After 동일 파일):**

Before:
```java
        entity.setSvcNm(dto.getSvcNm());
        entity.setFeeProdNm(dto.getFeeProdNm());
```

After:
```java
        entity.setSvcCd(dto.getSvcCd());
        entity.setFeeProdCd(dto.getFeeProdCd());
```

**toDto 메서드 변경:**

Before:
```java
        dto.setSvcNm(e.getSvcNm());
        dto.setFeeProdNm(e.getFeeProdNm());
```

After:
```java
        dto.setSvcCd(e.getSvcCd());
        dto.setFeeProdCd(e.getFeeProdCd());
```

#### 4-3. SubscriptionController.java

**Before:**
```java
    @GetMapping
    public List<SubscriptionResponseDto> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return subscriptionService.search(type, keyword);
    }
```

**After:**
```java
    @GetMapping
    public Page<SubscriptionResponseDto> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return subscriptionService.searchPage(type, keyword, pageable);
    }
```

import에 `org.springframework.data.domain.Page`, `org.springframework.data.domain.Pageable` 추가.

#### 4-4. SubscriptionMainService.java

**Before:**
```java
    List<SubscriptionMainListResponseDto> findList(String svcNm, String searchType, String keyword);
```

**After:**
```java
    Page<SubscriptionMainListResponseDto> findListPage(String svcCd, String searchType, String keyword, Pageable pageable);
```

#### 4-5. SubscriptionMainServiceImpl.java

**Before:**
```java
    @Override
    public List<SubscriptionMainListResponseDto> findList(String svcNm, String searchType, String keyword) {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> rows = repository.findListRaw(
            (svcNm == null || svcNm.isEmpty()) ? null : svcNm,
            searchType,
            keyword,
            now
        );
        return rows.stream().map(r -> {
            SubscriptionMainListResponseDto dto = new SubscriptionMainListResponseDto();
            dto.setSubsId((String) r[0]);
            dto.setSvcNm((String) r[1]);
            dto.setFeeProdNm((String) r[2]);
            dto.setMainSubsYn(r[3] != null ? r[3].toString() : "N");
            dto.setMainSubsId((String) r[4]);
            return dto;
        }).toList();
    }
```

**After:**
```java
    @Override
    public Page<SubscriptionMainListResponseDto> findListPage(String svcCd, String searchType, String keyword, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        Page<Object[]> page = repository.findListRaw(
            (svcCd == null || svcCd.isEmpty()) ? null : svcCd,
            searchType,
            keyword,
            now,
            pageable
        );
        return page.map(r -> {
            SubscriptionMainListResponseDto dto = new SubscriptionMainListResponseDto();
            dto.setSubsId((String) r[0]);
            dto.setSubsNm((String) r[1]);
            dto.setSvcCd((String) r[2]);
            dto.setFeeProdCd((String) r[3]);
            dto.setMainSubsYn(r[4] != null ? r[4].toString() : "N");
            dto.setMainSubsId((String) r[5]);
            return dto;
        });
    }
```

인덱스 시프트에 주의: 기존 5컬럼 → 6컬럼 (subsNm 추가).

#### 4-6. SubscriptionMainController.java

**Before:**
```java
    @GetMapping
    public List<SubscriptionMainListResponseDto> getList(
        @RequestParam(required = false) String svcNm,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword) {
        return service.findList(svcNm, searchType, keyword);
    }
```

**After:**
```java
    @GetMapping
    public Page<SubscriptionMainListResponseDto> getList(
        @RequestParam(required = false) String svcCd,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword,
        Pageable pageable) {
        return service.findListPage(svcCd, searchType, keyword, pageable);
    }
```

#### 4-7. SpecialSubscriptionService.java

**Before:**
```java
    List<SpecialSubscriptionResponseDto> findAll(String subsBillStdId, String subsId);
```

**After:**
```java
    Page<SpecialSubscriptionResponseDto> findPage(String subsBillStdId, String subsId, Pageable pageable);
```

#### 4-8. SpecialSubscriptionServiceImpl.java

**Before:**
```java
    @Override
    public List<SpecialSubscriptionResponseDto> findAll(String subsBillStdId, String subsId) {
        boolean hasBillStdId = subsBillStdId != null && !subsBillStdId.isBlank();
        boolean hasSubsId = subsId != null && !subsId.isBlank();

        List<SpecialSubscription> list;
        if (hasBillStdId && hasSubsId) {
            list = repository.findByIdSubsBillStdIdContainingAndSubsIdContaining(subsBillStdId, subsId);
        } else if (hasBillStdId) {
            list = repository.findByIdSubsBillStdIdContaining(subsBillStdId);
        } else if (hasSubsId) {
            list = repository.findBySubsIdContaining(subsId);
        } else {
            list = repository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }
```

**After:**
```java
    @Override
    public Page<SpecialSubscriptionResponseDto> findPage(String subsBillStdId, String subsId, Pageable pageable) {
        Specification<SpecialSubscription> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (subsBillStdId != null && !subsBillStdId.isBlank()) {
                predicates.add(cb.like(root.get("id").get("subsBillStdId"), "%" + subsBillStdId + "%"));
            }
            if (subsId != null && !subsId.isBlank()) {
                predicates.add(cb.like(root.get("subsId"), "%" + subsId + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }
```

#### 4-9. SpecialSubscriptionController.java

**Before:**
```java
    @GetMapping
    public List<SpecialSubscriptionResponseDto> getAll(
            @RequestParam(required = false) String subsBillStdId,
            @RequestParam(required = false) String subsId) {
        return service.findAll(subsBillStdId, subsId);
    }
```

**After:**
```java
    @GetMapping
    public Page<SpecialSubscriptionResponseDto> getAll(
            @RequestParam(required = false) String subsBillStdId,
            @RequestParam(required = false) String subsId,
            Pageable pageable) {
        return service.findPage(subsBillStdId, subsId, pageable);
    }
```

---

### Step 5: DataInitializer 수정

**Before:**
```java
        for (int i = 1; i <= 10; i++) {
            Subscription s = new Subscription();
            s.setSubsId(String.format("SUBS%04d", i));
            s.setSubsNm("가입자" + i);
            s.setSvcNm("서비스" + ((i % 3) + 1));
            s.setFeeProdNm("요금상품" + ((i % 2) + 1));
```

**After:**
```java
        String[] svcCodes = {"SVC01", "SVC02", "SVC03"};
        String[] feeProdCodes = {"FP_A", "FP_B", "FP_C", "FP_D", "FP_E", "FP_F"};

        for (int i = 1; i <= 30; i++) {
            Subscription s = new Subscription();
            s.setSubsId(String.format("SUBS%04d", i));
            s.setSubsNm("가입자" + i);
            s.setSvcCd(svcCodes[i % 3]);
            s.setFeeProdCd(feeProdCodes[i % 6]);
```

30건으로 증설하여 페이징 동작을 확인할 수 있도록 한다.

---

### Step 6: 프론트엔드 API 레이어 변경

#### 6-1. subscriptionApi.js

**Before:**
```js
export const searchSubscriptions = (type, keyword) =>
  apiClient.get('/subscriptions', { params: { type, keyword } }).then(res => res.data)
```

**After:**
```js
export const searchSubscriptionsPage = (params) =>
  apiClient.get('/subscriptions', { params }).then(res => res.data)
```

#### 6-2. subscriptionMainApi.js

**Before:**
```js
export const getSubscriptionMainList = (params) =>
  apiClient.get('/subscription-main', { params }).then(r => r.data)
```

**After:**
```js
export const getSubscriptionMainListPage = (params) =>
  apiClient.get('/subscription-main', { params }).then(r => r.data)
```

#### 6-3. specialSubscriptionApi.js

**Before:**
```js
export const getSpecialSubscriptions = (params) =>
  apiClient.get('/special-subscriptions', { params }).then(r => r.data)
```

**After:**
```js
export const getSpecialSubscriptionsPage = (params) =>
  apiClient.get('/special-subscriptions', { params }).then(r => r.data)
```

---

### Step 7: 프론트엔드 화면 변경

#### 7-1. SubscriptionPage.vue

**주요 변경:**

1. **페이징 상태 추가** (UserPage 패턴 차용):
```js
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10
```

2. **columns 변경** — `svcNm/feeProdNm` → `svcCd/feeProdCd`, 레이블 변경, CommonCodeLabel 적용:

Before:
```js
const { getLabel } = useCommonCodeLabel(['subs_status_cd'])

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcNm', header: '서비스명', size: 150 },
  { key: 'feeProdNm', header: '요금제명', size: 150 },
  { key: 'subsStatusCd', header: '상태', size: 80,
    cell: { props: ['value'], setup(props) { return () => getLabel('subs_status_cd', props.value) } } },
  { key: 'subsDt', header: '가입일시', size: 160 },
])
```

After:
```js
const { getLabel } = useCommonCodeLabel(['subs_status_cd', 'svc_cd', 'fee_prod_cd'])

const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcCd', header: '서비스', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
  { key: 'feeProdCd', header: '요금상품', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('fee_prod_cd', props.value) } } },
  { key: 'subsStatusCd', header: '상태', size: 80,
    cell: { props: ['value'], setup(props) { return () => getLabel('subs_status_cd', props.value) } } },
  { key: 'subsDt', header: '가입일시', size: 160 },
])
```

3. **EMPTY_FORM/toFormData/toRequestDto 변경:**

Before:
```js
const EMPTY_FORM = { subsId: '', subsNm: '', svcNm: '', feeProdNm: '', subsStatusCd: '', subsDt: '', chgDt: '' }
```

After:
```js
const EMPTY_FORM = { subsId: '', subsNm: '', svcCd: '', feeProdCd: '', subsStatusCd: '', subsDt: '', chgDt: '' }
```

`toFormData`, `toRequestDto` 내 `svcNm` → `svcCd`, `feeProdNm` → `feeProdCd`.

4. **폼 템플릿 변경** — `input` → `CommonCodeSelect` 콤보:

Before:
```html
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스명</label>
            <input v-model="formData.svcNm" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">요금제명</label>
            <input v-model="formData.feeProdNm" class="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400" />
          </div>
```

After:
```html
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <CommonCodeSelect common-code="svc_cd" v-model="formData.svcCd" />
          </div>
          <div>
            <label class="block text-xs text-gray-500 mb-1">요금상품</label>
            <CommonCodeSelect common-code="fee_prod_cd" v-model="formData.feeProdCd" />
          </div>
```

5. **DataGrid에 페이징 props 추가:**

Before:
```html
      <DataGrid
        :columns="columns"
        :data="items"
        row-id-accessor="subsId"
        :selected-row-id="selectedSubs?.subsId"
        @row-click="onRowSelect"
        storage-key="subscriptionPage"
        title="가입 목록"
      />
```

After:
```html
      <DataGrid
        :columns="columns"
        :data="items"
        :page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :page-size="pageSize"
        :on-page-change="handlePageChange"
        row-id-accessor="subsId"
        :selected-row-id="selectedSubs?.subsId"
        @row-click="onRowSelect"
        storage-key="subscriptionPage"
        title="가입 목록"
      />
```

6. **fetchList/onSearch를 페이징 호출로 변경:**

```js
const fetchList = async (searchParams = {}, pageNum = 0) => {
  const data = await searchSubscriptionsPage({ type: searchType.value, keyword: keyword.value.trim(), page: pageNum, size: pageSize })
  items.value = data.content
  page.value = data.number
  totalPages.value = data.totalPages
  totalElements.value = data.totalElements
}

const handlePageChange = (newPage) => fetchList({}, newPage)
```

7. **검색유형 드롭다운 변경:**

Before:
```html
              <option value="SUBS_ID">가입ID</option>
              <option value="SUBS_NM">가입명</option>
```

After:
```html
              <option value="SUBS_ID">가입ID</option>
              <option value="SUBS_NM">가입명</option>
              <option value="SVC_CD">서비스</option>
              <option value="FEE_PROD_CD">요금상품</option>
```

#### 7-2. SubscriptionMainPage.vue

**주요 변경:**

1. **SVC_MAP / SVC_LABEL_MAP 제거, displayItems computed 제거.**

2. **서비스 조회 드롭다운을 CommonCodeSelect로 교체:**

Before:
```html
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <select v-model="svcNm" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option>전체</option>
              <option>IDC 전력</option>
              <option>IDC NW</option>
              <option>비즈넷</option>
            </select>
          </div>
```

After:
```html
          <div>
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
            <select v-model="svcCd" class="h-8 px-2 border border-gray-300 rounded text-sm bg-white">
              <option value="">전체</option>
              <option v-for="opt in svcOptions" :key="opt.commonDtlCode" :value="opt.commonDtlCode">
                {{ opt.commonDtlCodeNm }}
              </option>
            </select>
          </div>
```

`svcOptions`는 `CommonCodeSelect`와 동일한 방식으로 `commonCodeApi.getEffectiveDetails('svc_cd')`에서 로드.

3. **columns 변경:**

Before:
```js
const columns = [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcNm', header: '서비스명', size: 120 },
  { key: 'mainSubsYn', header: '대표가입여부', size: 100 },
  { key: 'mainSubsId', header: '대표가입ID', size: 120 },
]
```

After:
```js
const columns = computed(() => [
  { key: 'subsId', header: '가입ID', size: 120 },
  { key: 'subsNm', header: '가입명', size: 150 },
  { key: 'svcCd', header: '서비스', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
  { key: 'feeProdCd', header: '요금상품', size: 120,
    cell: { props: ['value'], setup(props) { return () => getLabel('fee_prod_cd', props.value) } } },
  { key: 'mainSubsYn', header: '대표가입여부', size: 100 },
  { key: 'mainSubsId', header: '대표가입ID', size: 120 },
])
```

`useCommonCodeLabel(['svc_cd', 'fee_prod_cd'])` import 및 호출 추가.

4. **페이징 상태/props 추가** (SubscriptionPage와 동일 패턴).

5. **DataGrid의 `:data="displayItems"` → `:data="items"` 변경** (displayItems computed 제거).

6. **getSearchParams 변경:**

Before:
```js
const getSearchParams = () => ({
  svcNm: svcNm.value !== '전체' ? SVC_MAP[svcNm.value] : undefined,
  searchType: searchType.value || undefined,
  keyword: keyword.value.trim(),
})
```

After:
```js
const getSearchParams = () => ({
  svcCd: svcCd.value || undefined,
  searchType: searchType.value || undefined,
  keyword: keyword.value.trim(),
})
```

#### 7-3. SpecialSubscriptionPage.vue

**주요 변경:**

1. **columns 레이블 변경:**

Before:
```js
  { key: 'svcCd', header: '서비스코드', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
```

After:
```js
  { key: 'svcCd', header: '서비스', size: 100,
    cell: { props: ['value'], setup(props) { return () => getLabel('svc_cd', props.value) } } },
```

2. **폼 레이블 변경:**

Before:
```html
              <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
```

After:
```html
              <label class="block text-xs text-gray-500 mb-1">서비스</label>
```

3. **페이징 상태/props 추가** (동일 패턴).

4. **API 호출 변경:**

Before:
```js
    items.value = await getSpecialSubscriptions(getSearchParams())
```

After:
```js
    const data = await getSpecialSubscriptionsPage({ ...getSearchParams(), page: pageNum, size: pageSize })
    items.value = data.content
    page.value = data.number
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
```

#### 7-4. BillStdPage.vue — 레이블 변경 (Critic #1 반영 -- 그리드 헤더 포함)

**폼 레이블 변경:**

Before:
```html
            <label class="block text-xs text-gray-500 mb-1">서비스코드</label>
```

After:
```html
            <label class="block text-xs text-gray-500 mb-1">서비스</label>
```

**그리드 columns 헤더 변경 (해당하는 경우):**

columns 정의에서 "서비스코드"로 되어 있는 header를 "서비스"로 변경한다. 실제 columns 코드를 확인하여 `header: '서비스코드'`가 있으면 `header: '서비스'`로 수정.

---

### Step 8: 콤보 글자 깨짐 확인

코드 레벨에서는 `CommonCodeSelect.vue`와 `useCommonCodeLabel.js` 모두 정상 구현되어 있다. 글자 깨짐은 다음 중 하나로 추정:
- DB `data.sql`의 한글 인코딩 문제
- 서버 응답 charset 설정 문제
- 브라우저 캐시/빌드 산출물 불일치

**조치**: Step 1~7 구현 후 런타임에서 확인. `fee_prod_cd` 공통코드가 새로 추가되므로 이 코드에서 글자 깨짐이 재현되는지 확인하면 원인 특정이 가능하다. 만약 기존 `svc_cd`만 깨진다면 `data.sql` 인코딩을, 신규 코드도 깨진다면 서버/빌드 쪽을 의심한다.

---

## 5. 트레이드오프 기록

| 항목 | 선택 | 근거 |
|------|------|------|
| 검색 방식 | `JpaSpecificationExecutor` 기반 | UserPage와 동일 패턴. 기존 `findBy*` 메서드 나열보다 확장성 좋음 |
| SubscriptionMain 페이징 | 네이티브 쿼리 + `countQuery` + `Page<Object[]>` | JOIN 쿼리라 Specification으로 전환하면 복잡도가 급증. 네이티브 유지가 실용적 |
| 샘플 데이터 | 10건 → 30건 | 페이징(10건/페이지) 테스트에 최소 3페이지 필요 |
| 기존 API 함수 유지 여부 | 새 함수 추가 (`*Page`) | 다른 화면에서 전건 조회 API를 사용할 수 있으므로 기존 함수는 보존. 단, Controller가 `Page<T>`를 반환하면 `page`/`size` 없이 호출 시 Spring이 기본 페이징(20건)을 적용하므로, 전건 호출이 필요한 곳이 없다면 기존 함수 제거 가능 |

---

## 6. 테스트 계획

| # | 테스트 항목 | 방법 | 예상 결과 |
|---|-----------|------|----------|
| T1 | 서버 기동 | `./mvnw spring-boot:run` | H2 스키마 생성 + 30건 초기 데이터 정상 로드 |
| T2 | fee_prod_cd 공통코드 | `GET /api/common-codes/fee_prod_cd/details` | 6개 항목 반환 |
| T3 | 가입 관리 페이징 | `GET /api/subscriptions?page=0&size=10` | `content` 10건, `totalElements` 30 |
| T4 | 가입 관리 검색 | `GET /api/subscriptions?type=SVC_CD&keyword=SVC01&page=0&size=10` | 해당 서비스코드 가입만 반환 |
| T5 | 대표가입 관리 페이징 | `GET /api/subscription-main?searchType=가입ID&keyword=SUBS&page=0&size=10` | 페이징 응답 + `subsNm` 필드 포함 |
| T6 | 특수가입 관리 페이징 | `GET /api/special-subscriptions?page=0&size=10` | 페이징 응답 |
| T7 | 콤보 글자 깨짐 | 브라우저에서 가입 관리 폼의 서비스/요금상품 콤보 확인 | 한글 정상 표시 |
| T8 | 그리드 "코드" 레이블 | 특수가입/과금기준 화면에서 그리드 헤더·폼 레이블 확인 | "서비스"로 표시 (코드 없음) |
| T9 | 대표가입 가입명 | 대표가입 관리 목록에서 가입명 컬럼 확인 | "가입자1" 등 정상 표시 |

---

## 7. 롤백 방안

- 모든 변경은 단일 Git 커밋으로 관리.
- 롤백: `git revert <commit-hash>` 1회로 전체 원복.
- H2 인메모리 DB이므로 서버 재시작만으로 스키마/데이터 원복 가능.
- 만약 단계별 커밋이 필요하면, Step 1~2 (DB+Entity), Step 3~5 (Backend), Step 6~7 (Frontend) 3개 커밋으로 분리 가능.

---
## Summary (다음 단계 전달용)
- **Critic 반영**: (1) BillStdPage 그리드 헤더 "서비스코드"→"서비스" 변경 명시 추가 (2) SubscriptionServiceImpl Specification에 `SUBS_NM` 검색 분기 추가
- **구현 순서**: (1) DB 스키마+공통코드 (2) Entity/DTO 필드 변경 (3) Repository 변경 (4) Service/Controller 페이징 (5) DataInitializer (6) 프론트엔드 API (7) 프론트엔드 화면 4개 (8) 콤보 런타임 확인
- **변경 파일**: `schema.sql`, `data.sql`, `Subscription.java`, `SubscriptionRequestDto`, `SubscriptionResponseDto`, `SubscriptionRepository`, `SubscriptionService`, `SubscriptionServiceImpl`, `SubscriptionController`, `SubscriptionDataInitializer`, `SubscriptionMainListResponseDto`, `SubscriptionMainRepository`, `SubscriptionMainService`, `SubscriptionMainServiceImpl`, `SubscriptionMainController`, `SpecialSubscriptionRepository`, `SpecialSubscriptionService`, `SpecialSubscriptionServiceImpl`, `SpecialSubscriptionController`, `subscriptionApi.js`, `subscriptionMainApi.js`, `specialSubscriptionApi.js`, `SubscriptionPage.vue`, `SubscriptionMainPage.vue`, `SpecialSubscriptionPage.vue`, `BillStdPage.vue`
- **핵심 주의사항**: (1) SubscriptionMainRepository 네이티브 쿼리의 SELECT 컬럼 인덱스 시프트(5→6컬럼) 반드시 ServiceImpl 매핑과 동기화 (2) `fee_prod_cd` 공통코드 헤더+상세 6건 누락 없이 추가 (3) DataInitializer 30건으로 증설하여 페이징 테스트 가능하게 (4) 콤보 깨짐은 런타임 확인 필수
