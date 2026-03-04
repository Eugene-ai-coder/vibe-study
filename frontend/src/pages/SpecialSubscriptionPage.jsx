import { useState } from 'react'
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

      <div className="space-y-4 pb-24">
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
