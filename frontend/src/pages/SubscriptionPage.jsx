import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import useSubscription from '../hooks/useSubscription'
import { useAuth } from '../context/AuthContext'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import ConfirmDialog from '../components/common/ConfirmDialog'
import SubscriptionSearchBar from '../components/subscription/SubscriptionSearchBar'
import SubscriptionList from '../components/subscription/SubscriptionList'
import SubscriptionForm from '../components/subscription/SubscriptionForm'
import SubscriptionActionBar from '../components/subscription/SubscriptionActionBar'

const EMPTY_FORM = {
  subsId: '', subsNm: '', svcNm: '', feeProdNm: '',
  subsStatusCd: '', subsDt: '', chgDt: ''
}

const toFormData = (dto) => ({
  subsId:        dto.subsId        || '',
  subsNm:        dto.subsNm        || '',
  svcNm:         dto.svcNm         || '',
  feeProdNm:     dto.feeProdNm     || '',
  subsStatusCd:  dto.subsStatusCd  || '',
  subsDt:        dto.subsDt        ? dto.subsDt.slice(0, 16) : '',
  chgDt:         dto.chgDt         ? dto.chgDt.slice(0, 16) : '',
})

const toRequestDto = (form, userId) => ({
  subsId:       form.subsId       || null,
  subsNm:       form.subsNm       || null,
  svcNm:        form.svcNm        || null,
  feeProdNm:    form.feeProdNm    || null,
  subsStatusCd: form.subsStatusCd || null,
  subsDt:       form.subsDt       || null,
  chgDt:        form.chgDt        || null,
  createdBy:    userId ?? 'SYSTEM',
})

export default function SubscriptionPage() {
  const { user } = useAuth()
  const { isSearching, handleSearch, handleCreate, handleUpdate, handleDelete } = useSubscription()
  const [searchParams] = useSearchParams()
  const [items, setItems]               = useState([])
  const [selectedSubs, setSelectedSubs] = useState(null)
  const [formData, setFormData]         = useState(EMPTY_FORM)
  const [keyword, setKeyword]           = useState('')
  const [searchType, setSearchType]     = useState('SUBS_ID')
  const [errorMsg, setErrorMsg]         = useState(null)
  const [successMsg, setSuccessMsg]     = useState(null)
  const [confirmOpen, setConfirmOpen]   = useState(false)

  useEffect(() => {
    clearMessages()
    const subsId = searchParams.get('subsId')
    if (subsId) {
      setKeyword(subsId)
      setSearchType('SUBS_ID')
      handleSearch('SUBS_ID', subsId).then(result => setItems(result)).catch(() => setErrorMsg('조회에 실패했습니다.'))
    }
  }, [])

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const isRowSelected = Boolean(selectedSubs)

  // ── 검색 ──
  const onSearch = async () => {
    clearMessages()
    try {
      const result = await handleSearch(searchType, keyword.trim())
      setItems(result)
      setSelectedSubs(null)
      setFormData(EMPTY_FORM)
    } catch {
      setErrorMsg('조회에 실패했습니다.')
    }
  }

  // ── 행 선택 ──
  const onRowSelect = (item) => {
    setSelectedSubs(item)
    setFormData(toFormData(item))
  }

  const onFieldChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  // ── 등록 ──
  const onRegister = async () => {
    clearMessages()
    if (!formData.subsId.trim()) {
      setErrorMsg('가입ID는 필수값입니다.')
      return
    }
    try {
      const created = await handleCreate(toRequestDto(formData, user?.userId))
      setItems(prev => [...prev, created])
      setSelectedSubs(created)
      setFormData(toFormData(created))
      setSuccessMsg('등록이 완료되었습니다.')
    } catch (err) {
      const status = err?.response?.status
      setErrorMsg(status === 400
        ? (err?.response?.data?.message || '이미 등록된 가입ID입니다.')
        : '등록에 실패했습니다.')
    }
  }

  // ── 변경 ──
  const onUpdate = async () => {
    clearMessages()
    if (!isRowSelected) { setErrorMsg('가입을 선택해 주세요.'); return }
    try {
      const updated = await handleUpdate(selectedSubs.subsId, toRequestDto(formData, user?.userId))
      setItems(prev => prev.map(item => item.subsId === updated.subsId ? updated : item))
      setSelectedSubs(updated)
      setFormData(toFormData(updated))
      setSuccessMsg('변경이 완료되었습니다.')
    } catch {
      setErrorMsg('변경에 실패했습니다.')
    }
  }

  // ── 삭제 ──
  const onDeleteClick = () => {
    clearMessages()
    if (!isRowSelected) { setErrorMsg('가입을 선택해 주세요.'); return }
    setConfirmOpen(true)
  }

  const onDelete = async () => {
    try {
      await handleDelete(selectedSubs.subsId)
      setItems(prev => prev.filter(item => item.subsId !== selectedSubs.subsId))
      setSelectedSubs(null)
      setFormData(EMPTY_FORM)
      setSuccessMsg('삭제가 완료되었습니다.')
    } catch (err) {
      const status = err?.response?.status
      setErrorMsg(status === 409
        ? '과금기준이 존재하는 가입은 삭제할 수 없습니다.'
        : '삭제에 실패했습니다.')
    }
  }

  return (
    <MainLayout>
      <Toast message={successMsg}    type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-4">
        <h1 className="text-xl font-bold text-gray-800">가입 관리</h1>

        <SubscriptionSearchBar
          searchType={searchType}
          onSearchTypeChange={(e) => setSearchType(e.target.value)}
          keyword={keyword}
          onKeywordChange={(e) => setKeyword(e.target.value)}
          onSearch={onSearch}
          isSearching={isSearching}
        />

        <SubscriptionList
          items={items}
          selectedId={selectedSubs?.subsId}
          onSelect={onRowSelect}
        />

        <SubscriptionForm
          data={formData}
          onChange={onFieldChange}
          isReadOnly={isRowSelected}
        />
      </div>

      <SubscriptionActionBar
        onRegister={onRegister}
        onUpdate={onUpdate}
        onDelete={onDeleteClick}
      />

      {confirmOpen && (
        <ConfirmDialog
          message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
          onConfirm={() => { setConfirmOpen(false); onDelete() }}
          onCancel={() => setConfirmOpen(false)}
        />
      )}
    </MainLayout>
  )
}
