# 3단계: 프론트엔드 구현 계획서 (FE)

## 1. 구현 전략 개요

특수가입 관리 화면을 기존 BillStdPage(폼+액션바 패턴) + CommonCodePage(목록+폼+전건조회 패턴)를 조합하여 구현한다. 목록은 `DataGrid` 공통 컴포넌트를 사용하며, 전건 목록(페이징 없음) 유형을 적용한다.

**화면 구조:** SearchBar → DataGrid 목록 → 입력폼 → FloatingActionBar

**핵심 설계 결정:**
- 전건 목록 (페이징 없음) — `onPageChange` 생략하여 DataGrid 페이징 UI 자동 비노출
- 복합 PK 행 식별: `rowIdAccessor`에 `subsBillStdId` + `effStaDt` 결합 문자열 사용
- 모드 관리: `isNew` boolean 상태로 신규/수정 구분 (PK 필드 readOnly 제어)

---

## 2. 변경 파일 목록

| # | 파일 | 구분 | 역할 |
|---|---|---|---|
| 1 | `api/specialSubscriptionApi.js` | 신규 | API 호출 모듈 |
| 2 | `hooks/useSpecialSubscription.js` | 신규 | Hook (API 호출 캡슐화) |
| 3 | `components/special-subscription/SpecialSubscriptionSearchBar.jsx` | 신규 | 조회조건 |
| 4 | `components/special-subscription/SpecialSubscriptionList.jsx` | 신규 | 목록 (DataGrid) |
| 5 | `components/special-subscription/SpecialSubscriptionForm.jsx` | 신규 | 입력폼 |
| 6 | `components/special-subscription/SpecialSubscriptionActionBar.jsx` | 신규 | 액션바 |
| 7 | `pages/SpecialSubscriptionPage.jsx` | 신규 | 페이지 (상태 소유) |
| 8 | `App.jsx` | 수정 | 라우트 추가 |
| 9 | `components/main/Sidebar.jsx` | 수정 | 메뉴 활성화 |

---

## 3. 단계별 구현 순서

### Step 1: API 모듈 — `api/specialSubscriptionApi.js`

**참조 패턴:** `billStdApi.js` (개별 export 함수) + `commonCodeApi.js` (객체 export)

개별 export 함수 방식을 사용한다 (billStdApi 패턴).

```js
import apiClient from './apiClient'

export const getSpecialSubscriptions = (params) =>
  apiClient.get('/special-subscriptions', { params }).then(r => r.data)

export const getSpecialSubscription = (subsBillStdId, effStaDt) =>
  apiClient.get(`/special-subscriptions/${subsBillStdId}/${effStaDt}`).then(r => r.data)

export const createSpecialSubscription = (data) =>
  apiClient.post('/special-subscriptions', data).then(r => r.data)

export const updateSpecialSubscription = (subsBillStdId, effStaDt, data) =>
  apiClient.put(`/special-subscriptions/${subsBillStdId}/${effStaDt}`, data).then(r => r.data)

export const deleteSpecialSubscription = (subsBillStdId, effStaDt) =>
  apiClient.delete(`/special-subscriptions/${subsBillStdId}/${effStaDt}`)
```

---

### Step 2: Hook — `hooks/useSpecialSubscription.js`

**참조 패턴:** `useCommonCode.js` (전건 조회 + CRUD 래핑)

**전건 목록 패턴:** `items` 배열 상태 + `fetchList` 함수

```js
import { useState, useCallback } from 'react'
import {
  getSpecialSubscriptions,
  getSpecialSubscription,
  createSpecialSubscription,
  updateSpecialSubscription,
  deleteSpecialSubscription,
} from '../api/specialSubscriptionApi'

export default function useSpecialSubscription() {
  const [items, setItems] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const fetchList = useCallback(async (params = {}) => {
    setIsLoading(true)
    try {
      const data = await getSpecialSubscriptions(params)
      setItems(data)
      return data
    } finally {
      setIsLoading(false)
    }
  }, [])

  const fetchOne = useCallback(async (subsBillStdId, effStaDt) => {
    return getSpecialSubscription(subsBillStdId, effStaDt)
  }, [])

  const handleCreate = useCallback(async (data) => {
    return createSpecialSubscription(data)
  }, [])

  const handleUpdate = useCallback(async (subsBillStdId, effStaDt, data) => {
    return updateSpecialSubscription(subsBillStdId, effStaDt, data)
  }, [])

  const handleDelete = useCallback(async (subsBillStdId, effStaDt) => {
    return deleteSpecialSubscription(subsBillStdId, effStaDt)
  }, [])

  return { items, isLoading, fetchList, fetchOne, handleCreate, handleUpdate, handleDelete }
}
```

---

### Step 3: SearchBar — `components/special-subscription/SpecialSubscriptionSearchBar.jsx`

**참조 패턴:** `BillStdSearchBar.jsx`

**조회 조건:** 가입별과금기준ID (텍스트) + 가입ID (텍스트) + 조회 버튼

```jsx
export default function SpecialSubscriptionSearchBar({
  subsBillStdId, onSubsBillStdIdChange,
  subsId, onSubsIdChange,
  onSearch, isLoading
}) {
  return (
    <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
      <div className="flex items-center gap-2">
        <input
          type="text"
          value={subsBillStdId}
          onChange={onSubsBillStdIdChange}
          onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          placeholder="가입별과금기준ID"
          className="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        />
        <input
          type="text"
          value={subsId}
          onChange={onSubsIdChange}
          onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          placeholder="가입ID"
          className="w-48 h-8 border border-gray-300 rounded px-2 text-sm focus:outline-none focus:border-blue-400"
        />
        <button
          onClick={onSearch}
          disabled={isLoading}
          className="h-8 px-4 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white text-sm rounded transition-colors"
        >
          {isLoading ? '조회 중...' : '조회'}
        </button>
      </div>
    </div>
  )
}
```

---

### Step 4: List — `components/special-subscription/SpecialSubscriptionList.jsx`

**참조 패턴:** `DataGrid` 공통 컴포넌트 사용 (전건 목록 유형)

**복합 PK 행 식별:** `rowIdAccessor` 대신 `getRowId` 커스텀 함수 사용 또는 데이터에 결합키 필드 추가

```jsx
import { useMemo } from 'react'
import DataGrid from '../common/DataGrid'

export default function SpecialSubscriptionList({ items, selectedId, onRowClick }) {
  const columns = useMemo(() => [
    { accessorKey: 'subsBillStdId', header: '가입별과금기준ID', size: 160, minSize: 80 },
    { accessorKey: 'effStaDt',      header: '유효시작일',       size: 100, minSize: 60 },
    { accessorKey: 'subsId',        header: '가입ID',           size: 140, minSize: 80 },
    { accessorKey: 'svcCd',         header: '서비스코드',       size: 100, minSize: 60 },
    { accessorKey: 'statCd',        header: '상태코드',         size: 100, minSize: 60 },
    { accessorKey: 'lastEffYn',     header: '최종유효여부',     size: 100, minSize: 60 },
  ], [])

  // 복합 PK를 결합한 고유 식별자로 DataGrid에 데이터 전달
  const dataWithRowId = useMemo(
    () => items.map(item => ({ ...item, _rowId: `${item.subsBillStdId}__${item.effStaDt}` })),
    [items]
  )

  return (
    <DataGrid
      columns={columns}
      data={dataWithRowId}
      onRowClick={onRowClick}
      selectedRowId={selectedId}
      rowIdAccessor="_rowId"
    />
  )
}
```

---

### Step 5: Form — `components/special-subscription/SpecialSubscriptionForm.jsx`

**참조 패턴:** `BillStdForm.jsx` (Field + Section 내부 컴포넌트 패턴)

**PK 필드 제어:** `isNew` prop으로 신규(입력 가능) / 수정(readOnly) 분기

```jsx
function Field({ label, name, value, readOnly = false, required = false, onChange, type = 'text' }) {
  const inputClass = [
    'w-full h-8 border rounded px-2 text-sm',
    readOnly
      ? 'bg-gray-50 text-gray-400 border-gray-200'
      : 'bg-white border-gray-300 focus:outline-none focus:border-blue-400',
  ].join(' ')

  return (
    <div>
      <label className="block text-xs text-gray-500 mb-1">
        {label}
        {required && <span className="text-blue-400 ml-0.5">*</span>}
      </label>
      <input
        name={name}
        value={value}
        readOnly={readOnly}
        onChange={onChange}
        type={type}
        className={inputClass}
      />
    </div>
  )
}

function Section({ title, children }) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
      <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">{title}</h3>
      <div className="grid grid-cols-3 gap-x-4 gap-y-3">
        {children}
      </div>
    </div>
  )
}

export default function SpecialSubscriptionForm({ data = {}, onChange, isNew = false }) {
  const handleChange = onChange ?? (() => {})

  return (
    <div className="space-y-4">
      <Section title="기본 정보">
        <Field label="가입별과금기준ID" name="subsBillStdId" value={data.subsBillStdId || ''} readOnly={!isNew} onChange={handleChange} required />
        <Field label="유효시작일"       name="effStaDt"      value={data.effStaDt || ''}      readOnly={!isNew} onChange={handleChange} required />
        <Field label="가입ID"          name="subsId"        value={data.subsId || ''}        onChange={handleChange} required />
        <Field label="서비스코드"       name="svcCd"         value={data.svcCd || ''}         onChange={handleChange} />
        <Field label="유효종료일"       name="effEndDt"      value={data.effEndDt || ''}      onChange={handleChange} />
        <Field label="최종유효여부"     name="lastEffYn"     value={data.lastEffYn || ''}     onChange={handleChange} />
        <Field label="상태코드"        name="statCd"        value={data.statCd || ''}        onChange={handleChange} />
      </Section>

      <Section title="약정 정보">
        <Field label="계약용량(kMh)"   name="cntrcCapKmh"  value={data.cntrcCapKmh || ''}  onChange={handleChange} />
        <Field label="계약금액"        name="cntrcAmt"     value={data.cntrcAmt || ''}     onChange={handleChange} />
        <Field label="할인율"          name="dscRt"        value={data.dscRt || ''}        onChange={handleChange} />
      </Section>

      <Section title="비고">
        <div className="col-span-3">
          <label className="block text-xs text-gray-500 mb-1">비고</label>
          <textarea
            name="rmk"
            value={data.rmk || ''}
            onChange={handleChange}
            className="w-full h-20 border border-gray-300 rounded px-2 py-1 text-sm focus:outline-none focus:border-blue-400 resize-none"
          />
        </div>
      </Section>
    </div>
  )
}
```

---

### Step 6: ActionBar — `components/special-subscription/SpecialSubscriptionActionBar.jsx`

**참조 패턴:** `BillStdActionBar.jsx`

**버튼:** 삭제 (Danger) / 신규 (Neutral) / 저장 (Primary) — 왼쪽(위험) → 오른쪽(주요)

```jsx
import FloatingActionBar from '../common/FloatingActionBar'

export default function SpecialSubscriptionActionBar({ onNew, onSave, onDelete }) {
  return (
    <FloatingActionBar>
      <button
        onClick={onDelete}
        className="h-8 px-4 text-red-500 hover:text-red-700 hover:bg-red-50 rounded text-sm transition-colors"
      >
        삭제
      </button>
      <button
        onClick={onNew}
        className="h-8 px-4 border border-gray-300 text-gray-600 hover:bg-gray-50 rounded text-sm transition-colors"
      >
        신규
      </button>
      <button
        onClick={onSave}
        className="h-8 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm transition-colors"
      >
        저장
      </button>
    </FloatingActionBar>
  )
}
```

---

### Step 7: Page — `pages/SpecialSubscriptionPage.jsx`

**참조 패턴:** `BillStdPage.jsx` (EMPTY_FORM + toFormData + toRequestDto) + `CommonCodePage.jsx` (목록+폼 조합)

**상태 구조:**
- `searchSubsBillStdId`, `searchSubsId` — 조회조건
- `items` (hook에서 관리) — 목록 데이터
- `selected` — 선택된 행 원본 데이터
- `formData` — 폼 바인딩 데이터
- `isNew` — 신규/수정 모드

```jsx
import { useState, useMemo } from 'react'
import useSpecialSubscription from '../hooks/useSpecialSubscription'
import { useAuth } from '../context/AuthContext'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import ConfirmDialog from '../components/common/ConfirmDialog'
import SpecialSubscriptionSearchBar from '../components/special-subscription/SpecialSubscriptionSearchBar'
import SpecialSubscriptionList from '../components/special-subscription/SpecialSubscriptionList'
import SpecialSubscriptionForm from '../components/special-subscription/SpecialSubscriptionForm'
import SpecialSubscriptionActionBar from '../components/special-subscription/SpecialSubscriptionActionBar'

const EMPTY_FORM = {
  subsBillStdId: '', effStaDt: '', subsId: '', svcCd: '',
  effEndDt: '', lastEffYn: '', statCd: '',
  cntrcCapKmh: '', cntrcAmt: '', dscRt: '', rmk: '',
}

const toFormData = (dto) =>
  Object.fromEntries(
    Object.keys(EMPTY_FORM).map((key) => [key, dto[key] != null ? String(dto[key]) : ''])
  )

const toRequestDto = (form) => ({
  subsBillStdId: form.subsBillStdId || null,
  effStaDt:      form.effStaDt      || null,
  subsId:        form.subsId        || null,
  svcCd:         form.svcCd         || null,
  effEndDt:      form.effEndDt      || null,
  lastEffYn:     form.lastEffYn     || null,
  statCd:        form.statCd        || null,
  cntrcCapKmh:   form.cntrcCapKmh   ? parseFloat(form.cntrcCapKmh) : null,
  cntrcAmt:      form.cntrcAmt      ? parseFloat(form.cntrcAmt)    : null,
  dscRt:         form.dscRt         ? parseFloat(form.dscRt)       : null,
  rmk:           form.rmk           || null,
})

export default function SpecialSubscriptionPage() {
  const { user } = useAuth()
  const { items, isLoading, fetchList, handleCreate, handleUpdate, handleDelete } = useSpecialSubscription()

  const [searchSubsBillStdId, setSearchSubsBillStdId] = useState('')
  const [searchSubsId, setSearchSubsId]               = useState('')
  const [selected, setSelected]                       = useState(null)
  const [formData, setFormData]                       = useState(EMPTY_FORM)
  const [isNew, setIsNew]                             = useState(false)
  const [errorMsg, setErrorMsg]                       = useState(null)
  const [successMsg, setSuccessMsg]                   = useState(null)
  const [confirmOpen, setConfirmOpen]                 = useState(false)

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const selectedId = selected ? `${selected.subsBillStdId}__${selected.effStaDt}` : null

  // 조회
  const handleSearch = async () => {
    clearMessages()
    try {
      const params = {}
      if (searchSubsBillStdId.trim()) params.subsBillStdId = searchSubsBillStdId.trim()
      if (searchSubsId.trim())        params.subsId = searchSubsId.trim()
      await fetchList(params)
      setSelected(null)
      setFormData(EMPTY_FORM)
      setIsNew(false)
    } catch {
      setErrorMsg('조회에 실패했습니다.')
    }
  }

  // 행 클릭
  const handleRowClick = (item) => {
    setSelected(item)
    setFormData(toFormData(item))
    setIsNew(false)
  }

  // 필드 변경
  const handleFormChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  // 신규
  const handleNewClick = () => {
    setSelected(null)
    setFormData(EMPTY_FORM)
    setIsNew(true)
    clearMessages()
  }

  // 저장
  const handleSaveClick = async () => {
    clearMessages()
    if (!formData.subsBillStdId || !formData.effStaDt || !formData.subsId) {
      setErrorMsg('필수 항목을 입력해 주세요.')
      return
    }
    try {
      if (isNew) {
        const created = await handleCreate(toRequestDto(formData))
        setFormData(toFormData(created))
        setSelected(created)
        setIsNew(false)
        setSuccessMsg('저장이 완료되었습니다.')
      } else {
        if (!selected) { setErrorMsg('목록에서 항목을 선택해 주세요.'); return }
        const updated = await handleUpdate(
          selected.subsBillStdId, selected.effStaDt, toRequestDto(formData)
        )
        setFormData(toFormData(updated))
        setSelected(updated)
        setSuccessMsg('변경이 완료되었습니다.')
      }
      // 목록 갱신
      const params = {}
      if (searchSubsBillStdId.trim()) params.subsBillStdId = searchSubsBillStdId.trim()
      if (searchSubsId.trim())        params.subsId = searchSubsId.trim()
      await fetchList(params)
    } catch (err) {
      const status = err?.response?.status
      if (status === 409) {
        setErrorMsg('이미 존재하는 특수가입입니다.')
      } else if (status === 400) {
        setErrorMsg(err?.response?.data?.message || '입력값을 확인해 주세요.')
      } else {
        setErrorMsg('저장에 실패했습니다.')
      }
    }
  }

  // 삭제
  const handleDeleteClick = () => {
    if (!selected) { setErrorMsg('목록에서 항목을 선택해 주세요.'); return }
    clearMessages()
    setConfirmOpen(true)
  }

  const executeDelete = async () => {
    try {
      await handleDelete(selected.subsBillStdId, selected.effStaDt)
      setSelected(null)
      setFormData(EMPTY_FORM)
      setIsNew(false)
      setSuccessMsg('삭제가 완료되었습니다.')
      // 목록 갱신
      const params = {}
      if (searchSubsBillStdId.trim()) params.subsBillStdId = searchSubsBillStdId.trim()
      if (searchSubsId.trim())        params.subsId = searchSubsId.trim()
      await fetchList(params)
    } catch {
      setErrorMsg('삭제에 실패했습니다.')
    }
  }

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-4">
        <h1 className="text-xl font-bold text-gray-800">특수가입 관리</h1>

        <SpecialSubscriptionSearchBar
          subsBillStdId={searchSubsBillStdId}
          onSubsBillStdIdChange={(e) => setSearchSubsBillStdId(e.target.value)}
          subsId={searchSubsId}
          onSubsIdChange={(e) => setSearchSubsId(e.target.value)}
          onSearch={handleSearch}
          isLoading={isLoading}
        />

        <SpecialSubscriptionList
          items={items}
          selectedId={selectedId}
          onRowClick={handleRowClick}
        />

        <SpecialSubscriptionForm
          data={formData}
          onChange={handleFormChange}
          isNew={isNew}
        />
      </div>

      <SpecialSubscriptionActionBar
        onNew={handleNewClick}
        onSave={handleSaveClick}
        onDelete={handleDeleteClick}
      />

      {confirmOpen && (
        <ConfirmDialog
          message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
          onConfirm={() => { setConfirmOpen(false); executeDelete() }}
          onCancel={() => setConfirmOpen(false)}
        />
      )}
    </MainLayout>
  )
}
```

---

### Step 8: App.jsx 수정

**Before:**
```jsx
import QnaDetailPage from './pages/QnaDetailPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        ...
        <Route path="/qna/:id" element={<ProtectedRoute><QnaDetailPage /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  )
}
```

**After:**
```jsx
import QnaDetailPage from './pages/QnaDetailPage'
import SpecialSubscriptionPage from './pages/SpecialSubscriptionPage'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        ...
        <Route path="/qna/:id" element={<ProtectedRoute><QnaDetailPage /></ProtectedRoute>} />
        <Route path="/special-subscription" element={<ProtectedRoute><SpecialSubscriptionPage /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  )
}
```

---

### Step 9: Sidebar.jsx 수정

**Before:**
```jsx
{ label: '특수가입관리', to: null },
```

**After:**
```jsx
{ label: '특수가입관리', to: '/special-subscription' },
```

---

## 4. 트레이드오프 기록

| 결정 | 선택 | 대안 | 이유 |
|---|---|---|---|
| 복합 PK 행 식별 | `_rowId` 결합 문자열 필드 추가 | row index | DataGrid의 `rowIdAccessor`가 단일 문자열 키를 기대 — 결합키가 가장 안정적 |
| 목록 유형 | 전건 목록 (페이징 없음) | 서버 페이징 | 설정성 데이터, 건수 한정적 — 요구사항 확정서 기준 |
| 모드 관리 | `isNew` boolean | `mode` 문자열 (`new`/`edit`/`view`) | 이 화면은 CommonCode와 달리 마스터-디테일 구조 아님. 단순 boolean으로 충분 |
| API 모듈 스타일 | 개별 export 함수 | 객체 export | billStdApi 패턴 준수. 특수가입은 단일 도메인이므로 개별 함수로 충분 |
| 비고 필드 | `textarea` (col-span-3) | 일반 input | 500자 VARCHAR에 자유 입력 — textarea가 UX에 적합 |

---

## 5. 테스트 계획

- 기존 프로젝트에 프론트엔드 테스트 없음 → 별도 테스트 작성 불요
- 검증은 6단계(품질 검증)에서 브라우저 수동 테스트로 수행:
  - 메뉴 클릭 → 페이지 진입
  - 전건 조회 / 조건부 조회
  - 행 선택 → 폼 바인딩
  - 신규 등록 / 수정 / 삭제
  - Toast 메시지 표시
  - ConfirmDialog 동작

---

## 6. 롤백 방안

- 신규 파일 7개 삭제
- `App.jsx`: 추가된 import + Route 1줄 제거
- `Sidebar.jsx`: `to: '/special-subscription'` → `to: null` 원복
