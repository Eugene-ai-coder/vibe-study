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
| 모서리 반경 | `rounded` (`rounded-md`) | 4~6px 통일 |
| 목록 영역 고정 높이 | `h-[280px]` | 28px × 10행 |

**목록 스크롤 규칙**

- 목록 tbody 영역을 `h-[280px] overflow-y-auto`로 고정 — 10행 초과 시 세로 스크롤
- thead는 고정(sticky), tbody만 스크롤되도록 구성

**목록 공통 컴포넌트 (`DataGrid`)**

모든 목록은 `components/common/DataGrid.jsx`를 사용한다. 직접 `<table>`을 작성하지 않는다.

- 컬럼 너비: 컬럼 정의의 `size`(초기 px) / `minSize`(최소 px)로 선언 — TanStack Table 내장 리사이즈 사용
- 리사이즈: `columnResizeMode: 'onChange'` + `header.getResizeHandler()` — 수동 mousedown 이벤트 금지
- 레이아웃: 단일 `<table>` + `table-layout: fixed` + `colgroup`, 전체 너비 = 컬럼 합계
- 오버플로: 컬럼 합계가 컨테이너 초과 시 `overflow-x-auto` 가로 스크롤
- 셀 좌우 구분선: 헤더 `border-r border-gray-200`, 바디 `border-r border-gray-100`, 마지막 컬럼 `last:border-r-0`
- `title`: 그리드 타이틀 (선택) — 툴바 좌측에 표시. 필요 시 적용
- `storageKey`: 컬럼 개인화 활성화 (선택) — camelCase 페이지명 (예: `userPage`, `subscriptionMainPage`). 필요 시 적용
- `pinnedCount`: 좌측 컬럼 고정 (선택) — 고정할 왼쪽 컬럼 수. 필요 시 적용
- 팝업(필터/설정): `createPortal(document.body)` + `position: fixed` — overflow 컨텍스트 탈출 원칙

**컬럼 정의 예시**

```jsx
const columns = useMemo(() => [
  { accessorKey: 'userId',   header: '아이디', size: 120, minSize: 60 },
  { accessorKey: 'nickname', header: '닉네임', size: 120, minSize: 60 },
  { accessorKey: 'email',    header: '이메일', size: 200, minSize: 80 },
  {
    accessorKey: 'accountStatus',
    header: '상태',
    size: 80,
    minSize: 60,
    cell: ({ getValue }) => { /* 커스텀 셀 렌더링 */ },
    enableColumnFilter: false,  // 필터 비활성화가 필요한 컬럼에 지정
  },
], [])
```

**DataGrid 사용 유형**

데이터 규모에 따라 두 가지 유형으로 나눈다. 페이징 여부는 도메인 특성에 맞게 선택.

| 유형 | 기준 | 페이징 | 예시 도메인 |
|---|---|---|---|
| 페이징 목록 | 데이터가 지속 증가 (트랜잭션성) | 서버 페이징 | User, Qna, Subscription |
| 전건 목록 | 건수 한정적 (코드/설정성) | 없음 | CommonCode, BillStd |

**A. 페이징 목록 — Hook 패턴**

```jsx
// Hook: page 상태 + fetch 함수가 page/size를 서버에 전달
const [items, setItems] = useState([])
const [page, setPage] = useState(0)
const [totalPages, setTotalPages] = useState(0)
const [totalElements, setTotalElements] = useState(0)
const pageSize = 10

const fetchList = useCallback(async (searchParams = {}, pageNum = 0) => {
  const data = await getListPage({ page: pageNum, size: pageSize, ...searchParams })
  setItems(data.content)
  setPage(data.number)
  setTotalPages(data.totalPages)
  setTotalElements(data.totalElements)
}, [])
```

```jsx
// Page → DataGrid: 페이징 props 전달
<DataGrid
  columns={columns}
  data={items}
  page={page}
  totalPages={totalPages}
  totalElements={totalElements}
  pageSize={pageSize}
  onPageChange={(p) => fetchList(searchParams, p)}
  onRowClick={handleRowClick}
  selectedRowId={selectedId}
  rowIdAccessor="userId"
  storageKey="userPage"
  title="사용자 목록"
/>
```

**B. 전건 목록 — Hook 패턴**

```jsx
// Hook: 단순 배열 반환, 페이징 상태 없음
const [items, setItems] = useState([])

const fetchList = useCallback(async (params = {}) => {
  const res = await getAll(params)
  setItems(res.data)
}, [])
```

```jsx
// Page → DataGrid: onPageChange 생략 → 페이징 UI 자동 비노출
<DataGrid
  columns={columns}
  data={items}
  onRowClick={handleRowClick}
  selectedRowId={selectedCode}
  rowIdAccessor="commonCode"
/>
```

### 4.2 화면 4단 구조

```
A. 조회조건 (SearchBar)
B. 목록 (List)
C. 입력폼 (Form)
D. 액션바 (ActionBar) — FloatingActionBar 공통 컴포넌트 필수 사용
```

**액션바 배치 규칙**

- 반드시 `components/common/FloatingActionBar.jsx` 공통 컴포넌트를 사용한다 — 인라인 `fixed`/`sticky` 직접 작성 금지
- 동작: 콘텐츠가 짧을 때는 콘텐츠 바로 아래 배치, 스크롤이 생기면 화면 하단에 플로팅 고정 (`sticky bottom-0`)
- 페이지 콘텐츠 래퍼에 `pb-20` 등 하단 여백 불필요 — sticky는 콘텐츠를 가리지 않음

### 4.3 색상 팔레트

| 용도 | 값 |
|---|---|
| 배경 | `#F8FAFC` |
| 강조(Primary) | `#2563EB` |
| 카드 | `bg-white` + `rounded-lg` + 연한 그림자 |
| 입력 테두리 | `border-gray-300` |

### 4.4 조회조건 (SearchBar) 레이아웃

- 전체 너비를 채우지 않고 좌측 정렬 — 조건 입력 요소들을 `inline-flex` 또는 `flex gap-2`로 한 덩어리로 묶음
- 배경: `bg-gray-50 border border-gray-200 rounded-lg p-3` — 입력폼 영역과 시각적 분리
- 검색 조건 드롭다운 + 입력창 + 검색 버튼을 좌측부터 순서대로 배치

```jsx
<div className="bg-gray-50 border border-gray-200 rounded-lg p-3 mb-4">
  <div className="flex items-center gap-2">
    <select className="h-8 border border-gray-300 rounded px-2 text-sm ...">...</select>
    <input  className="h-8 border border-gray-300 rounded px-2 text-sm w-48 ..." />
    <button className="h-8 px-4 bg-blue-600 text-white rounded text-sm ...">조회</button>
  </div>
</div>
```

### 4.5 입력폼 레이아웃

- **레이블**: 입력필드 상단 배치 (`block text-xs text-gray-500 mb-1`)
- **입력/조회필드**: 레이블 하단 배치
- **열 수**: 한 행에 3개 항목 (`grid-cols-3`)
- **논리 그룹화**: 관련 항목끼리 섹션으로 묶고 소제목(`text-xs font-semibold text-gray-500 uppercase tracking-wide`) 부여

```jsx
{/* 섹션 예시 */}
<div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
  <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">기본 정보</h3>
  <div className="grid grid-cols-3 gap-x-4 gap-y-3">
    <div>
      <label className="block text-xs text-gray-500 mb-1">레이블 <span className="text-blue-400">*</span></label>
      <input className="w-full h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400" />
    </div>
    <div>
      <label className="block text-xs text-gray-500 mb-1">레이블</label>
      <input className="w-full h-8 border border-gray-200 rounded px-2 text-sm bg-gray-50 text-gray-400" readOnly />
    </div>
    <div>
      <label className="block text-xs text-gray-500 mb-1">레이블</label>
      <div className="flex gap-2">
        <input className="flex-1 h-8 border border-gray-300 rounded px-2 text-sm" />
        <button className="h-8 px-3 border border-gray-300 rounded text-sm text-gray-600 hover:bg-gray-50">검색</button>
      </div>
    </div>
  </div>
</div>
```

- 필수 항목 표시: `<span className="text-blue-400">*</span>` — 빨간색 대신 브랜드 컬러 사용
- `readOnly` 필드: `bg-gray-50 text-gray-400 border-gray-200` (테두리 연하게 처리)
- 조회 버튼이 있는 필드: 셀 내 `flex gap-2`로 입력+버튼 수평 배치
- 와이드 필드(textarea 등): `col-span-2` 또는 `col-span-3`으로 확장

### 4.6 액션바 버튼 색상 계층

| 역할 | 스타일 | Tailwind 예시 |
|---|---|---|
| 저장 (Primary) | 채움 · 브랜드 컬러 | `bg-blue-600 text-white hover:bg-blue-700` |
| 변경 (Secondary) | 테두리만 · 브랜드 컬러 | `border border-blue-600 text-blue-600 hover:bg-blue-50` |
| 삭제 (Danger) | 텍스트 버튼 · 빨간색 | `text-red-500 hover:text-red-700 hover:bg-red-50` |
| 취소/초기화 (Neutral) | 테두리 · 회색 | `border border-gray-300 text-gray-600 hover:bg-gray-50` |

- 저장·변경·삭제·취소 버튼 순서: 왼쪽(위험/보조) → 오른쪽(주요 액션)
- 모든 버튼 높이 `h-8`, 패딩 `px-4`, 모서리 `rounded`

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
