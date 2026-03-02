import { useState } from 'react'
import useBillStd from '../hooks/useBillStd'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import ConfirmDialog from '../components/common/ConfirmDialog'
import BillStdSearchBar from '../components/billstd/BillStdSearchBar'
import BillStdForm from '../components/billstd/BillStdForm'
import BillStdActionBar from '../components/billstd/BillStdActionBar'

const EMPTY_FORM = {
  billStdId: '', subsId: '', billStdRegDt: '', svcCd: '', lastEffYn: 'Y',
  effStartDt: '', effEndDt: '', stdRegStatCd: '', billStdStatCd: '',
  pwrMetCalcMethCd: '', uprcDetMethCd: '', meteringUnitPriceAmt: '', billQty: '',
  pueDetMethCd: '', pue1Rt: '', pue2Rt: '',
  firstDscRt: '', secondDscRt: '', lossCompRt: '',
  cntrcCapKmh: '', cntrcAmt: '', dscAmt: '', dailyUnitPrice: '',
}

// API 응답 → 폼 문자열 변환
const toFormData = (dto) =>
  Object.fromEntries(
    Object.keys(EMPTY_FORM).map((key) => [key, dto[key] != null ? String(dto[key]) : ''])
  )

// 폼 문자열 → API 요청 객체 변환
const toRequestDto = (form) => ({
  subsId:               form.subsId               || null,
  billStdRegDt:         form.billStdRegDt         || null,
  svcCd:                form.svcCd                || null,
  lastEffYn:            form.lastEffYn            || null,
  effStartDt:           form.effStartDt           || null,
  effEndDt:             form.effEndDt             || null,
  stdRegStatCd:         form.stdRegStatCd         || null,
  billStdStatCd:        form.billStdStatCd        || null,
  pwrMetCalcMethCd:     form.pwrMetCalcMethCd     || null,
  uprcDetMethCd:        form.uprcDetMethCd        || null,
  meteringUnitPriceAmt: form.meteringUnitPriceAmt ? parseFloat(form.meteringUnitPriceAmt) : null,
  billQty:              form.billQty              ? parseFloat(form.billQty)              : null,
  pueDetMethCd:         form.pueDetMethCd         || null,
  pue1Rt:               form.pue1Rt               ? parseFloat(form.pue1Rt)               : null,
  pue2Rt:               form.pue2Rt               ? parseFloat(form.pue2Rt)               : null,
  firstDscRt:           form.firstDscRt           ? parseFloat(form.firstDscRt)           : null,
  secondDscRt:          form.secondDscRt          ? parseFloat(form.secondDscRt)          : null,
  lossCompRt:           form.lossCompRt           ? parseFloat(form.lossCompRt)           : null,
  cntrcCapKmh:          form.cntrcCapKmh          ? parseFloat(form.cntrcCapKmh)          : null,
  cntrcAmt:             form.cntrcAmt             ? parseFloat(form.cntrcAmt)             : null,
  dscAmt:               form.dscAmt               ? parseFloat(form.dscAmt)               : null,
  dailyUnitPrice:       form.dailyUnitPrice       ? parseFloat(form.dailyUnitPrice)       : null,
  createdBy:            'SYSTEM',
})

export default function BillStdPage() {
  const { isSearching, searchBySubsId, searchById, handleCreate, handleUpdate, handleDelete } = useBillStd()
  const [formData, setFormData]             = useState(EMPTY_FORM)
  const [keyword, setKeyword]               = useState('')
  const [searchType, setSearchType]         = useState('subsId')
  const [errorMsg, setErrorMsg]       = useState(null)
  const [successMsg, setSuccessMsg]   = useState(null)
  const [confirmOpen, setConfirmOpen] = useState(false)

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const handleSearch = async () => {
    clearMessages()
    if (!keyword.trim()) {
      setErrorMsg('검색어를 입력해 주세요.')
      return
    }
    try {
      const found = searchType === 'subsId'
        ? await searchBySubsId(keyword.trim())
        : await searchById(keyword.trim())
      setFormData(toFormData(found))
      setSuccessMsg('조회가 완료되었습니다.')
    } catch (err) {
      const status = err?.response?.status
      setErrorMsg(status === 404 ? '조회 결과가 없습니다.' : '서버와 연결할 수 없습니다.')
      setFormData(EMPTY_FORM)
    }
  }

  const handleFieldChange = (e) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSave = async () => {
    clearMessages()
    try {
      const created = await handleCreate(toRequestDto(formData))
      setFormData(toFormData(created))
      setSuccessMsg('저장이 완료되었습니다.')
    } catch {
      setErrorMsg('저장에 실패했습니다.')
    }
  }

  const handleChange = async () => {
    if (!formData.billStdId) { setErrorMsg('조회 후 변경할 수 있습니다.'); return }
    clearMessages()
    try {
      const updated = await handleUpdate(formData.billStdId, toRequestDto(formData))
      setFormData(toFormData(updated))
      setSuccessMsg('변경이 완료되었습니다.')
    } catch {
      setErrorMsg('변경에 실패했습니다.')
    }
  }

  const handleDeleteClick = () => {
    if (!formData.billStdId) { setErrorMsg('조회 후 삭제할 수 있습니다.'); return }
    clearMessages()
    setConfirmOpen(true)
  }

  const executeDelete = async () => {
    try {
      await handleDelete(formData.billStdId)
      setFormData(EMPTY_FORM)
      setSuccessMsg('삭제가 완료되었습니다.')
    } catch (err) {
      const status = err?.response?.status
      setErrorMsg(
        status === 409
          ? '다른 이력이 존재하여 삭제할 수 없습니다.'
          : '삭제에 실패했습니다.'
      )
    }
  }

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-4 pb-20">
        <h1 className="text-xl font-bold text-gray-800">가입별 과금기준 관리</h1>

        <BillStdSearchBar
          keyword={keyword}
          onKeywordChange={(e) => setKeyword(e.target.value)}
          searchType={searchType}
          onSearchTypeChange={(e) => setSearchType(e.target.value)}
          onSearch={handleSearch}
          isSearching={isSearching}
        />

        <BillStdForm data={formData} onChange={handleFieldChange} />
      </div>

      <BillStdActionBar
        onSave={handleSave}
        onChange={handleChange}
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
