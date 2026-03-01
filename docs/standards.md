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

---

## 3. 에러/알림 메시지 표시 표준

### 3.1 상황별 표시 방식

| 상황 | 방식 | 컴포넌트 |
|---|---|---|
| 액션 결과 피드백 (저장·변경·삭제·조회 성공·실패) | **Toast** — 상단 중앙 고정, 3초 자동소멸 | `<Toast>` |
| 폼 입력 유효성 오류 (클라이언트) | **Inline text** — 필드/조건 영역 하단 | `<p className="text-sm text-red-600">` |
| 서버 validation 오류 (배열 응답) | **Inline 에러 박스** — 폼 상단 | `<div className="bg-red-50 border border-red-200 ..."><ul>` |
| 파괴적 액션 확인 (삭제 등) | **ConfirmDialog** — 모달 오버레이 | `<ConfirmDialog>` |
| 페이지 초기 데이터 로드 실패 | **ErrorMessage** — 재시도 버튼 포함 | `<ErrorMessage>` |

### 3.2 Toast 사용 규칙

```jsx
// 페이지 state 선언
const [errorMsg, setErrorMsg]   = useState(null)  // 에러
const [successMsg, setSuccessMsg] = useState(null) // 성공

// JSX 배치 (MainLayout 바로 아래 첫 번째 위치)
<Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
<Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />
```

- 변수명: `errorMsg` / `successMsg` 단일 표준 (다른 명칭 금지)
- `clearMessages()` 헬퍼로 액션 시작 시 초기화
- duration: 3초 (Toast 컴포넌트 기본값)

### 3.3 ConfirmDialog 사용 규칙

```jsx
const [confirmOpen, setConfirmOpen] = useState(false)

// 삭제 버튼
<button onClick={() => setConfirmOpen(true)}>삭제</button>

// Dialog
{confirmOpen && (
  <ConfirmDialog
    message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
    onConfirm={() => { setConfirmOpen(false); handleDelete() }}
    onCancel={() => setConfirmOpen(false)}
  />
)}
```

- 브라우저 기본 `confirm()` 사용 금지
- 위험 액션(삭제) 확인 시 반드시 `ConfirmDialog` 사용
