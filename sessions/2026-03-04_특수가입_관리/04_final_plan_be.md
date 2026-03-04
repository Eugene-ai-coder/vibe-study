# 3단계: 백엔드 구현 계획서 (BE)

## 1. 구현 전략 개요

특수가입(SpecialSubscription) CRUD를 기존 CommonDtlCode의 `@EmbeddedId` 복합 PK 패턴과 BillStd의 Service 레이어 패턴을 조합하여 구현한다. 전건 목록 조회(페이징 없음) + 조건부 필터링 방식.

**핵심 설계 결정:**
- 복합 PK: `@EmbeddedId`(SpecialSubscriptionId) — CommonDtlCode 패턴 준수
- ID 자동생성 없음: PK 두 필드 모두 사용자 입력
- 전건 조회: `List<T>` 반환 (BillStd findAll 패턴)
- 시스템 필드: `SecurityUtils.getCurrentUserId()` 기반 자동 설정

---

## 2. 변경 파일 목록

| # | 파일 | 구분 | 역할 |
|---|---|---|---|
| 1 | `SpecialSubscriptionId.java` | 신규 | `@Embeddable` 복합키 클래스 |
| 2 | `SpecialSubscription.java` | 신규 | `@Entity` |
| 3 | `SpecialSubscriptionRepository.java` | 신규 | JpaRepository |
| 4 | `SpecialSubscriptionRequestDto.java` | 신규 | 요청 DTO |
| 5 | `SpecialSubscriptionResponseDto.java` | 신규 | 응답 DTO |
| 6 | `SpecialSubscriptionService.java` | 신규 | Service Interface |
| 7 | `SpecialSubscriptionServiceImpl.java` | 신규 | Service 구현 |
| 8 | `SpecialSubscriptionController.java` | 신규 | REST Controller |
| 9 | `schema.sql` | 수정 | `tb_special_subscription` DDL 추가 |

---

## 3. 단계별 구현 순서

### Step 1: 복합키 클래스 — `SpecialSubscriptionId.java`

**역할:** `subs_bill_std_id` + `eff_sta_dt` 복합 PK 값 객체

**참조 패턴:** `CommonDtlCodeId.java`

```java
package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SpecialSubscriptionId implements Serializable {
    private String subsBillStdId;
    private String effStaDt;

    public SpecialSubscriptionId() {}
    public SpecialSubscriptionId(String subsBillStdId, String effStaDt) {
        this.subsBillStdId = subsBillStdId;
        this.effStaDt = effStaDt;
    }

    public String getSubsBillStdId() { return subsBillStdId; }
    public void setSubsBillStdId(String subsBillStdId) { this.subsBillStdId = subsBillStdId; }
    public String getEffStaDt() { return effStaDt; }
    public void setEffStaDt(String effStaDt) { this.effStaDt = effStaDt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecialSubscriptionId)) return false;
        SpecialSubscriptionId that = (SpecialSubscriptionId) o;
        return Objects.equals(subsBillStdId, that.subsBillStdId) && Objects.equals(effStaDt, that.effStaDt);
    }

    @Override
    public int hashCode() { return Objects.hash(subsBillStdId, effStaDt); }
}
```

---

### Step 2: Entity — `SpecialSubscription.java`

**역할:** `tb_special_subscription` 테이블 매핑

**참조 패턴:** `CommonDtlCode.java` (`@EmbeddedId` 사용) + BillStd 필드 구조

```java
package com.example.vibestudy;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_special_subscription")
public class SpecialSubscription {

    @EmbeddedId
    private SpecialSubscriptionId id;

    @Column(name = "subs_id", length = 20, nullable = false)
    private String subsId;

    @Column(name = "svc_cd", length = 10)
    private String svcCd;

    @Column(name = "eff_end_dt", length = 8)
    private String effEndDt;

    @Column(name = "last_eff_yn", length = 1)
    private String lastEffYn;

    @Column(name = "stat_cd", length = 10)
    private String statCd;

    @Column(name = "cntrc_cap_kmh", precision = 18, scale = 4)
    private BigDecimal cntrcCapKmh;

    @Column(name = "cntrc_amt", precision = 18, scale = 2)
    private BigDecimal cntrcAmt;

    @Column(name = "dsc_rt", precision = 18, scale = 4)
    private BigDecimal dscRt;

    @Column(name = "rmk", length = 500)
    private String rmk;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // getter/setter 전체 필드 선언 (Lombok 미사용)
    public SpecialSubscriptionId getId() { return id; }
    public void setId(SpecialSubscriptionId id) { this.id = id; }
    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }
    public String getSvcCd() { return svcCd; }
    public void setSvcCd(String svcCd) { this.svcCd = svcCd; }
    public String getEffEndDt() { return effEndDt; }
    public void setEffEndDt(String effEndDt) { this.effEndDt = effEndDt; }
    public String getLastEffYn() { return lastEffYn; }
    public void setLastEffYn(String lastEffYn) { this.lastEffYn = lastEffYn; }
    public String getStatCd() { return statCd; }
    public void setStatCd(String statCd) { this.statCd = statCd; }
    public BigDecimal getCntrcCapKmh() { return cntrcCapKmh; }
    public void setCntrcCapKmh(BigDecimal cntrcCapKmh) { this.cntrcCapKmh = cntrcCapKmh; }
    public BigDecimal getCntrcAmt() { return cntrcAmt; }
    public void setCntrcAmt(BigDecimal cntrcAmt) { this.cntrcAmt = cntrcAmt; }
    public BigDecimal getDscRt() { return dscRt; }
    public void setDscRt(BigDecimal dscRt) { this.dscRt = dscRt; }
    public String getRmk() { return rmk; }
    public void setRmk(String rmk) { this.rmk = rmk; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}
```

---

### Step 3: Repository — `SpecialSubscriptionRepository.java`

**역할:** JPA CRUD + 조건부 필터 쿼리

**참조 패턴:** `CommonDtlCodeRepository.java`

```java
package com.example.vibestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpecialSubscriptionRepository extends JpaRepository<SpecialSubscription, SpecialSubscriptionId> {
    List<SpecialSubscription> findByIdSubsBillStdIdContaining(String subsBillStdId);
    List<SpecialSubscription> findBySubsIdContaining(String subsId);
    List<SpecialSubscription> findByIdSubsBillStdIdContainingAndSubsIdContaining(String subsBillStdId, String subsId);
}
```

---

### Step 4: RequestDto — `SpecialSubscriptionRequestDto.java`

**역할:** 등록/수정 요청 데이터 전달

**규칙 준수:** `createdBy` 단일 필드 원칙

```java
package com.example.vibestudy;

import java.math.BigDecimal;

public class SpecialSubscriptionRequestDto {
    private String subsBillStdId;
    private String effStaDt;
    private String subsId;
    private String svcCd;
    private String effEndDt;
    private String lastEffYn;
    private String statCd;
    private BigDecimal cntrcCapKmh;
    private BigDecimal cntrcAmt;
    private BigDecimal dscRt;
    private String rmk;
    private String createdBy;  // INSERT → created_by, UPDATE → updated_by

    // getter/setter 전체 선언
}
```

---

### Step 5: ResponseDto — `SpecialSubscriptionResponseDto.java`

**역할:** 조회 응답 데이터 (시스템 필드 포함)

```java
package com.example.vibestudy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SpecialSubscriptionResponseDto {
    private String subsBillStdId;
    private String effStaDt;
    private String subsId;
    private String svcCd;
    private String effEndDt;
    private String lastEffYn;
    private String statCd;
    private BigDecimal cntrcCapKmh;
    private BigDecimal cntrcAmt;
    private BigDecimal dscRt;
    private String rmk;
    private String createdBy;
    private LocalDateTime createdDt;
    private String updatedBy;
    private LocalDateTime updatedDt;

    // getter/setter 전체 선언
}
```

---

### Step 6: Service Interface — `SpecialSubscriptionService.java`

**참조 패턴:** `CommonCodeService.java`

```java
package com.example.vibestudy;

import java.util.List;

public interface SpecialSubscriptionService {
    List<SpecialSubscriptionResponseDto> findAll(String subsBillStdId, String subsId);
    SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStaDt);
    SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto);
    SpecialSubscriptionResponseDto update(String subsBillStdId, String effStaDt, SpecialSubscriptionRequestDto dto);
    void delete(String subsBillStdId, String effStaDt);
}
```

---

### Step 7: Service 구현 — `SpecialSubscriptionServiceImpl.java`

**참조 패턴:** `CommonCodeServiceImpl.java` + `BillStdServiceImpl.java`

**핵심 로직:**
- `findAll`: 파라미터 유무에 따라 전건/필터 분기
- `create`: 복합키 중복 체크 → Entity 생성 → `SecurityUtils.getCurrentUserId()` 적용
- `update`: `findOrThrow` → 비PK 필드만 갱신 → `updatedBy`/`updatedDt` 설정
- `delete`: `findOrThrow` → 삭제
- `effEndDt` 기본값: `"99991231"` (등록 시 미입력이면 자동 설정)

```java
@Service
public class SpecialSubscriptionServiceImpl implements SpecialSubscriptionService {

    private final SpecialSubscriptionRepository repository;

    public SpecialSubscriptionServiceImpl(SpecialSubscriptionRepository repository) {
        this.repository = repository;
    }

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

    @Override
    public SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStaDt) {
        return toDto(findOrThrow(subsBillStdId, effStaDt));
    }

    @Override
    @Transactional
    public SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto) {
        SpecialSubscriptionId id = new SpecialSubscriptionId(dto.getSubsBillStdId(), dto.getEffStaDt());
        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 특수가입입니다: " + dto.getSubsBillStdId() + "/" + dto.getEffStaDt());
        }

        SpecialSubscription entity = new SpecialSubscription();
        entity.setId(id);
        entity.setSubsId(dto.getSubsId());
        entity.setSvcCd(dto.getSvcCd());
        entity.setEffEndDt(dto.getEffEndDt() != null && !dto.getEffEndDt().isBlank() ? dto.getEffEndDt() : "99991231");
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setStatCd(dto.getStatCd());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscRt(dto.getDscRt());
        entity.setRmk(dto.getRmk());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public SpecialSubscriptionResponseDto update(String subsBillStdId, String effStaDt, SpecialSubscriptionRequestDto dto) {
        SpecialSubscription entity = findOrThrow(subsBillStdId, effStaDt);

        // PK 필드(subsBillStdId, effStaDt) 변경 불가
        entity.setSubsId(dto.getSubsId());
        entity.setSvcCd(dto.getSvcCd());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setStatCd(dto.getStatCd());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscRt(dto.getDscRt());
        entity.setRmk(dto.getRmk());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String subsBillStdId, String effStaDt) {
        findOrThrow(subsBillStdId, effStaDt);
        repository.deleteById(new SpecialSubscriptionId(subsBillStdId, effStaDt));
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private SpecialSubscription findOrThrow(String subsBillStdId, String effStaDt) {
        SpecialSubscriptionId id = new SpecialSubscriptionId(subsBillStdId, effStaDt);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "특수가입을 찾을 수 없습니다: " + subsBillStdId + "/" + effStaDt));
    }

    private SpecialSubscriptionResponseDto toDto(SpecialSubscription e) {
        SpecialSubscriptionResponseDto dto = new SpecialSubscriptionResponseDto();
        dto.setSubsBillStdId(e.getId().getSubsBillStdId());
        dto.setEffStaDt(e.getId().getEffStaDt());
        dto.setSubsId(e.getSubsId());
        dto.setSvcCd(e.getSvcCd());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setLastEffYn(e.getLastEffYn());
        dto.setStatCd(e.getStatCd());
        dto.setCntrcCapKmh(e.getCntrcCapKmh());
        dto.setCntrcAmt(e.getCntrcAmt());
        dto.setDscRt(e.getDscRt());
        dto.setRmk(e.getRmk());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
```

---

### Step 8: Controller — `SpecialSubscriptionController.java`

**참조 패턴:** `CommonCodeController.java` (전건 목록 + 복합 PK 경로)

```java
@RestController
@RequestMapping("/api/special-subscriptions")
public class SpecialSubscriptionController {

    private final SpecialSubscriptionService service;

    public SpecialSubscriptionController(SpecialSubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<SpecialSubscriptionResponseDto> getAll(
            @RequestParam(required = false) String subsBillStdId,
            @RequestParam(required = false) String subsId) {
        return service.findAll(subsBillStdId, subsId);
    }

    @GetMapping("/{subsBillStdId}/{effStaDt}")
    public ResponseEntity<SpecialSubscriptionResponseDto> getById(
            @PathVariable String subsBillStdId,
            @PathVariable String effStaDt) {
        return ResponseEntity.ok(service.findById(subsBillStdId, effStaDt));
    }

    @PostMapping
    public ResponseEntity<SpecialSubscriptionResponseDto> create(
            @RequestBody SpecialSubscriptionRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{subsBillStdId}/{effStaDt}")
    public ResponseEntity<SpecialSubscriptionResponseDto> update(
            @PathVariable String subsBillStdId,
            @PathVariable String effStaDt,
            @RequestBody SpecialSubscriptionRequestDto dto) {
        return ResponseEntity.ok(service.update(subsBillStdId, effStaDt, dto));
    }

    @DeleteMapping("/{subsBillStdId}/{effStaDt}")
    public ResponseEntity<Void> delete(
            @PathVariable String subsBillStdId,
            @PathVariable String effStaDt) {
        service.delete(subsBillStdId, effStaDt);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Step 9: schema.sql 수정

**Before:** 파일 끝 (`tb_qna_comment` 인덱스 이후)

**After:** 아래 DDL 블록 추가

```sql
-- ================================================================
-- 테이블명 : TB_SPECIAL_SUBSCRIPTION (특수가입)
-- 설명     : 가입별 특수 과금 기준 관리
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_special_subscription
(
    /* ── Key (복합 PK) ──────────────────────────────── */
    subs_bill_std_id        VARCHAR(20)    NOT NULL,               -- 가입별과금기준ID
    eff_sta_dt              VARCHAR(8)     NOT NULL,               -- 유효시작일 (YYYYMMDD)

    /* ── 기본 정보 ──────────────────────────────────── */
    subs_id                 VARCHAR(20)    NOT NULL,               -- 가입ID
    svc_cd                  VARCHAR(10)    NULL,                   -- 서비스코드
    eff_end_dt              VARCHAR(8)     DEFAULT '99991231',     -- 유효종료일 (YYYYMMDD)
    last_eff_yn             VARCHAR(1)     NULL,                   -- 최종유효여부
    stat_cd                 VARCHAR(10)    NULL,                   -- 상태코드

    /* ── 약정 정보 ──────────────────────────────────── */
    cntrc_cap_kmh           NUMERIC(18,4)  NULL,                   -- 계약용량(kMh)
    cntrc_amt               NUMERIC(18,2)  NULL,                   -- 계약금액
    dsc_rt                  NUMERIC(18,4)  NULL,                   -- 할인율

    /* ── 비고 ───────────────────────────────────────── */
    rmk                     VARCHAR(500)   NULL,                   -- 비고

    /* ── System Fields ──────────────────────────────── */
    created_by              VARCHAR(50)    NOT NULL,               -- 생성자ID
    created_dt              TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(50)    NULL,                   -- 수정자ID
    updated_dt              TIMESTAMP      NULL,

    CONSTRAINT pk_tb_special_subscription PRIMARY KEY (subs_bill_std_id, eff_sta_dt)
);

CREATE INDEX IF NOT EXISTS idx_tb_special_subscription_subs
    ON tb_special_subscription (subs_id);
```

---

## 4. 트레이드오프 기록

| 결정 | 선택 | 대안 | 이유 |
|---|---|---|---|
| 복합 PK 방식 | `@EmbeddedId` | `@IdClass` | 프로젝트 기존 패턴(CommonDtlCode) 준수 |
| 조건부 검색 | Repository 메서드 분기 | `@Query` + JPQL 동적 쿼리 | 단순 조건이므로 Spring Data 파생 쿼리로 충분 |
| `effStaDt` 타입 | `VARCHAR(8)` / `String` | `LocalDate` | 요구사항 명시 + YYYYMMDD 문자열 비교 용이 |
| FK 제약 없음 | 문자열 ID 관리 | FK 선언 | 요구사항에서 제외 결정 |

---

## 5. 테스트 계획

- 기존 프로젝트에 단위 테스트 없음 → 별도 테스트 작성 불요 (요구사항 확정서 기준)
- 검증은 6단계(품질 검증)에서 서버 기동 + API 호출로 수행

---

## 6. 롤백 방안

- 신규 파일 8개 + schema.sql 수정 1건
- 모든 변경이 신규 생성이므로 파일 삭제로 원복 가능
- schema.sql은 추가된 DDL 블록만 제거
- H2 `ddl-auto=update` 환경이므로 테이블은 DB 콘솔에서 `DROP TABLE tb_special_subscription` 실행
