# 03. 백엔드 구현 계획서

## 구현 전략 개요

신규 도메인(공통코드, Q&A) 추가와 기존 도메인 버그 수정을 병렬 트랙으로 분리한다.
기존 단일 패키지 구조(`com.example.vibestudy`)를 유지하며, 모든 신규 파일은 해당 패키지에 플랫하게 추가한다.
`created_by` 'SYSTEM' 고정 → 세션 사용자 ID 교체는 `AuthController` 의 `register` 엔드포인트에만 해당하며,
다른 도메인은 프론트엔드에서 `user.userId`를 전달하는 방식으로 이미 처리 중이므로 `UserServiceImpl.register`만 수정한다.

---

## 변경 파일 목록

### 기존 파일 수정

| 파일 | 변경 사유 |
|---|---|
| `src/main/resources/schema.sql` | `tb_common_code`, `tb_common_dtl_code`, `tb_qna`, `tb_qna_comment` DDL 추가 |
| `src/main/java/.../UserServiceImpl.java` | `register()` 메서드의 `createdBy` 'SYSTEM' → 파라미터 수령 방식 변경 |
| `src/main/java/.../RegisterRequestDto.java` | `createdBy` 필드 추가 |
| `src/main/java/.../AuthController.java` | `/api/auth/register` — `HttpSession`에서 userId 추출하여 dto에 주입 (관리자가 등록하는 시나리오 대응) |

### 신규 파일 (공통코드)

| 파일 | 역할 |
|---|---|
| `CommonCode.java` | Entity — `tb_common_code` |
| `CommonDtlCode.java` | Entity — `tb_common_dtl_code` (복합 PK: `CommonDtlCodeId.java`) |
| `CommonDtlCodeId.java` | `@Embeddable` 복합 PK |
| `CommonCodeRepository.java` | JpaRepository |
| `CommonDtlCodeRepository.java` | JpaRepository |
| `CommonCodeRequestDto.java` | 헤더 등록/수정 요청 DTO |
| `CommonDtlCodeRequestDto.java` | 디테일 등록/수정 요청 DTO |
| `CommonCodeResponseDto.java` | 헤더 응답 DTO |
| `CommonDtlCodeResponseDto.java` | 디테일 응답 DTO |
| `CommonCodeService.java` | Service Interface |
| `CommonCodeServiceImpl.java` | Service 구현체 |
| `CommonCodeController.java` | REST Controller (`/api/common-codes`) |

### 신규 파일 (Q&A)

| 파일 | 역할 |
|---|---|
| `Qna.java` | Entity — `tb_qna` |
| `QnaComment.java` | Entity — `tb_qna_comment` |
| `QnaRepository.java` | JpaRepository (페이징 + 검색) |
| `QnaCommentRepository.java` | JpaRepository |
| `QnaRequestDto.java` | 게시글 등록/수정 요청 DTO |
| `QnaCommentRequestDto.java` | 댓글 등록 요청 DTO |
| `QnaResponseDto.java` | 게시글 응답 DTO |
| `QnaCommentResponseDto.java` | 댓글 응답 DTO |
| `QnaService.java` | Service Interface |
| `QnaServiceImpl.java` | Service 구현체 |
| `QnaController.java` | REST Controller (`/api/qna`) |

---

## 단계별 구현 순서

```
Step 1. schema.sql — 신규 테이블 4개 DDL 추가
Step 2. UserServiceImpl — createdBy 'SYSTEM' 제거
Step 3. 공통코드 백엔드 (Entity → Repository → DTO → Service → Controller)
Step 4. Q&A 백엔드 (Entity → Repository → DTO → Service → Controller)
Step 5. backend-rules.md — 신규 도메인 ID 접두사 등록
```

---

## Step 1. schema.sql DDL 추가

파일: `src/main/resources/schema.sql`

**After (기존 내용 뒤에 추가):**

```sql
-- ================================================================
-- 테이블명 : TB_COMMON_CODE (공통코드 헤더)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_common_code
(
    common_code         VARCHAR(50)     NOT NULL,
    common_code_nm      VARCHAR(100)    NULL,
    eff_start_dt        TIMESTAMP       NULL,
    eff_end_dt          TIMESTAMP       DEFAULT '9999-12-31 23:59:59',
    remark              VARCHAR(500)    NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_common_code PRIMARY KEY (common_code)
);

-- ================================================================
-- 테이블명 : TB_COMMON_DTL_CODE (공통상세코드)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_common_dtl_code
(
    common_code         VARCHAR(50)     NOT NULL,
    common_dtl_code     VARCHAR(50)     NOT NULL,
    common_dtl_code_nm  VARCHAR(100)    NULL,
    sort_order          INTEGER         DEFAULT 0,
    eff_start_dt        TIMESTAMP       NULL,
    eff_end_dt          TIMESTAMP       DEFAULT '9999-12-31 23:59:59',
    remark              VARCHAR(500)    NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_common_dtl_code PRIMARY KEY (common_code, common_dtl_code),
    CONSTRAINT fk_common_dtl_code_hdr FOREIGN KEY (common_code) REFERENCES tb_common_code (common_code)
);

CREATE INDEX IF NOT EXISTS idx_tb_common_dtl_code_code
    ON tb_common_dtl_code (common_code, sort_order);

-- ================================================================
-- 테이블명 : TB_QNA (Q&A 게시글)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_qna
(
    qna_id              VARCHAR(50)     NOT NULL,
    title               VARCHAR(200)    NULL,
    content             TEXT            NULL,
    view_cnt            INTEGER         DEFAULT 0,
    answer_yn           VARCHAR(1)      DEFAULT 'N',
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_qna PRIMARY KEY (qna_id)
);

-- ================================================================
-- 테이블명 : TB_QNA_COMMENT (Q&A 댓글)
-- ================================================================
CREATE TABLE IF NOT EXISTS tb_qna_comment
(
    comment_id          VARCHAR(50)     NOT NULL,
    qna_id              VARCHAR(50)     NOT NULL,
    parent_comment_id   VARCHAR(50)     NULL,
    content             TEXT            NULL,
    created_by          VARCHAR(50)     NOT NULL,
    created_dt          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(50)     NULL,
    updated_dt          TIMESTAMP       NULL,
    CONSTRAINT pk_tb_qna_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_qna_comment_qna FOREIGN KEY (qna_id) REFERENCES tb_qna (qna_id)
);

CREATE INDEX IF NOT EXISTS idx_tb_qna_comment_qna_id
    ON tb_qna_comment (qna_id);
```

---

## Step 2. createdBy 'SYSTEM' 제거

### 2-1. RegisterRequestDto.java

**Before:**
```java
public class RegisterRequestDto {
    private String userId;
    private String nickname;
    private String password;
    private String email;
    // getter/setter ...
}
```

**After (createdBy 필드 추가):**
```java
public class RegisterRequestDto {
    private String userId;
    private String nickname;
    private String password;
    private String email;
    private String createdBy;   // 추가
    // getter/setter ...
}
```

### 2-2. UserServiceImpl.java — register()

**Before:**
```java
user.setCreatedBy("SYSTEM");
```

**After:**
```java
user.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
```

### 2-3. AuthController.java — register()

**Before:**
```java
@PostMapping("/register")
public ResponseEntity<UserSessionDto> register(
        @Valid @RequestBody RegisterRequestDto dto) {
    return ResponseEntity.status(201).body(userService.register(dto));
}
```

**After (세션에서 userId 주입):**
```java
@PostMapping("/register")
public ResponseEntity<UserSessionDto> register(
        @Valid @RequestBody RegisterRequestDto dto,
        HttpSession session) {
    UserSessionDto caller = (UserSessionDto) session.getAttribute("SESSION_USER");
    if (caller != null && dto.getCreatedBy() == null) {
        dto.setCreatedBy(caller.getUserId());
    }
    return ResponseEntity.status(201).body(userService.register(dto));
}
```

---

## Step 3. 공통코드 백엔드

### 3-1. CommonCode.java (Entity)

```java
package com.example.vibestudy;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_common_code")
public class CommonCode {

    @Id
    @Column(name = "common_code", length = 50, nullable = false)
    private String commonCode;

    @Column(name = "common_code_nm", length = 100)
    private String commonCodeNm;

    @Column(name = "eff_start_dt")
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt")
    private LocalDateTime effEndDt;

    @Column(name = "remark", length = 500)
    private String remark;

    /* ── System Fields ── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // getter/setter 전체 명시적 선언
    public String getCommonCode() { return commonCode; }
    public void setCommonCode(String commonCode) { this.commonCode = commonCode; }
    public String getCommonCodeNm() { return commonCodeNm; }
    public void setCommonCodeNm(String commonCodeNm) { this.commonCodeNm = commonCodeNm; }
    public LocalDateTime getEffStartDt() { return effStartDt; }
    public void setEffStartDt(LocalDateTime effStartDt) { this.effStartDt = effStartDt; }
    public LocalDateTime getEffEndDt() { return effEndDt; }
    public void setEffEndDt(LocalDateTime effEndDt) { this.effEndDt = effEndDt; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
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

### 3-2. CommonDtlCodeId.java (복합 PK)

```java
package com.example.vibestudy;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommonDtlCodeId implements Serializable {
    private String commonCode;
    private String commonDtlCode;

    public CommonDtlCodeId() {}
    public CommonDtlCodeId(String commonCode, String commonDtlCode) {
        this.commonCode = commonCode;
        this.commonDtlCode = commonDtlCode;
    }
    // getter/setter, equals, hashCode
    public String getCommonCode() { return commonCode; }
    public void setCommonCode(String commonCode) { this.commonCode = commonCode; }
    public String getCommonDtlCode() { return commonDtlCode; }
    public void setCommonDtlCode(String commonDtlCode) { this.commonDtlCode = commonDtlCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonDtlCodeId)) return false;
        CommonDtlCodeId that = (CommonDtlCodeId) o;
        return Objects.equals(commonCode, that.commonCode) && Objects.equals(commonDtlCode, that.commonDtlCode);
    }
    @Override
    public int hashCode() { return Objects.hash(commonCode, commonDtlCode); }
}
```

### 3-3. CommonDtlCode.java (Entity)

```java
@Entity
@Table(name = "tb_common_dtl_code")
public class CommonDtlCode {

    @EmbeddedId
    private CommonDtlCodeId id;

    @Column(name = "common_dtl_code_nm", length = 100)
    private String commonDtlCodeNm;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "eff_start_dt")
    private LocalDateTime effStartDt;

    @Column(name = "eff_end_dt")
    private LocalDateTime effEndDt;

    @Column(name = "remark", length = 500)
    private String remark;

    /* ── System Fields ── */
    // createdBy, createdDt, updatedBy, updatedDt — 명시적 getter/setter
}
```

### 3-4. Repository 인터페이스

```java
// CommonCodeRepository.java
public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
    List<CommonCode> findByCommonCodeContainingOrCommonCodeNmContaining(
            String commonCode, String commonCodeNm);
}

// CommonDtlCodeRepository.java
public interface CommonDtlCodeRepository extends JpaRepository<CommonDtlCode, CommonDtlCodeId> {
    List<CommonDtlCode> findByIdCommonCodeOrderBySortOrder(String commonCode);
}
```

### 3-5. DTO 구조

**CommonCodeRequestDto:**
```java
// 필드: commonCode, commonCodeNm, effStartDt(LocalDateTime), effEndDt(LocalDateTime), remark, createdBy
```

**CommonDtlCodeRequestDto:**
```java
// 필드: commonDtlCode, commonDtlCodeNm, sortOrder(Integer), effStartDt, effEndDt, remark, createdBy
```

**CommonCodeResponseDto / CommonDtlCodeResponseDto:**
```java
// 요청 DTO 필드 + createdDt, updatedBy, updatedDt
```

### 3-6. CommonCodeService.java (Interface)

```java
public interface CommonCodeService {
    List<CommonCodeResponseDto> findAll(String commonCode, String commonCodeNm);
    CommonCodeResponseDto create(CommonCodeRequestDto dto);
    CommonCodeResponseDto update(String commonCode, CommonCodeRequestDto dto);
    void delete(String commonCode);

    List<CommonDtlCodeResponseDto> findDetails(String commonCode);
    CommonDtlCodeResponseDto createDetail(String commonCode, CommonDtlCodeRequestDto dto);
    CommonDtlCodeResponseDto updateDetail(String commonCode, String dtlCode, CommonDtlCodeRequestDto dto);
    void deleteDetail(String commonCode, String dtlCode);
}
```

### 3-7. CommonCodeServiceImpl.java — 핵심 로직

- **create**: `commonCode` PK 중복 시 `HttpStatus.CONFLICT` 예외
- **delete**: 연결된 `tb_common_dtl_code` 레코드 존재 시 `HttpStatus.CONFLICT` 예외
- **update**: `commonCode`(PK) 불변, `updatedBy = dto.getCreatedBy()`, `updatedDt = now()`
- **createDetail**: `(commonCode, commonDtlCode)` 복합 PK 중복 시 `HttpStatus.CONFLICT`
- 공통코드 ID는 사용자가 직접 입력 — 타임스탬프 자동생성 없음

### 3-8. CommonCodeController.java

```java
@RestController
@RequestMapping("/api/common-codes")
public class CommonCodeController {

    @GetMapping
    public List<CommonCodeResponseDto> getAll(
            @RequestParam(required = false) String commonCode,
            @RequestParam(required = false) String commonCodeNm) { ... }

    @PostMapping
    public ResponseEntity<CommonCodeResponseDto> create(@RequestBody CommonCodeRequestDto dto) { ... }

    @PutMapping("/{commonCode}")
    public ResponseEntity<CommonCodeResponseDto> update(
            @PathVariable String commonCode, @RequestBody CommonCodeRequestDto dto) { ... }

    @DeleteMapping("/{commonCode}")
    public ResponseEntity<Void> delete(@PathVariable String commonCode) { ... }

    @GetMapping("/{commonCode}/details")
    public List<CommonDtlCodeResponseDto> getDetails(@PathVariable String commonCode) { ... }

    @PostMapping("/{commonCode}/details")
    public ResponseEntity<CommonDtlCodeResponseDto> createDetail(
            @PathVariable String commonCode, @RequestBody CommonDtlCodeRequestDto dto) { ... }

    @PutMapping("/{commonCode}/details/{dtlCode}")
    public ResponseEntity<CommonDtlCodeResponseDto> updateDetail(
            @PathVariable String commonCode,
            @PathVariable String dtlCode,
            @RequestBody CommonDtlCodeRequestDto dto) { ... }

    @DeleteMapping("/{commonCode}/details/{dtlCode}")
    public ResponseEntity<Void> deleteDetail(
            @PathVariable String commonCode, @PathVariable String dtlCode) { ... }
}
```

---

## Step 4. Q&A 백엔드

### 4-1. Qna.java (Entity)

```java
@Entity
@Table(name = "tb_qna")
public class Qna {
    @Id
    @Column(name = "qna_id", length = 50, nullable = false)
    private String qnaId;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_cnt")
    private Integer viewCnt = 0;

    @Column(name = "answer_yn", length = 1)
    private String answerYn = "N";

    /* ── System Fields ── */
    // createdBy, createdDt, updatedBy, updatedDt
}
```

### 4-2. QnaComment.java (Entity)

```java
@Entity
@Table(name = "tb_qna_comment")
public class QnaComment {
    @Id
    @Column(name = "comment_id", length = 50, nullable = false)
    private String commentId;

    @Column(name = "qna_id", length = 50, nullable = false)
    private String qnaId;

    @Column(name = "parent_comment_id", length = 50)
    private String parentCommentId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /* ── System Fields ── */
}
```

### 4-3. QnaRepository.java

```java
public interface QnaRepository extends JpaRepository<Qna, String> {
    // 제목+내용 LIKE 검색 + 페이징
    Page<Qna> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);
}
```

### 4-4. ID 생성 패턴

```java
// QnaServiceImpl
private String generateQnaId() {
    return "QNA" + LocalDateTime.now().format(ID_FORMATTER);
}
// QnaCommentServiceImpl (또는 동일 Impl 내)
private String generateCommentId() {
    return "CMT" + LocalDateTime.now().format(ID_FORMATTER);
}
```

### 4-5. QnaService Interface

```java
public interface QnaService {
    Page<QnaResponseDto> findAll(String keyword, int page, int size);
    QnaResponseDto findById(String qnaId);   // 조회수 +1
    QnaResponseDto create(QnaRequestDto dto);
    QnaResponseDto update(String qnaId, QnaRequestDto dto);
    void delete(String qnaId);

    List<QnaCommentResponseDto> findComments(String qnaId);
    QnaCommentResponseDto createComment(String qnaId, QnaCommentRequestDto dto);
    void deleteComment(String qnaId, String commentId);
}
```

### 4-6. QnaController.java

```java
@RestController
@RequestMapping("/api/qna")
public class QnaController {

    @GetMapping
    public Page<QnaResponseDto> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { ... }

    @PostMapping
    public ResponseEntity<QnaResponseDto> create(@RequestBody QnaRequestDto dto) { ... }

    @GetMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> getOne(@PathVariable String qnaId) { ... }

    @PutMapping("/{qnaId}")
    public ResponseEntity<QnaResponseDto> update(
            @PathVariable String qnaId, @RequestBody QnaRequestDto dto) { ... }

    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> delete(@PathVariable String qnaId) { ... }

    @GetMapping("/{qnaId}/comments")
    public List<QnaCommentResponseDto> getComments(@PathVariable String qnaId) { ... }

    @PostMapping("/{qnaId}/comments")
    public ResponseEntity<QnaCommentResponseDto> createComment(
            @PathVariable String qnaId, @RequestBody QnaCommentRequestDto dto) { ... }

    @DeleteMapping("/{qnaId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String qnaId, @PathVariable String commentId) { ... }
}
```

---

## Step 5. backend-rules.md ID 접두사 등록

파일: `docs/backend-rules.md` 섹션 4 테이블에 추가

**Before:**
```
| 대표가입 (SubscriptionMain) | `SM` | `SM20240315143022123` |
| (신규 도메인 추가 시 여기에 등록) | | |
```

**After:**
```
| 대표가입 (SubscriptionMain) | `SM` | `SM20240315143022123` |
| Q&A 게시글 (Qna)            | `QNA` | `QNA20240315143022123` |
| Q&A 댓글 (QnaComment)       | `CMT` | `CMT20240315143022123` |
```

---

## 트레이드오프 기록

| 결정 | 선택 | 이유 |
|---|---|---|
| 공통코드 복합 PK | `@EmbeddedId` | JPA 표준, 별도 PK 클래스 명시적 선언으로 Lombok 없이 처리 가능 |
| Q&A 페이징 | Spring Data `Page<T>` | 프론트 페이지네이션과 직결, 추가 라이브러리 불필요 |
| 공통코드 삭제 | 디테일 존재 시 409 | 데이터 무결성 우선. 연계 삭제(Cascade)는 요구사항에 없음 |
| createdBy 'SYSTEM' 제거 | 폴백 유지 (`!= null ? ... : "SYSTEM"`) | 비로그인 등록 시나리오(회원가입 자가등록) 방어 |

---

## 테스트 계획

1. `schema.sql` 신규 DDL — 애플리케이션 기동 후 H2 Console에서 테이블 4개 생성 확인
2. 공통코드 CRUD — Postman: 그룹 등록 → 상세 등록 → 상세 조회 → 그룹 삭제(상세 있을 때 409 확인)
3. Q&A CRUD — Postman: 게시글 등록 → 조회(view_cnt +1 확인) → 댓글 등록 → 댓글 삭제
4. createdBy — 로그인 세션 상태에서 사용자 등록 시 `created_by` = 로그인 userId 확인

---

## 롤백 방안

- `schema.sql`에 `CREATE TABLE IF NOT EXISTS`로 추가하므로 기존 테이블 영향 없음
- 신규 파일만 추가되므로 파일 삭제로 롤백 가능
- `UserServiceImpl` 수정: 폴백 `"SYSTEM"` 유지로 기존 동작 보존
