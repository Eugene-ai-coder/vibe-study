# 03. 프론트엔드 구현 계획서

## 구현 전략 개요

버그 수정(A/B/C/F)과 신규 기능(D/E)을 분리하여 순차 진행한다.
모든 변경은 `frontend/src/` 하위에 한정되며, 신규 도메인은 기존 패턴(`api/` → `hooks/` → `components/` → `pages/`)을 그대로 따른다.
`useAuth()` 훅에서 `user.userId`를 가져와 저장 시 `createdBy`로 전달하는 패턴은 모든 Page에 동일하게 적용한다.

---

## 변경 파일 목록

### 기존 파일 수정

| 파일 | 변경 내용 |
|---|---|
| `components/common/MainLayout.jsx` | 헤더에 `sticky top-0 z-10` 적용 (A-2) |
| `components/main/Sidebar.jsx` | Main 메뉴 추가, 공통코드관리 링크 활성화, 그룹명 폰트 변경 (F-1, F-2, D) |
| `App.jsx` | `/code`, `/qna`, `/qna/:id`, `/qna/new` 라우트 추가 (D, E) |
| `components/subscription-main/SubscriptionMainList.jsx` | 경계선, tbody max-h, 컬럼 드래그 리사이즈 (A-4, A-5, C-4) |
| `components/subscription-main/SubscriptionMainForm.jsx` | `grid-cols-2` → `grid-cols-6` 3열 구조 (A-3, C-2) |
| `pages/SubscriptionMainPage.jsx` | `SVC_MAP` 역방향 매핑, `createdBy` → `user.userId` (A-1, C-3) |
| `pages/UserPage.jsx` | 검색영역 추가, ActionBar 패턴 적용, `createdBy` → `user.userId` (B-1, B-2, C-3) |
| `pages/BillStdPage.jsx` | `createdBy` → `user.userId` (C-3) |
| `pages/SubscriptionPage.jsx` | `createdBy` → `user.userId` (C-3) |

### 신규 파일 (공통코드)

| 파일 | 역할 |
|---|---|
| `api/commonCodeApi.js` | 공통코드 API 호출 모듈 |
| `hooks/useCommonCode.js` | 공통코드 상태·이벤트 훅 |
| `components/common-code/CommonCodeList.jsx` | 좌측 공통코드 그룹 목록 |
| `components/common-code/CommonDtlCodeList.jsx` | 우측 공통상세코드 목록 |
| `components/common-code/CommonCodeForm.jsx` | 공통코드 그룹 입력폼 |
| `components/common-code/CommonDtlCodeForm.jsx` | 공통상세코드 입력폼 |
| `components/common-code/CommonCodeActionBar.jsx` | 액션바 (저장/취소/삭제) |
| `pages/CommonCodePage.jsx` | 마스터-디테일 화면 |

### 신규 파일 (Q&A)

| 파일 | 역할 |
|---|---|
| `api/qnaApi.js` | Q&A API 호출 모듈 |
| `hooks/useQna.js` | Q&A 상태·이벤트 훅 |
| `components/qna/QnaList.jsx` | 게시글 목록 + 페이지네이션 |
| `components/qna/QnaForm.jsx` | 제목/내용 입력폼 |
| `components/qna/QnaCommentList.jsx` | 댓글 목록 |
| `components/qna/QnaCommentForm.jsx` | 댓글 입력 영역 |
| `components/qna/QnaActionBar.jsx` | 액션바 (저장/수정/삭제/목록) |
| `pages/QnaPage.jsx` | 게시글 목록 화면 (`/qna`) |
| `pages/QnaDetailPage.jsx` | 게시글 상세/등록/수정 화면 (`/qna/:id`, `/qna/new`) |

---

## 단계별 구현 순서

```
Step 1. 공통 레이아웃 수정 (MainLayout 헤더 고정, Sidebar 개선)
Step 2. createdBy 'SYSTEM' 교체 (SubscriptionMainPage, BillStdPage, SubscriptionPage, UserPage)
Step 3. 대표가입 관리 화면 버그 수정 (SVC_MAP 역방향, 목록 경계선, 컬럼 리사이즈, 폼 3열)
Step 4. 사용자 관리 화면 버그 수정 (검색영역, ActionBar)
Step 5. 공통코드 관리 신규 화면 (api → hooks → components → pages → App.jsx)
Step 6. Q&A 게시판 신규 화면 (api → hooks → components → pages → App.jsx)
```

---

## Step 1. 공통 레이아웃 수정

### 1-1. MainLayout.jsx — 헤더 sticky 적용

**Before:**
```jsx
<header className="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6 flex-shrink-0">
```

**After:**
```jsx
<header className="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6 flex-shrink-0 sticky top-0 z-10">
```

### 1-2. Sidebar.jsx — Main 메뉴 추가, 공통코드 링크, 그룹명 폰트

**Before:**
```jsx
const MENU = [
  {
    group: '가입관리',
    items: [
      { label: '가입관리',     to: '/subscriptions' },
      ...
    ],
  },
  {
    group: '시스템 설정',
    items: [
      { label: '사용자관리',   to: '/users' },
      { label: '공통코드관리', to: null },
    ],
  },
]
// ...
<p className="px-4 py-1 text-xs font-semibold text-gray-400 uppercase tracking-wider">
```

**After:**
```jsx
const MENU = [
  { label: 'Main', to: '/main' },   // 단독 최상단 항목 (그룹 없음)
  {
    group: '가입관리',
    items: [
      { label: '가입관리',     to: '/subscriptions' },
      { label: '과금기준',     to: '/bill-std' },
      { label: '대표가입 관리', to: '/subscription-main' },
      { label: '특수가입관리', to: null },
    ],
  },
  {
    group: '시스템 설정',
    items: [
      { label: '사용자관리',   to: '/users' },
      { label: '공통코드관리', to: '/code' },   // null → '/code'
    ],
  },
  {
    group: '게시판',
    items: [
      { label: 'Q&A',  to: '/qna' },
    ],
  },
]
// ...
// MENU 렌더: 최상단 단독 항목은 NavLink, 나머지는 그룹 렌더 유지
// 그룹명 폰트 변경:
<p className="px-4 py-1 text-sm font-semibold text-gray-500 uppercase tracking-wider">
```

---

## Step 2. createdBy 'SYSTEM' 교체

모든 Page에서 아래 패턴으로 교체한다.

**Before (SubscriptionMainPage.jsx L98):**
```jsx
createdBy: 'SYSTEM',
```

**After:**
```jsx
// 상단에 추가
const { user } = useAuth()
// handleSaveClick 내부
createdBy: user?.userId ?? 'SYSTEM',
```

**적용 대상:**
- `SubscriptionMainPage.jsx` — `handleSaveClick` 내 `createdBy: 'SYSTEM'`
- `BillStdPage.jsx` — `toRequestDto` 내 `createdBy: 'SYSTEM'`
- `SubscriptionPage.jsx` — `toRequestDto` 내 `createdBy: 'SYSTEM'`
- `UserPage.jsx` — `useUser` 훅에 `user.userId` 전달 방식 연동

---

## Step 3. 대표가입 관리 화면 버그 수정

### 3-1. SVC_MAP 역방향 매핑 (SubscriptionMainPage.jsx)

현황: `SVC_MAP = { 'IDC 전력': '서비스1', ... }` — 화면명 → DB코드 방향
DB에는 '서비스1/2/3'으로 저장됨 → 목록 표시 시 역방향 변환 필요

**After:**
```jsx
// 기존 SVC_MAP 유지 (검색 파라미터 변환용)
const SVC_MAP = { 'IDC 전력': '서비스1', 'IDC NW': '서비스2', '비즈넷': '서비스3' }
// 역방향 매핑 추가 (목록 표시용)
const SVC_LABEL_MAP = { '서비스1': 'IDC 전력', '서비스2': 'IDC NW', '서비스3': '비즈넷' }
```

### 3-2. SubscriptionMainList.jsx — 경계선, max-h, 컬럼 리사이즈

**Before:**
```jsx
export default function SubscriptionMainList({ items, selectedId, onRowClick }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-gray-50 border-b border-gray-200">
          <tr>
            <th className="px-3 py-1.5 text-left text-xs font-medium text-gray-500">가입ID</th>
            ...
          </tr>
        </thead>
        <tbody>
          ...
          <tr className={`h-7 cursor-pointer border-b border-gray-100 ...`}>
```

**After (컬럼 리사이즈 + 경계선 + max-h):**
```jsx
import { useState, useRef, useCallback } from 'react'

const DEFAULT_WIDTHS = { subsId: 160, svcNm: 120, feeProdNm: 160, mainSubsYn: 80, mainSubsId: 160 }
const COLS = ['subsId', 'svcNm', 'feeProdNm', 'mainSubsYn', 'mainSubsId']

export default function SubscriptionMainList({ items, selectedId, onRowClick }) {
  const [colWidths, setColWidths] = useState(DEFAULT_WIDTHS)
  const dragging = useRef(null)

  const onMouseDown = useCallback((col, e) => {
    dragging.current = { col, startX: e.clientX, startW: colWidths[col] }
    const onMove = (ev) => {
      const delta = ev.clientX - dragging.current.startX
      setColWidths(prev => ({
        ...prev,
        [dragging.current.col]: Math.max(60, dragging.current.startW + delta)
      }))
    }
    const onUp = () => {
      dragging.current = null
      window.removeEventListener('mousemove', onMove)
      window.removeEventListener('mouseup', onUp)
    }
    window.addEventListener('mousemove', onMove)
    window.addEventListener('mouseup', onUp)
  }, [colWidths])

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm border-collapse">
          <thead className="bg-gray-50 border-t border-gray-300 border-b border-gray-300">
            <tr>
              {/* th: 상대 위치로 드래그 핸들 배치 */}
              <th style={{ width: colWidths.subsId }} className="relative px-3 py-1.5 text-left text-xs font-medium text-gray-500">
                가입ID
                <span onMouseDown={(e) => onMouseDown('subsId', e)}
                  className="absolute right-0 top-0 h-full w-1 cursor-col-resize bg-gray-200 hover:bg-blue-400" />
              </th>
              {/* 나머지 th 동일 패턴 */}
            </tr>
          </thead>
        </table>
        {/* tbody 별도 스크롤 영역 */}
        <div className="max-h-[40rem] overflow-y-auto">
          <table className="w-full text-sm border-collapse">
            <colgroup>
              {COLS.map(col => <col key={col} style={{ width: colWidths[col] }} />)}
            </colgroup>
            <tbody>
              {items.map(item => (
                <tr key={item.subsId}
                  onClick={() => onRowClick(item)}
                  className={`h-7 cursor-pointer border-b border-gray-200 ...`}>
                  <td className="px-3 text-xs">{item.subsId}</td>
                  <td className="px-3 text-xs">{SVC_LABEL_MAP[item.svcNm] || item.svcNm}</td>
                  {/* svcNm은 props로 SVC_LABEL_MAP 변환값 전달 또는 컴포넌트 내 import */}
                  ...
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
```

> 주의: `SVC_LABEL_MAP`은 `SubscriptionMainList.jsx` 내에 직접 선언하거나 Page에서 변환 후 props로 전달한다.
> 단순성 우선 — Page에서 변환 후 전달하는 방식 채택 (컴포넌트는 표시만 담당).

### 3-3. SubscriptionMainForm.jsx — grid-cols-2 → grid-cols-6

**Before:**
```jsx
<div className="grid grid-cols-2 gap-x-6 gap-y-3">
  <div>...</div>  {/* 가입ID */}
  <div>...</div>  {/* 대표가입여부 */}
  <div>...</div>  {/* 유효시작일시 */}
  <div>...</div>  {/* 유효종료일시 */}
  <div className="col-span-2">...</div>  {/* 대표가입ID */}
</div>
```

**After (레이블+값 × 3쌍, col-span-1 레이블 + col-span-1 값):**
```jsx
<div className="grid grid-cols-6 gap-x-4 gap-y-3">
  {/* Row 1: 가입ID | 대표가입여부 | 유효시작일시 */}
  <label className="col-span-1 flex items-center text-xs text-gray-500">가입ID</label>
  <div className="col-span-1"><input readOnly ... /></div>
  <label className="col-span-1 flex items-center text-xs text-gray-500">대표가입여부</label>
  <div className="col-span-1"><select ... /></div>
  <label className="col-span-1 flex items-center text-xs text-gray-500">유효시작일시</label>
  <div className="col-span-1"><input readOnly ... /></div>

  {/* Row 2: 유효종료일시 | 대표가입ID (2칸 span) */}
  <label className="col-span-1 flex items-center text-xs text-gray-500">유효종료일시</label>
  <div className="col-span-1"><input readOnly ... /></div>
  <label className="col-span-1 flex items-center text-xs text-gray-500">
    대표가입ID {isMainSubsIdActive && <span className="text-red-400 ml-1">*</span>}
  </label>
  <div className="col-span-3 flex gap-2">
    <input ... />
    <button ...>검색</button>
  </div>
</div>
```

---

## Step 4. 사용자 관리 화면 버그 수정

### 4-1. UserPage.jsx — 검색영역 추가

**Before:** 조회영역 없음

**After (상단에 SearchBar 추가):**
```jsx
// 상태 추가
const [searchUserId, setSearchUserId]   = useState('')
const [searchNickname, setSearchNickname] = useState('')
const [searchEmail, setSearchEmail]     = useState('')

// 검색 처리
const handleSearch = async () => {
  clearMessages()
  try {
    const result = await fetchUsers({ userId: searchUserId, nickname: searchNickname, email: searchEmail })
    setUsers(result)
  } catch {
    setErrorMsg('조회에 실패했습니다.')
  }
}

// 조회영역 JSX
<div className="bg-white rounded-lg shadow-sm p-4 flex items-end gap-4">
  <div>
    <label className="block text-xs text-gray-500 mb-1">사용자ID</label>
    <input value={searchUserId} onChange={e => setSearchUserId(e.target.value)}
      className="h-8 px-3 border border-gray-300 rounded text-sm" />
  </div>
  <div>
    <label className="block text-xs text-gray-500 mb-1">사용자명</label>
    <input value={searchNickname} onChange={e => setSearchNickname(e.target.value)}
      className="h-8 px-3 border border-gray-300 rounded text-sm" />
  </div>
  <div>
    <label className="block text-xs text-gray-500 mb-1">이메일</label>
    <input value={searchEmail} onChange={e => setSearchEmail(e.target.value)}
      className="h-8 px-3 border border-gray-300 rounded text-sm" />
  </div>
  <button onClick={handleSearch}
    className="h-8 px-4 bg-[#2563EB] text-white text-sm rounded">조회</button>
</div>
```

> `useUser` 훅에 `fetchUsers(params)` 메서드 추가 또는 `api/userApi.js`에 검색 파라미터 지원 추가

### 4-2. UserPage.jsx — ActionBar 패턴

**Before:**
```jsx
<div className="mt-4 flex justify-end">
  <button onClick={handleRegister} ...>등록</button>
</div>
```

**After (폼 내 버튼 제거 + ActionBar 추가):**
```jsx
{/* 폼 내 버튼 div 전체 제거 */}

{/* 페이지 하단에 추가 */}
<div className="fixed bottom-0 left-56 right-0 bg-white border-t border-gray-200 px-6 py-3 flex justify-end gap-3 z-10">
  <button onClick={() => setForm(EMPTY_FORM)}
    className="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50">
    취소
  </button>
  <button onClick={handleRegister}
    className="h-8 px-6 bg-[#2563EB] text-white text-sm rounded">
    등록
  </button>
</div>

{/* pb-20으로 본문 여백 확보 */}
<div className="space-y-6 pb-20">
```

---

## Step 5. 공통코드 관리 신규 화면

### 5-1. api/commonCodeApi.js

```js
import apiClient from './apiClient'

export const commonCodeApi = {
  getAll:       (params) => apiClient.get('/api/common-codes', { params }),
  create:       (data)   => apiClient.post('/api/common-codes', data),
  update:       (code, data) => apiClient.put(`/api/common-codes/${code}`, data),
  delete:       (code)   => apiClient.delete(`/api/common-codes/${code}`),

  getDetails:     (code)        => apiClient.get(`/api/common-codes/${code}/details`),
  createDetail:   (code, data)  => apiClient.post(`/api/common-codes/${code}/details`, data),
  updateDetail:   (code, dtl, data) => apiClient.put(`/api/common-codes/${code}/details/${dtl}`, data),
  deleteDetail:   (code, dtl)   => apiClient.delete(`/api/common-codes/${code}/details/${dtl}`),
}
```

### 5-2. hooks/useCommonCode.js

```js
import { useState, useCallback } from 'react'
import { commonCodeApi } from '../api/commonCodeApi'

export default function useCommonCode() {
  const [codes, setCodes]   = useState([])
  const [details, setDetails] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const fetchCodes = useCallback(async (params = {}) => {
    setIsLoading(true)
    try {
      const res = await commonCodeApi.getAll(params)
      setCodes(res.data)
      return res.data
    } finally { setIsLoading(false) }
  }, [])

  const fetchDetails = useCallback(async (commonCode) => {
    const res = await commonCodeApi.getDetails(commonCode)
    setDetails(res.data)
  }, [])

  // createCode, updateCode, deleteCode, createDetail, updateDetail, deleteDetail 메서드 선언
  return { codes, details, isLoading, fetchCodes, fetchDetails, /* ... */ }
}
```

### 5-3. CommonCodePage.jsx — 화면 레이아웃

```
┌─ SearchBar (공통코드, 공통코드명, 조회버튼) ──────────────────┐
├─ 좌측: CommonCodeList ──┬─ 우측: CommonDtlCodeList ──────────┤
│  공통코드 목록           │  상세코드 목록                      │
│  [등록] 버튼             │  [등록] [수정] [삭제] 버튼           │
└─────────────────────────┴────────────────────────────────────┘
├─ 선택된 공통코드 폼 (CommonCodeForm) ──────────────────────────┤
├─ 선택된 상세코드 폼 (CommonDtlCodeForm) ───────────────────────┤
└─ CommonCodeActionBar (저장/취소) ──────────────────────────────┘
```

```jsx
// EMPTY_CODE_FORM / EMPTY_DTL_FORM 상수 선언
const EMPTY_CODE_FORM = { commonCode: '', commonCodeNm: '', effStartDt: '', effEndDt: '', remark: '' }
const EMPTY_DTL_FORM  = { commonDtlCode: '', commonDtlCodeNm: '', sortOrder: 0, effStartDt: '', effEndDt: '', remark: '' }

export default function CommonCodePage() {
  const { user } = useAuth()
  const { codes, details, isLoading, fetchCodes, fetchDetails,
          createCode, updateCode, deleteCode,
          createDetail, updateDetail, deleteDetail } = useCommonCode()

  const [selectedCode, setSelectedCode]   = useState(null)
  const [selectedDtl, setSelectedDtl]     = useState(null)
  const [codeForm, setCodeForm]           = useState(EMPTY_CODE_FORM)
  const [dtlForm, setDtlForm]             = useState(EMPTY_DTL_FORM)
  const [codeMode, setCodeMode]           = useState('view')   // 'new' | 'edit' | 'view'
  const [dtlMode, setDtlMode]             = useState('view')

  // 공통코드 행 클릭 → 우측 상세 갱신
  const handleCodeRowClick = async (code) => {
    setSelectedCode(code)
    setCodeForm({ ...code })
    setSelectedDtl(null)
    setDtlForm(EMPTY_DTL_FORM)
    await fetchDetails(code.commonCode)
  }
  // ...
}
```

### 5-4. 마스터-디테일 레이아웃 구조

```jsx
<div className="flex gap-4">
  {/* 좌측 공통코드 목록 */}
  <div className="w-1/3">
    <CommonCodeList codes={codes} selectedCode={selectedCode?.commonCode} onRowClick={handleCodeRowClick} />
    <button onClick={() => { setCodeMode('new'); setCodeForm(EMPTY_CODE_FORM) }}>등록</button>
  </div>
  {/* 우측 공통상세코드 목록 */}
  <div className="w-2/3">
    <CommonDtlCodeList details={details} selectedDtl={selectedDtl?.commonDtlCode} onRowClick={handleDtlRowClick} />
    <div className="flex gap-2">
      <button onClick={() => setDtlMode('new')}>등록</button>
      <button onClick={() => setDtlMode('edit')} disabled={!selectedDtl}>수정</button>
      <button onClick={handleDtlDelete} disabled={!selectedDtl}>삭제</button>
    </div>
  </div>
</div>

{/* 폼 영역 */}
{codeMode !== 'view' && <CommonCodeForm data={codeForm} onChange={handleCodeFormChange} />}
{dtlMode !== 'view' && <CommonDtlCodeForm data={dtlForm} onChange={handleDtlFormChange} />}
```

### 5-5. CommonCodeForm.jsx — grid-cols-6 3열 구조

```jsx
// 필드: 공통코드(PK — 신규 시 입력, 수정 시 readonly), 공통코드명, 유효시작일, 유효종료일, 비고
<div className="grid grid-cols-6 gap-x-4 gap-y-3">
  <label className="col-span-1 ...">공통코드</label>
  <div className="col-span-1"><input name="commonCode" readOnly={isEdit} /></div>
  <label className="col-span-1 ...">공통코드명</label>
  <div className="col-span-1"><input name="commonCodeNm" /></div>
  <label className="col-span-1 ...">유효시작일</label>
  <div className="col-span-1"><input type="datetime-local" name="effStartDt" /></div>

  <label className="col-span-1 ...">유효종료일</label>
  <div className="col-span-1"><input type="datetime-local" name="effEndDt" /></div>
  <label className="col-span-1 ...">비고</label>
  <div className="col-span-3"><textarea name="remark" /></div>
</div>
```

### 5-6. App.jsx 라우트 추가

**After (기존 라우트 유지 + 추가):**
```jsx
import CommonCodePage from './pages/CommonCodePage'
import QnaPage        from './pages/QnaPage'
import QnaDetailPage  from './pages/QnaDetailPage'

// Routes 내부에 추가
<Route path="/code" element={<ProtectedRoute><CommonCodePage /></ProtectedRoute>} />
<Route path="/qna"  element={<ProtectedRoute><QnaPage /></ProtectedRoute>} />
<Route path="/qna/new" element={<ProtectedRoute><QnaDetailPage /></ProtectedRoute>} />
<Route path="/qna/:id" element={<ProtectedRoute><QnaDetailPage /></ProtectedRoute>} />
```

---

## Step 6. Q&A 게시판 신규 화면

### 6-1. api/qnaApi.js

```js
import apiClient from './apiClient'

export const qnaApi = {
  getAll:       (params) => apiClient.get('/api/qna', { params }),
  getOne:       (qnaId)  => apiClient.get(`/api/qna/${qnaId}`),
  create:       (data)   => apiClient.post('/api/qna', data),
  update:       (qnaId, data) => apiClient.put(`/api/qna/${qnaId}`, data),
  delete:       (qnaId)  => apiClient.delete(`/api/qna/${qnaId}`),
  getComments:  (qnaId)  => apiClient.get(`/api/qna/${qnaId}/comments`),
  createComment:(qnaId, data) => apiClient.post(`/api/qna/${qnaId}/comments`, data),
  deleteComment:(qnaId, commentId) => apiClient.delete(`/api/qna/${qnaId}/comments/${commentId}`),
}
```

### 6-2. QnaPage.jsx — 목록 화면

```
A. 조회영역: [검색어] [검색]
B. QnaList: 번호, 제목(NavLink), 작성자, 답변여부, 조회수, 등록일
C. 페이지네이션: 10건 단위 이전/다음 버튼
D. 하단 버튼: [등록] — /qna/new 이동
```

```jsx
// 페이지네이션 상태
const [page, setPage]       = useState(0)
const [totalPages, setTotalPages] = useState(0)
const [keyword, setKeyword] = useState('')

const handleSearch = async (p = 0) => {
  const res = await qnaApi.getAll({ keyword, page: p, size: 10 })
  setItems(res.data.content)
  setTotalPages(res.data.totalPages)
  setPage(p)
}
```

### 6-3. QnaDetailPage.jsx — 상세/등록/수정

```
A. 폼: 제목(input), 내용(textarea)
B. QnaCommentList: 댓글 목록 (들여쓰기로 대댓글 표현)
C. QnaCommentForm: 댓글 입력 textarea + 등록 버튼
D. QnaActionBar: [저장|수정] [삭제] [목록] 버튼
```

```jsx
const { id } = useParams()
const isNew = id === undefined  // /qna/new 경로

// 신규: 빈 폼, 기존: useEffect로 getOne 호출 후 폼 채움
useEffect(() => {
  if (!isNew) {
    qnaApi.getOne(id).then(res => setForm(res.data))
    qnaApi.getComments(id).then(res => setComments(res.data))
  }
}, [id])
```

### 6-4. QnaCommentList.jsx — 계층 표현

```jsx
// parent_comment_id null = 최상위, 그 외 = 대댓글
// 단순 구현: parentCommentId가 있으면 pl-8 들여쓰기
{comments.map(c => (
  <div key={c.commentId} className={c.parentCommentId ? 'pl-8' : ''}>
    <p className="text-sm text-gray-800">{c.content}</p>
    <span className="text-xs text-gray-400">{c.createdBy} · {c.createdDt}</span>
    {c.createdBy === user?.userId && (
      <button onClick={() => handleDeleteComment(c.commentId)} className="...">삭제</button>
    )}
  </div>
))}
```

---

## 공통 UI 표준 적용 사항 (C-1, C-4)

모든 List 컴포넌트:
- `tbody` 영역: `max-h-[40rem] overflow-y-auto`
- `tr` 행 높이: `h-7`
- 텍스트: `text-sm`
- 경계선: `border-b border-gray-200` (tbody tr), `border-t border-gray-300 border-b border-gray-300` (thead)

---

## 트레이드오프 기록

| 결정 | 선택 | 이유 |
|---|---|---|
| 컬럼 리사이즈 | 커스텀 onMouseDown 이벤트 | 외부 라이브러리 의존 없이 구현. 상태를 localStorage에 영속화하지 않음(요구사항 제외) |
| 공통코드 마스터-디테일 | 단일 Page + 조건부 폼 렌더 | 좌우 분할 구조로 라우팅 없이 Panel 전환 |
| Q&A 상세/등록/수정 | 단일 QnaDetailPage (`useParams`) | isNew 분기로 코드 중복 최소화 |
| SVC_MAP 역방향 | Page에서 변환 후 props 전달 | 컴포넌트의 순수성 유지 (UI만 담당) |
| 사용자 검색 API | `/api/auth/users` 기존 엔드포인트 활용 + 프론트 필터 | 백엔드 별도 검색 API 추가 없이 프론트에서 필터링 (데이터 양이 많지 않은 관리자 화면) |

---

## 테스트 계획

1. 헤더 고정: 목록에 데이터 다수 로드 후 스크롤 시 헤더 위치 확인
2. SVC_MAP: 대표가입 목록의 서비스 컬럼에 '서비스1' 대신 'IDC 전력' 표시 확인
3. 컬럼 리사이즈: 헤더 우측 핸들 드래그 후 컬럼 폭 변경 확인
4. 3열 폼: SubscriptionMainForm 필드 3쌍 배치 확인
5. UserPage 검색: 이름 LIKE 검색 → 결과 필터 확인
6. UserPage ActionBar: 폼 외부 하단 고정 버튼 확인
7. 공통코드: 그룹 등록 → 상세 등록 → 좌측 선택 시 우측 갱신 확인
8. Q&A: 게시글 등록 → 목록 노출 → 상세 조회수 +1 → 댓글 등록 확인
9. Sidebar: Main 클릭 → /main 이동, 공통코드관리 클릭 → /code 이동
10. createdBy: 로그인 사용자 ID가 저장된 레코드에 반영되었는지 BE 응답 확인

---

## 롤백 방안

- 신규 파일(api/hooks/components/pages)은 삭제로 즉시 롤백
- App.jsx 라우트 추가분 제거
- Sidebar.jsx MENU 배열 이전 상태로 복원
- MainLayout.jsx 헤더 클래스에서 `sticky top-0 z-10` 제거
- 각 Page의 `createdBy: user?.userId ?? 'SYSTEM'` → `createdBy: 'SYSTEM'` 롤백 (폴백 내장으로 기능 유지)
