# Backend Rules (Spring Boot 및 DB 전용)

---

## 1. 구조 규칙

- 의존성 주입: **생성자 주입** 전용 — `@Autowired` 필드 주입 금지
- Service는 반드시 **Interface + Impl** 분리
- GET 목록: `List<T>` 직접 반환 허용
- GET 단건·POST·PUT·DELETE: `ResponseEntity` 사용

---

## 2. Entity 규칙

### 2.1 Lombok 미사용

`@Data`, `@Getter`, `@Setter`, `@Builder` 등 Lombok 어노테이션 사용 금지.
모든 필드에 **명시적 getter/setter** 직접 선언:

```java
public String getSubsId() { return subsId; }
public void setSubsId(String subsId) { this.subsId = subsId; }
```

### 2.2 시스템 필드 — 모든 Entity 필수

```java
/* ── System Fields ─────────────────────────────── */
@Column(name = "created_by", length = 50, nullable = false)
private String createdBy;

@Column(name = "created_dt", nullable = false)
private LocalDateTime createdDt;

@Column(name = "updated_by", length = 50)
private String updatedBy;

@Column(name = "updated_dt")
private LocalDateTime updatedDt;
```

| 오퍼레이션 | created_by | created_dt | updated_by | updated_dt |
|---|---|---|---|---|
| INSERT | dto 전달값 | `LocalDateTime.now()` | NULL | NULL |
| UPDATE | 불변 | 불변 | dto 전달값 | `LocalDateTime.now()` |

---

## 3. DTO 규칙

### createdBy 단일 필드 원칙

RequestDto에 `createdBy` 필드 **1개만** 선언:

```java
private String createdBy;  // INSERT → created_by, UPDATE → updated_by 에 각각 매핑
```

`updatedBy` 별도 선언 금지. Service에서 오퍼레이션에 따라 분기 처리:

```java
// INSERT
entity.setCreatedBy(dto.getCreatedBy());
entity.setCreatedDt(LocalDateTime.now());

// UPDATE
entity.setUpdatedBy(dto.getCreatedBy());
entity.setUpdatedDt(LocalDateTime.now());
```

### toDto() 위치

Entity → ResponseDto 변환 메서드는 **ServiceImpl 내부 private** 메서드로 선언:

```java
private XxxResponseDto toDto(Xxx entity) { ... }
```

---

## 4. ID 자동생성 패턴

타임스탬프 기반 ID: `{도메인 접두사}` + `yyyyMMddHHmmssSSS`

```java
private static final DateTimeFormatter ID_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

private String generateId() {
    return "BS" + LocalDateTime.now().format(ID_FORMATTER);
}
```

| 도메인 | 접두사 | 예시 |
|---|---|---|
| 과금기준 (BillStd) | `BS` | `BS20240315143022123` |
| 대표가입 (SubscriptionMain) | `SM` | `SM20240315143022123` |
| Q&A 게시글 (Qna)            | `QNA` | `QNA20240315143022123` |
| Q&A 댓글 (QnaComment)       | `CMT` | `CMT20240315143022123` |

UUID 사용 금지.

---

## 5. DB 스키마 규칙

- 테이블명: `tb_{domain}` 소문자 스네이크 케이스
- 인덱스명: `idx_tb_{table}_{columns}`
- 시스템 필드 4개는 모든 테이블 마지막에 선언
- `created_dt` DEFAULT: `NOW()` / `updated_dt` DEFAULT: NULL
