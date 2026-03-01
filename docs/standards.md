# 프로젝트 공통 표준 (Standards)

---

## 1. 테이블 시스템 필드

### 1.1 필드 명세

모든 테이블에 아래 4개 시스템 필드를 필수 적용한다.

| 컬럼명 | Java 타입 | DB 타입 | nullable | 설명 |
|---|---|---|---|---|
| `created_by` | `String` (50) | VARCHAR(50) | NOT NULL | 최초 생성자 ID |
| `created_dt` | `LocalDateTime` | TIMESTAMP | NOT NULL | 최초 생성일시 |
| `updated_by` | `String` (50) | VARCHAR(50) | NULL | 최종 수정자 ID |
| `updated_dt` | `LocalDateTime` | TIMESTAMP | NULL | 최종 수정일시 |

### 1.2 처리 규칙

| 오퍼레이션 | `created_by` | `created_dt` | `updated_by` | `updated_dt` |
|---|---|---|---|---|
| **INSERT** | 프론트 전달값 (`dto.getCreatedBy()`) | 백엔드 자동 (`LocalDateTime.now()`) | 미설정 (NULL) | 미설정 (NULL) |
| **UPDATE** | 불변 (변경 금지) | 불변 (변경 금지) | 프론트 전달값 (`dto.getCreatedBy()`) | 백엔드 자동 (`LocalDateTime.now()`) |

### 1.3 프론트엔드 전달 방식
- 로그인 사용자 ID를 `createdBy` 필드로 request body에 포함하여 전달
- `RequestDto`에 `createdBy` 필드 1개만 선언 → INSERT 시 `created_by`, UPDATE 시 `updated_by`에 각각 매핑

### 1.4 Entity 선언 예시
```java
/* ── System Fields ─────────────────────────────────── */
@Column(name = "created_by", length = 50, nullable = false)
private String createdBy;

@Column(name = "created_dt", nullable = false)
private LocalDateTime createdDt;

@Column(name = "updated_by", length = 50)
private String updatedBy;

@Column(name = "updated_dt")
private LocalDateTime updatedDt;
```

---

## 2. UI 공통 표준

### 2.1 밀도 (Density)

| 요소 | Tailwind 클래스 | 픽셀 | 적용 범위 |
|---|---|---|---|
| 목록 행 높이 | `h-7` | 28px | 모든 테이블 행 |
| 입력/버튼 높이 | `h-8` | 32px | input, select, button |
| 텍스트 크기 | `text-sm` | 14px | 목록 및 입력 영역 전반 |

### 2.2 레이아웃

- **메인 레이아웃**: 상단 헤더(고정) + 좌측 LNB + 중앙 본문 → `MainLayout` 컴포넌트 사용
- **적용 대상**: 로그인 화면을 제외한 모든 메인화면
- **화면 4단 구조**: 조회조건(A) → 목록(B) → 조회/입력(C) → 액션바(D, `fixed bottom-0`)

### 2.3 색상 팔레트

| 용도 | 값 |
|---|---|
| 배경 | `#F8FAFC` |
| 강조(Primary) | `#2563EB` |
| 카드 배경 | `white` + `rounded-lg` + 연한 그림자 |
| 입력 테두리 | `border-gray-300` |
