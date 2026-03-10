# 03. Implementation Plan - 과금기준 동적필드 관리

## 1. 구현 전략 개요

기존 BillStd 테이블의 서비스별 고정 컬럼 14개를 EAV(Entity-Attribute-Value) 구조로 전환한다. 신규 테이블 2개(Config, FieldValue)를 추가하고, BillStd 엔티티에서 고정 필드를 제거한다. Config 관리 화면을 신규 개발하고, BillStdPage에 동적 폼 렌더링을 추가한다.

**핵심 원칙:**
- DB 재생성 방식 (마이그레이션 불필요)
- SpecialSubscription의 @EmbeddedId 패턴을 3필드 복합 PK로 확장
- eff_start_dt/eff_end_dt는 VARCHAR(8) YYYYMMDD 형식
- BillStd ID 채번을 UUID에서 타임스탬프 기반으로 변경

## 2. 변경 파일 목록

### 2.1 신규 파일

| 계층 | 파일 경로 | 설명 |
|---|---|---|
| Backend | `BillStdFieldConfigId.java` | 복합 PK (svcCd + fieldCd + effStartDt) |
| Backend | `BillStdFieldConfig.java` | Entity |
| Backend | `BillStdFieldConfigRepository.java` | Repository |
| Backend | `BillStdFieldConfigService.java` | Interface |
| Backend | `BillStdFieldConfigServiceImpl.java` | Impl (CRUD + 사용종료 + 삭제차단) |
| Backend | `BillStdFieldConfigController.java` | REST Controller |
| Backend | `BillStdFieldConfigRequestDto.java` | Request DTO |
| Backend | `BillStdFieldConfigResponseDto.java` | Response DTO |
| Backend | `BillStdFieldValueId.java` | 복합 PK (billStdId + fieldCd) |
| Backend | `BillStdFieldValue.java` | Entity |
| Backend | `BillStdFieldValueRepository.java` | Repository |
| Frontend | `pages/BillStdFieldConfigPage.vue` | Config 관리 화면 |
| Frontend | `api/billStdFieldConfigApi.js` | Config API 호출 |

### 2.2 수정 파일

| 계층 | 파일 경로 | 변경 요약 |
|---|---|---|
| DB | `schema.sql` | tb_bill_std 14개 컬럼 제거, tb_bill_std_field_config/value 추가 |
| DB | `data.sql` | Config 초기 데이터 (기존 14개 필드를 서비스별 Config화), 메뉴 등록 |
| Backend | `BillStd.java` | 14개 서비스별 필드 제거 |
| Backend | `BillStdRequestDto.java` | 14개 필드 제거, `Map<String, String> fieldValues` 추가 |
| Backend | `BillStdResponseDto.java` | 14개 필드 제거, `Map<String, String> fieldValues` 추가 |
| Backend | `BillStdServiceImpl.java` | ID 채번 변경, create/update에서 fieldValue 처리, toDto에서 fieldValue 로드 |
| Frontend | `BillStdPage.vue` | 동적 폼 섹션 추가, Config 기반 렌더링, 저장/조회 로직 변경 |
| Frontend | `billStdApi.js` | Config 조회 API 추가 |
| Frontend | `router/index.js` | `/bill-std-field-config` 라우트 추가 |

## 3. 단계별 구현 순서

| 단계 | 내용 | 의존성 |
|---|---|---|
| 1 | DB 스키마 변경 (schema.sql) | 없음 |
| 2 | BillStdFieldConfig 백엔드 (Id/Entity/Repo/Service/Controller/DTO) | 단계 1 |
| 3 | BillStdFieldValue 백엔드 (Id/Entity/Repo) | 단계 1 |
| 4 | BillStd 백엔드 수정 (Entity/DTO/ServiceImpl) | 단계 1, 3 |
| 5 | data.sql 초기 데이터 | 단계 1 |
| 6 | Config 관리 프론트엔드 (Page/Api/Router) | 단계 2 |
| 7 | BillStdPage 동적 폼 프론트엔드 수정 | 단계 4, 6 |

## 4. 단계별 상세

### 4.1 단계 1: DB 스키마 변경

#### schema.sql - tb_bill_std 수정

**Before** (50~70행):
```sql
    /* ── 과금 산정 방식 ──────────────────────────────────────── */
    pwr_met_calc_meth_cd    VARCHAR(10)    NULL,
    uprc_det_meth_cd        VARCHAR(10)    NULL,
    metering_unit_price_amt NUMERIC(18,4)  NULL,
    bill_qty                NUMERIC(18,4)  NULL,

    /* ── PUE ─────────────────────────────────────────────────── */
    pue_det_meth_cd         VARCHAR(10)    NULL,
    pue1_rt                 NUMERIC(18,4)  NULL,
    pue2_rt                 NUMERIC(18,4)  NULL,

    /* ── 할인·손실 ───────────────────────────────────────────── */
    first_dsc_rt            NUMERIC(18,4)  NULL,
    second_dsc_rt           NUMERIC(18,4)  NULL,
    loss_comp_rt            NUMERIC(18,4)  NULL,

    /* ── 약정·정산 ───────────────────────────────────────────── */
    cntrc_cap_kmh           NUMERIC(18,4)  NULL,
    cntrc_amt               NUMERIC(18,2)  NULL,
    dsc_amt                 NUMERIC(18,2)  NULL,
    daily_unit_price        NUMERIC(18,4)  NULL,
```

**After**: 위 14개 컬럼 전체 삭제. tb_bill_std는 Key + 등록/유효제어 + 상태코드 + System Fields만 유지.

#### schema.sql - 신규 테이블 추가

```sql
-- ================================================================
-- 테이블명 : TB_BILL_STD_FIELD_CONFIG (과금기준 필드설정)
-- 설명     : 서비스별 과금기준 동적 필드 정의
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_field_config
(
    /* ── Key (복합 PK) ──────────────────────────────── */
    svc_cd                  VARCHAR(10)    NOT NULL,               -- 서비스코드
    field_cd                VARCHAR(50)    NOT NULL,               -- 필드코드
    eff_start_dt            VARCHAR(8)     NOT NULL,               -- 유효시작일 (YYYYMMDD)

    /* ── 필드 정의 ──────────────────────────────────── */
    field_nm                VARCHAR(100)   NOT NULL,               -- 필드명
    field_type              VARCHAR(10)    NOT NULL,               -- 필드타입 (TEXT/NUMBER/SELECT/DATE)
    required_yn             CHAR(1)        DEFAULT 'N',            -- 필수여부
    sort_order              INTEGER        DEFAULT 0,              -- 정렬순서
    common_code             VARCHAR(50)    NULL,                   -- 공통코드 (SELECT 타입 전용)
    default_value           VARCHAR(200)   NULL,                   -- 기본값
    eff_end_dt              VARCHAR(8)     DEFAULT '99991231',     -- 유효종료일 (YYYYMMDD)

    /* ── System Fields ──────────────────────────────── */
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              TIMESTAMP      NULL,

    CONSTRAINT pk_tb_bill_std_field_config PRIMARY KEY (svc_cd, field_cd, eff_start_dt)
);

CREATE INDEX IF NOT EXISTS idx_tb_bill_std_field_config_svc
    ON tb_bill_std_field_config (svc_cd, eff_start_dt, eff_end_dt);

-- ================================================================
-- 테이블명 : TB_BILL_STD_FIELD_VALUE (과금기준 필드값)
-- 설명     : 과금기준별 동적 필드 값 저장 (EAV)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_bill_std_field_value
(
    /* ── Key (복합 PK) ──────────────────────────────── */
    bill_std_id             VARCHAR(20)    NOT NULL,               -- 과금기준ID
    field_cd                VARCHAR(50)    NOT NULL,               -- 필드코드

    /* ── 값 ─────────────────────────────────────────── */
    field_value             VARCHAR(500)   NULL,                   -- 필드값

    /* ── System Fields ──────────────────────────────── */
    created_by              VARCHAR(50)    NOT NULL,
    created_dt              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,
    updated_dt              TIMESTAMP      NULL,

    CONSTRAINT pk_tb_bill_std_field_value PRIMARY KEY (bill_std_id, field_cd),
    CONSTRAINT fk_tb_bill_std_field_value_bs FOREIGN KEY (bill_std_id) REFERENCES tb_bill_std (bill_std_id)
);
```

### 4.2 단계 2: BillStdFieldConfig 백엔드

#### BillStdFieldConfigId.java (신규)

SpecialSubscriptionId 패턴을 3필드로 확장:

```java
@Embeddable
public class BillStdFieldConfigId implements Serializable {
    private String svcCd;
    private String fieldCd;
    private String effStartDt;

    public BillStdFieldConfigId() {}
    public BillStdFieldConfigId(String svcCd, String fieldCd, String effStartDt) {
        this.svcCd = svcCd;
        this.fieldCd = fieldCd;
        this.effStartDt = effStartDt;
    }

    // getter/setter, equals/hashCode (Objects.hash(svcCd, fieldCd, effStartDt))
}
```

#### BillStdFieldConfig.java (신규)

```java
@Entity
@Table(name = "tb_bill_std_field_config")
public class BillStdFieldConfig {

    @EmbeddedId
    private BillStdFieldConfigId id;

    @Column(name = "field_nm", length = 100, nullable = false)
    private String fieldNm;

    @Column(name = "field_type", length = 10, nullable = false)
    private String fieldType;  // TEXT, NUMBER, SELECT, DATE

    @Column(name = "required_yn", length = 1)
    private String requiredYn;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "common_code", length = 50)
    private String commonCode;  // SELECT 타입 전용

    @Column(name = "default_value", length = 200)
    private String defaultValue;

    @Column(name = "eff_end_dt", length = 8)
    private String effEndDt;

    // System Fields (4개) + getter/setter
}
```

#### BillStdFieldConfigRepository.java (신규)

```java
public interface BillStdFieldConfigRepository
        extends JpaRepository<BillStdFieldConfig, BillStdFieldConfigId>,
                JpaSpecificationExecutor<BillStdFieldConfig> {

    // 서비스코드별 현재 유효한 Config 목록 (동적 폼 렌더링용)
    @Query("SELECT c FROM BillStdFieldConfig c WHERE c.id.svcCd = :svcCd " +
           "AND c.id.effStartDt <= :today AND c.effEndDt >= :today " +
           "ORDER BY c.sortOrder")
    List<BillStdFieldConfig> findEffectiveBySvcCd(
        @Param("svcCd") String svcCd, @Param("today") String today);
}
```

#### BillStdFieldConfigController.java (신규)

SpecialSubscriptionController 패턴 기반, URL: `/api/bill-std-field-configs`

```java
@RestController
@RequestMapping("/api/bill-std-field-configs")
public class BillStdFieldConfigController {

    @GetMapping
    public List<BillStdFieldConfigResponseDto> getAll(
            @RequestParam(required = false) String svcCd,
            @RequestParam(required = false) String fieldCd) { ... }

    @GetMapping("/{svcCd}/{fieldCd}/{effStartDt}")
    public ResponseEntity<BillStdFieldConfigResponseDto> getById(...) { ... }

    @GetMapping("/effective/{svcCd}")
    public List<BillStdFieldConfigResponseDto> getEffective(
            @PathVariable String svcCd) { ... }
    // 현재일자 기준 유효 Config 목록 반환 (BillStdPage 동적 폼용)

    @PostMapping
    public ResponseEntity<BillStdFieldConfigResponseDto> create(...) { ... }

    @PutMapping("/{svcCd}/{fieldCd}/{effStartDt}")
    public ResponseEntity<BillStdFieldConfigResponseDto> update(...) { ... }

    @DeleteMapping("/{svcCd}/{fieldCd}/{effStartDt}")
    public ResponseEntity<Void> delete(...) { ... }
    // 사용 중(field_value 존재) 시 409 반환

    @PutMapping("/{svcCd}/{fieldCd}/{effStartDt}/expire")
    public ResponseEntity<BillStdFieldConfigResponseDto> expire(...) { ... }
    // eff_end_dt를 현재일자(YYYYMMDD)로 설정
}
```

#### BillStdFieldConfigServiceImpl.java 핵심 로직

**삭제 차단 로직:**
```java
@Override
public void delete(String svcCd, String fieldCd, String effStartDt) {
    findOrThrow(svcCd, fieldCd, effStartDt);
    long usageCount = fieldValueRepository.countByIdFieldCd(fieldCd);
    if (usageCount > 0) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "사용 중인 필드는 삭제할 수 없습니다. 사용종료 기능을 이용해 주세요.");
    }
    repository.deleteById(new BillStdFieldConfigId(svcCd, fieldCd, effStartDt));
}
```

**사용종료 로직:**
```java
@Override
@Transactional
public BillStdFieldConfigResponseDto expire(String svcCd, String fieldCd, String effStartDt) {
    BillStdFieldConfig entity = findOrThrow(svcCd, fieldCd, effStartDt);
    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    entity.setEffEndDt(today);
    entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
    entity.setUpdatedDt(LocalDateTime.now());
    return toDto(repository.save(entity));
}
```

### 4.3 단계 3: BillStdFieldValue 백엔드

#### BillStdFieldValueId.java (신규)

```java
@Embeddable
public class BillStdFieldValueId implements Serializable {
    private String billStdId;
    private String fieldCd;

    // 기본생성자, 파라미터 생성자, getter/setter, equals/hashCode
}
```

#### BillStdFieldValue.java (신규)

```java
@Entity
@Table(name = "tb_bill_std_field_value")
public class BillStdFieldValue {

    @EmbeddedId
    private BillStdFieldValueId id;

    @Column(name = "field_value", length = 500)
    private String fieldValue;

    // System Fields (4개) + getter/setter
}
```

#### BillStdFieldValueRepository.java (신규)

```java
public interface BillStdFieldValueRepository
        extends JpaRepository<BillStdFieldValue, BillStdFieldValueId> {

    List<BillStdFieldValue> findByIdBillStdId(String billStdId);

    long countByIdFieldCd(String fieldCd);

    void deleteByIdBillStdId(String billStdId);
}
```

### 4.4 단계 4: BillStd 백엔드 수정

#### BillStd.java 수정

**Before** (46~91행):
```java
    /* ── 과금 산정 방식 ──────────────────────────────────────── */
    @Column(name = "pwr_met_calc_meth_cd", length = 10)
    private String pwrMetCalcMethCd;
    // ... 14개 필드 선언 + getter/setter 28개 메서드
```

**After**: 위 14개 필드 및 관련 getter/setter 전체 삭제. Entity는 Key + 등록/유효제어 + 상태코드 + System Fields만 유지.

#### BillStdRequestDto.java 수정

**Before** (24~44행):
```java
    /* ── 과금 산정 방식 ──────────────────────────────────────── */
    private String     pwrMetCalcMethCd;
    private String     uprcDetMethCd;
    private BigDecimal meteringUnitPriceAmt;
    // ... 14개 필드
```

**After**:
```java
    // 14개 서비스별 필드 제거 후, 동적 필드값 Map 추가
    private Map<String, String> fieldValues;  // key=fieldCd, value=fieldValue

    public Map<String, String> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, String> fieldValues) { this.fieldValues = fieldValues; }
```

#### BillStdResponseDto.java 수정

**Before** (23~43행): 14개 서비스별 필드

**After**:
```java
    // 14개 서비스별 필드 제거 후, 동적 필드값 Map 추가
    private Map<String, String> fieldValues;  // key=fieldCd, value=fieldValue

    public Map<String, String> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, String> fieldValues) { this.fieldValues = fieldValues; }
```

#### BillStdServiceImpl.java 수정

**ID 채번 변경:**

Before (155~157행):
```java
    private String generateId() {
        return "BS" + UUID.randomUUID().toString().replace("-", "").substring(0, 17);
    }
```

After:
```java
    private static final DateTimeFormatter ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private String generateId() {
        return "BS" + LocalDateTime.now().format(ID_FORMATTER);
    }
```

**create 메서드 수정:**

Before (68~96행):
```java
        BillStd entity = new BillStd();
        entity.setBillStdId(generateId());
        entity.setSubsId(dto.getSubsId());
        // ... 공통 필드 설정
        entity.setPwrMetCalcMethCd(dto.getPwrMetCalcMethCd());
        entity.setUprcDetMethCd(dto.getUprcDetMethCd());
        // ... 14개 서비스별 필드 매핑
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toDto(repository.save(entity));
```

After:
```java
        BillStd entity = new BillStd();
        String billStdId = generateId();
        entity.setBillStdId(billStdId);
        entity.setSubsId(dto.getSubsId());
        // ... 공통 필드 설정 (billStdRegDt, svcCd, lastEffYn, effStartDt, effEndDt, 상태코드)
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        BillStd saved = repository.save(entity);

        // 동적 필드값 저장
        saveFieldValues(billStdId, dto.getFieldValues(), SecurityUtils.getCurrentUserId());

        return toDto(saved);
```

**saveFieldValues 헬퍼 메서드 (신규):**
```java
    private void saveFieldValues(String billStdId, Map<String, String> fieldValues, String userId) {
        if (fieldValues == null || fieldValues.isEmpty()) return;
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            BillStdFieldValue fv = new BillStdFieldValue();
            fv.setId(new BillStdFieldValueId(billStdId, entry.getKey()));
            fv.setFieldValue(entry.getValue());
            fv.setCreatedBy(userId);
            fv.setCreatedDt(LocalDateTime.now());
            fieldValueRepository.save(fv);
        }
    }
```

**update 메서드 수정:**

Before (102~131행): 14개 서비스별 필드 개별 매핑

After:
```java
        BillStd entity = findOrThrow(billStdId);
        // 공통 필드만 업데이트 (billStdRegDt, svcCd, lastEffYn, effStartDt, effEndDt, 상태코드)
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        BillStd saved = repository.save(entity);

        // 기존 필드값 삭제 후 재저장
        fieldValueRepository.deleteByIdBillStdId(billStdId);
        saveFieldValues(billStdId, dto.getFieldValues(), SecurityUtils.getCurrentUserId());

        return toDto(saved);
```

**toDto 메서드 수정:**

Before (159~189행): 14개 필드 개별 매핑

After:
```java
    private BillStdResponseDto toDto(BillStd e) {
        BillStdResponseDto dto = new BillStdResponseDto();
        dto.setBillStdId(e.getBillStdId());
        dto.setSubsId(e.getSubsId());
        dto.setBillStdRegDt(e.getBillStdRegDt());
        dto.setSvcCd(e.getSvcCd());
        dto.setLastEffYn(e.getLastEffYn());
        dto.setEffStartDt(e.getEffStartDt());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setStdRegStatCd(e.getStdRegStatCd());
        dto.setBillStdStatCd(e.getBillStdStatCd());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());

        // 동적 필드값 로드
        List<BillStdFieldValue> values = fieldValueRepository.findByIdBillStdId(e.getBillStdId());
        Map<String, String> fieldValues = new LinkedHashMap<>();
        for (BillStdFieldValue fv : values) {
            fieldValues.put(fv.getId().getFieldCd(), fv.getFieldValue());
        }
        dto.setFieldValues(fieldValues);

        return dto;
    }
```

**생성자 수정 (의존성 추가):**

Before:
```java
    private final BillStdRepository repository;

    public BillStdServiceImpl(BillStdRepository repository) {
        this.repository = repository;
    }
```

After:
```java
    private final BillStdRepository repository;
    private final BillStdFieldValueRepository fieldValueRepository;

    public BillStdServiceImpl(BillStdRepository repository,
                              BillStdFieldValueRepository fieldValueRepository) {
        this.repository = repository;
        this.fieldValueRepository = fieldValueRepository;
    }
```

### 4.5 단계 5: data.sql 초기 데이터

기존 14개 고정 필드를 서비스별 Config로 등록. SVC01(전력) 서비스 기준으로 전체 14개 필드를 Config화하고, SVC02(냉방)/SVC03(통신)은 해당 서비스에 적용되는 필드만 등록.

```sql
-- ================================================================
-- 과금기준 필드설정 초기 데이터 (SVC01: 전력 - 14개 전체)
-- ================================================================
MERGE INTO tb_bill_std_field_config (svc_cd, field_cd, eff_start_dt, field_nm, field_type, required_yn, sort_order, common_code, default_value, eff_end_dt, created_by, created_dt) KEY (svc_cd, field_cd, eff_start_dt) VALUES
('SVC01', 'pwr_met_calc_meth_cd', '20000101', '전력종량계산방식', 'SELECT', 'N', 1, 'pwr_met_calc_meth_cd', NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_bill_std_field_config (...) KEY (...) VALUES
('SVC01', 'uprc_det_meth_cd', '20000101', '단가결정방식', 'SELECT', 'N', 2, 'uprc_det_meth_cd', NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_bill_std_field_config (...) KEY (...) VALUES
('SVC01', 'metering_unit_price_amt', '20000101', '종량단가', 'NUMBER', 'N', 3, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP);
MERGE INTO tb_bill_std_field_config (...) KEY (...) VALUES
('SVC01', 'bill_qty', '20000101', '과금량갯수', 'NUMBER', 'N', 4, NULL, NULL, '99991231', 'SYSTEM', CURRENT_TIMESTAMP);
-- ... pue_det_meth_cd(SELECT), pue1_rt(NUMBER), pue2_rt(NUMBER),
--     first_dsc_rt(NUMBER), second_dsc_rt(NUMBER), loss_comp_rt(NUMBER),
--     cntrc_cap_kmh(NUMBER), cntrc_amt(NUMBER), dsc_amt(NUMBER), daily_unit_price(NUMBER)
-- sort_order: 1~14 순서 배정

-- SVC02(냉방): 관련 필드만 (cntrc_cap_kmh, cntrc_amt, metering_unit_price_amt 등)
-- SVC03(통신): 관련 필드만 (cntrc_amt, bill_qty 등)
```

메뉴 데이터 (tb_menu에 INSERT):
```sql
MERGE INTO tb_menu (menu_id, menu_nm, menu_url, parent_menu_id, sort_order, use_yn, menu_level, created_by, created_dt) KEY (menu_id) VALUES
('MENU_BSFC', '과금기준필드설정', '/bill-std-field-config', 'MENU_GRP_BILL', 2, 'Y', 2, 'SYSTEM', CURRENT_TIMESTAMP);
```
(상위 메뉴 ID는 기존 메뉴 구조 확인 후 결정)

### 4.6 단계 6: Config 관리 프론트엔드

#### billStdFieldConfigApi.js (신규)

```js
import apiClient from './apiClient'

export const getFieldConfigs = (params) =>
  apiClient.get('/bill-std-field-configs', { params }).then(res => res.data)

export const getFieldConfig = (svcCd, fieldCd, effStartDt) =>
  apiClient.get(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}`).then(res => res.data)

export const getEffectiveConfigs = (svcCd) =>
  apiClient.get(`/bill-std-field-configs/effective/${svcCd}`).then(res => res.data)

export const createFieldConfig = (data) =>
  apiClient.post('/bill-std-field-configs', data).then(res => res.data)

export const updateFieldConfig = (svcCd, fieldCd, effStartDt, data) =>
  apiClient.put(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}`, data).then(res => res.data)

export const deleteFieldConfig = (svcCd, fieldCd, effStartDt) =>
  apiClient.delete(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}`)

export const expireFieldConfig = (svcCd, fieldCd, effStartDt) =>
  apiClient.put(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}/expire`).then(res => res.data)
```

#### BillStdFieldConfigPage.vue (신규)

표준 4단 레이아웃: 조회 -> 그리드(DataGrid) -> 입력폼 -> FloatingActionBar

- **조회영역**: CommonCodeSelect(svc_cd) + 필드코드 텍스트 입력 + 조회 버튼
- **그리드**: DataGrid (전건 목록, 페이징 없음). 컬럼: svcCd, fieldCd, fieldNm, fieldType, requiredYn, sortOrder, effStartDt, effEndDt
- **입력폼**: grid-cols-3 레이아웃. svcCd(CommonCodeSelect), fieldCd(TEXT), fieldNm(TEXT), fieldType(SELECT: TEXT/NUMBER/SELECT/DATE), requiredYn(SELECT: Y/N), sortOrder(NUMBER), commonCode(TEXT, fieldType=SELECT일 때만 활성), defaultValue(TEXT), effStartDt(TEXT YYYYMMDD), effEndDt(TEXT YYYYMMDD readonly)
- **액션바**: 등록, 변경, 사용종료, 삭제

#### router/index.js 수정

**Before** (10행 이후):
```js
  { path: '/bill-std', name: 'BillStd', component: () => import('../pages/BillStdPage.vue') },
```

**After** (추가):
```js
  { path: '/bill-std', name: 'BillStd', component: () => import('../pages/BillStdPage.vue') },
  { path: '/bill-std-field-config', name: 'BillStdFieldConfig', component: () => import('../pages/BillStdFieldConfigPage.vue') },
```

### 4.7 단계 7: BillStdPage 동적 폼 수정

#### BillStdPage.vue 수정

**EMPTY_FORM 변경:**

Before:
```js
const EMPTY_FORM = {
  billStdId: '', subsId: '', svcCd: '', lastEffYn: 'Y',
  effStartDt: '', effEndDt: '', meteringUnitPriceAmt: '',
  cntrcCapKmh: '', cntrcAmt: '',
}
```

After:
```js
const EMPTY_FORM = {
  billStdId: '', subsId: '', svcCd: '', lastEffYn: 'Y',
  effStartDt: '', effEndDt: '',
  stdRegStatCd: '', billStdStatCd: '',
  fieldValues: {},
}
```

**동적 폼 섹션 추가 (template):**

기존 "과금기준 정보" 카드 아래에 "동적 필드" 카드 추가:

```html
<!-- 동적 필드 섹션 -->
<div v-if="fieldConfigs.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
  <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">서비스별 과금항목</h3>
  <div class="grid grid-cols-3 gap-x-4 gap-y-3">
    <div v-for="config in fieldConfigs" :key="config.fieldCd">
      <label class="block text-xs text-gray-500 mb-1">
        {{ config.fieldNm }}
        <span v-if="config.requiredYn === 'Y'" class="text-blue-400">*</span>
      </label>
      <!-- TEXT/NUMBER -->
      <input v-if="config.fieldType === 'TEXT' || config.fieldType === 'NUMBER'"
        v-model="formData.fieldValues[config.fieldCd]"
        :type="config.fieldType === 'NUMBER' ? 'number' : 'text'"
        class="w-full h-8 px-2 border border-gray-300 rounded text-sm ..." />
      <!-- SELECT (공통코드 연동) -->
      <CommonCodeSelect v-else-if="config.fieldType === 'SELECT'"
        :common-code="config.commonCode"
        v-model="formData.fieldValues[config.fieldCd]" />
      <!-- DATE -->
      <input v-else-if="config.fieldType === 'DATE'"
        v-model="formData.fieldValues[config.fieldCd]" type="text"
        placeholder="YYYYMMDD"
        class="w-full h-8 px-2 border border-gray-300 rounded text-sm ..." />
    </div>
  </div>
</div>
```

**Config 조회 로직 추가 (script):**

```js
import { getEffectiveConfigs } from '../api/billStdFieldConfigApi'

const fieldConfigs = ref([])

// 가입 조회 성공 후 svcCd 기반으로 Config 로드
const loadFieldConfigs = async (svcCd) => {
  if (!svcCd) { fieldConfigs.value = []; return }
  try {
    fieldConfigs.value = await getEffectiveConfigs(svcCd)
  } catch { fieldConfigs.value = [] }
}
```

**toFormData 수정:**

Before:
```js
const toFormData = (dto) => {
  Object.keys(EMPTY_FORM).forEach(key => {
    formData[key] = dto[key] != null ? String(dto[key]) : ''
  })
}
```

After:
```js
const toFormData = (dto) => {
  formData.billStdId = dto.billStdId ?? ''
  formData.subsId = dto.subsId ?? ''
  formData.svcCd = dto.svcCd ?? ''
  formData.lastEffYn = dto.lastEffYn ?? 'Y'
  formData.effStartDt = dto.effStartDt ?? ''
  formData.effEndDt = dto.effEndDt ?? ''
  formData.stdRegStatCd = dto.stdRegStatCd ?? ''
  formData.billStdStatCd = dto.billStdStatCd ?? ''
  formData.fieldValues = dto.fieldValues ?? {}
}
```

**toRequestDto 수정:**

Before:
```js
const toRequestDto = () => ({
  ...formData,
  meteringUnitPriceAmt: formData.meteringUnitPriceAmt ? parseFloat(formData.meteringUnitPriceAmt) : null,
  cntrcCapKmh: formData.cntrcCapKmh ? parseFloat(formData.cntrcCapKmh) : null,
  cntrcAmt: formData.cntrcAmt ? parseFloat(formData.cntrcAmt) : null,
  createdBy: auth.user?.userId ?? 'SYSTEM',
})
```

After:
```js
const toRequestDto = () => ({
  subsId: formData.subsId,
  svcCd: formData.svcCd,
  lastEffYn: formData.lastEffYn,
  effStartDt: formData.effStartDt,
  effEndDt: formData.effEndDt,
  stdRegStatCd: formData.stdRegStatCd,
  billStdStatCd: formData.billStdStatCd,
  fieldValues: { ...formData.fieldValues },
  createdBy: auth.user?.userId ?? 'SYSTEM',
})
```

**handleSearch 수정 (Config 로드 연동):**

조회 성공 후 `loadFieldConfigs(found.svcCd)` 호출 추가.

## 5. 트레이드오프 기록

| 결정 | 채택 안 | 대안 | 근거 |
|---|---|---|---|
| 필드값 저장 타입 | VARCHAR(500) 단일 타입 | 타입별 컬럼 (num_value, str_value 등) | EAV 단순성 우선. 타입 검증은 프론트엔드 + Config의 field_type으로 처리 |
| Config 삭제 정책 | 사용 중 차단 + 사용종료 | 논리삭제 플래그 | 유효기간 기반 관리가 이미 있으므로 별도 삭제 플래그 불필요 |
| BillStd 저장 시 fieldValue | delete-all + re-insert | merge (개별 upsert) | 전건 교체가 단순하고 일관적. 필드값 건수가 소량(~14건)이므로 성능 영향 무시 가능 |
| Config 조회 API | 전건 목록 (페이징 없음) | 서버 페이징 | 설정 데이터이므로 건수 한정적. frontend-rules.md의 "전건 목록" 유형 적용 |
| BillStd의 eff_start_dt/eff_end_dt 타입 | 현행 유지 (TIMESTAMP) | VARCHAR(8) 전환 | 요구사항에 BillStd 자체의 유효일자 타입 변경은 포함되지 않음. Config 테이블만 VARCHAR(8) 적용 |

## 6. 테스트 계획

기존 테스트가 없으므로 수동 검증 중심:

### 6.1 Config 관리 화면
- [ ] Config 등록 -> DB 확인 (복합 PK 3개 필드 정상 저장)
- [ ] Config 조회 -> 서비스코드별 필터링 동작
- [ ] Config 변경 -> PK 불변, 나머지 필드 업데이트
- [ ] Config 삭제 (미사용) -> 정상 삭제
- [ ] Config 삭제 (사용 중) -> 409 에러 + 에러 메시지 표시
- [ ] Config 사용종료 -> eff_end_dt가 현재일자로 설정

### 6.2 과금기준 관리 화면
- [ ] 가입 조회 -> svcCd 기반 유효 Config 목록 로드 -> 동적 폼 렌더링
- [ ] field_type=SELECT -> CommonCodeSelect 드롭다운 정상 표시
- [ ] field_type=NUMBER -> 숫자 입력 필드 렌더링
- [ ] 과금기준 저장 -> tb_bill_std + tb_bill_std_field_value 동시 저장
- [ ] 과금기준 조회 -> fieldValues Map 정상 로드 및 표시
- [ ] 과금기준 변경 -> fieldValues delete-all + re-insert 정상 동작
- [ ] BillStd ID 채번 -> "BS" + 17자리 타임스탬프 형식 확인

### 6.3 회귀 테스트
- [ ] 과금기준 이력 처리 (lastEffYn 전환) 정상 동작
- [ ] 과금기준 삭제 정상 동작
- [ ] 기존 다른 화면 (가입, 특수가입 등) 영향 없음

## 7. 롤백 방안

1. **Git 기반 롤백**: 전체 변경사항이 단일 커밋으로 관리되므로 `git revert`로 복원
2. **DB 롤백**: H2 파일 DB 삭제 후 기존 schema.sql/data.sql로 재생성 (vibedb.mv.db, vibedb.lock.db 삭제)
3. **부분 롤백**: 단계별 독립적이므로 프론트엔드만 롤백하거나 Config 관련만 롤백 가능
4. **영향 범위**: BillStd 관련 코드만 변경하므로 다른 도메인(User, Subscription, QnA 등)에 영향 없음

---
## Summary (다음 단계 전달용)
- **구현 순서**: (1) DB 스키마 (2) FieldConfig 백엔드 (3) FieldValue 백엔드 (4) BillStd 백엔드 수정 (5) data.sql 초기데이터 (6) Config 프론트엔드 (7) BillStdPage 동적폼
- **신규 파일**: BillStdFieldConfigId/Entity/Repo/Service/ServiceImpl/Controller/RequestDto/ResponseDto, BillStdFieldValueId/Entity/Repo, BillStdFieldConfigPage.vue, billStdFieldConfigApi.js (총 13개)
- **수정 파일**: schema.sql, data.sql, BillStd.java, BillStdRequestDto.java, BillStdResponseDto.java, BillStdServiceImpl.java, BillStdPage.vue, billStdApi.js, router/index.js (총 9개)
- **핵심 설계 결정**: (1) 3필드 복합PK(@EmbeddedId) - SpecialSubscriptionId 패턴 확장 (2) fieldValues는 Map<String,String>으로 DTO 전달 (3) BillStd 저장 시 delete-all+re-insert (4) Config 삭제 시 사용중 409 차단+사용종료 기능 (5) ID 채번 UUID->타임스탬프 (6) eff_start_dt/eff_end_dt VARCHAR(8) YYYYMMDD
