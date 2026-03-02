# Frontend Rules (React 화면 개발 전용)

---

## 1. 구조 규칙

- 신규 기능: `components/{domain}/`, `hooks/use{Domain}.js`, `api/{domain}Api.js` 패턴 유지
- **Page**: 전체 상태 소유 + 이벤트 조율만 담당
- **Component**: UI 렌더링 전담 — 상태 없음, props만 수신
- **Hook**: API 호출 로직 캡슐화 — Page에서 직접 API 호출 금지
- `apiClient.js` 경유 필수 — `axios` 직접 import 금지
- 모든 메인 화면: `<MainLayout>` 으로 감싸기 (로그인 페이지 제외)

---

## 2. 상태 관리 규칙

### 2.1 메시지 변수명 고정

```jsx
const [errorMsg, setErrorMsg]     = useState(null)
const [successMsg, setSuccessMsg] = useState(null)
```

- `errorMsg` / `successMsg` 외 다른 명칭 금지 (`error`, `message`, `toast` 등)
- 액션 시작 전 항상 초기화:

```jsx
const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }
```

### 2.2 EMPTY_FORM 패턴

폼 초기값을 상수로 선언하고 등록·취소·행 선택 해제 시 재사용:

```jsx
const EMPTY_FORM = {
  subsId: '', subsNm: '', svcNm: '', subsStatusCd: ''
}

const [formData, setFormData] = useState(EMPTY_FORM)

// 초기화 시
setFormData(EMPTY_FORM)
```

인라인 `{}` 초기화 금지 — 필드 누락 방지 및 일관성 확보.

---

## 3. API 에러 처리 규칙

HTTP 상태 코드 기반으로 메시지를 분기한다:

```jsx
} catch (err) {
  const status = err?.response?.status
  if (status === 409) {
    setErrorMsg('과금기준이 존재하는 가입은 삭제할 수 없습니다.')
  } else if (status === 400) {
    setErrorMsg(err?.response?.data?.message || '입력값을 확인해 주세요.')
  } else {
    setErrorMsg('처리에 실패했습니다.')
  }
}
```

- 단순 `catch` 후 일괄 메시지 처리 금지
- 도메인별 409/400 메시지는 해당 기능 구현 시 명시

---

## 4. UI 표준

### 4.1 밀도 (Density)

| 요소 | Tailwind 클래스 | 픽셀 |
|---|---|---|
| 목록 행 높이 | `h-7` | 28px |
| 입력/버튼 높이 | `h-8` | 32px |
| 텍스트 크기 | `text-sm` | 14px |

### 4.2 화면 4단 구조

```
A. 조회조건 (SearchBar)
B. 목록 (List)
C. 입력폼 (Form)
D. 액션바 (ActionBar) — fixed bottom-0, pb-20으로 본문 여백 확보
```

### 4.3 색상 팔레트

| 용도 | 값 |
|---|---|
| 배경 | `#F8FAFC` |
| 강조(Primary) | `#2563EB` |
| 카드 | `bg-white` + `rounded-lg` + 연한 그림자 |
| 입력 테두리 | `border-gray-300` |

---

## 5. 알림/다이얼로그 표준

### 5.1 상황별 컴포넌트 선택

| 상황 | 컴포넌트 |
|---|---|
| 저장·변경·삭제·조회 성공/실패 | `<Toast>` — 상단 중앙, 3초 자동소멸 |
| 폼 입력 유효성 오류 (클라이언트) | `<p className="text-sm text-red-600">` |
| 서버 validation 오류 (배열 응답) | `<div className="bg-red-50 border border-red-200 ..."><ul>` |
| 파괴적 액션 확인 (삭제 등) | `<ConfirmDialog>` — 모달 오버레이 |
| 페이지 초기 데이터 로드 실패 | `<ErrorMessage>` — 재시도 버튼 포함 |

### 5.2 Toast 배치

```jsx
// MainLayout 바로 아래 첫 번째 위치
<MainLayout>
  <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
  <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />
  ...
</MainLayout>
```

### 5.3 ConfirmDialog 패턴

```jsx
const [confirmOpen, setConfirmOpen] = useState(false)

<button onClick={() => setConfirmOpen(true)}>삭제</button>

{confirmOpen && (
  <ConfirmDialog
    message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
    onConfirm={() => { setConfirmOpen(false); handleDelete() }}
    onCancel={() => setConfirmOpen(false)}
  />
)}
```

- 브라우저 기본 `confirm()` 사용 금지
