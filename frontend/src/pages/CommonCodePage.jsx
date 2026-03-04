import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import useCommonCode from '../hooks/useCommonCode'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import ConfirmDialog from '../components/common/ConfirmDialog'
import CommonCodeList from '../components/common-code/CommonCodeList'
import CommonDtlCodeList from '../components/common-code/CommonDtlCodeList'
import CommonCodeForm from '../components/common-code/CommonCodeForm'
import CommonDtlCodeForm from '../components/common-code/CommonDtlCodeForm'
import CommonCodeActionBar from '../components/common-code/CommonCodeActionBar'

const EMPTY_CODE_FORM = { commonCode: '', commonCodeNm: '', effStartDt: '', effEndDt: '', remark: '' }
const EMPTY_DTL_FORM  = { commonDtlCode: '', commonDtlCodeNm: '', sortOrder: 0, effStartDt: '', effEndDt: '', remark: '' }

export default function CommonCodePage() {
  const { user } = useAuth()
  const {
    codes, details, isLoading,
    fetchCodes, fetchDetails,
    createCode, updateCode, deleteCode,
    createDetail, updateDetail, deleteDetail,
  } = useCommonCode()

  const [searchCode, setSearchCode]     = useState('')
  const [searchCodeNm, setSearchCodeNm] = useState('')
  const [selectedCode, setSelectedCode] = useState(null)
  const [selectedDtl, setSelectedDtl]   = useState(null)
  const [codeForm, setCodeForm]         = useState(EMPTY_CODE_FORM)
  const [dtlForm, setDtlForm]           = useState(EMPTY_DTL_FORM)
  const [codeMode, setCodeMode]           = useState('view')   // 'new' | 'edit' | 'view'
  const [dtlMode, setDtlMode]             = useState('view')
  const [dtlConfirmOpen, setDtlConfirmOpen] = useState(false)
  const [successMsg, setSuccessMsg]       = useState(null)
  const [errorMsg, setErrorMsg]           = useState(null)

  const clearMessages = () => { setSuccessMsg(null); setErrorMsg(null) }

  const handleSearch = async () => {
    clearMessages()
    try {
      const params = {}
      if (searchCode)   params.commonCode   = searchCode
      if (searchCodeNm) params.commonCodeNm = searchCodeNm
      await fetchCodes(params)
      setSelectedCode(null)
      setSelectedDtl(null)
      setCodeForm(EMPTY_CODE_FORM)
      setDtlForm(EMPTY_DTL_FORM)
      setCodeMode('view')
      setDtlMode('view')
    } catch {
      setErrorMsg('조회에 실패했습니다.')
    }
  }

  const handleCodeRowClick = async (code) => {
    setSelectedCode(code)
    setCodeForm({ ...code })
    setSelectedDtl(null)
    setDtlForm(EMPTY_DTL_FORM)
    setCodeMode('edit')
    setDtlMode('view')
    try {
      await fetchDetails(code.commonCode)
    } catch {
      setErrorMsg('상세코드 조회에 실패했습니다.')
    }
  }

  const handleDtlRowClick = (dtl) => {
    setSelectedDtl(dtl)
    setDtlForm({ ...dtl })
    setDtlMode('edit')
  }

  const handleCodeFormChange = (e) => {
    const { name, value } = e.target
    setCodeForm(prev => ({ ...prev, [name]: value }))
  }

  const handleDtlFormChange = (e) => {
    const { name, value } = e.target
    setDtlForm(prev => ({ ...prev, [name]: name === 'sortOrder' ? Number(value) : value }))
  }

  const handleCodeNewClick = () => {
    setCodeMode('new')
    setCodeForm(EMPTY_CODE_FORM)
  }

  const handleDtlDeleteConfirm = async () => {
    setDtlConfirmOpen(false)
    clearMessages()
    try {
      await deleteDetail(selectedCode.commonCode, selectedDtl.commonDtlCode)
      setSelectedDtl(null)
      setDtlForm(EMPTY_DTL_FORM)
      setDtlMode('view')
      await fetchDetails(selectedCode.commonCode)
      setSuccessMsg('상세코드가 삭제되었습니다.')
    } catch {
      setErrorMsg('상세코드 삭제에 실패했습니다.')
    }
  }

  const handleSave = async () => {
    clearMessages()
    const createdBy = user?.userId ?? 'SYSTEM'
    try {
      if (codeMode === 'new') {
        await createCode({ ...codeForm, createdBy })
        setSuccessMsg('공통코드가 등록되었습니다.')
        setCodeMode('view')
        const params = {}
        if (searchCode)   params.commonCode   = searchCode
        if (searchCodeNm) params.commonCodeNm = searchCodeNm
        await fetchCodes(params)
        setCodeForm(EMPTY_CODE_FORM)
      } else if (codeMode === 'edit') {
        await updateCode(codeForm.commonCode, { ...codeForm, createdBy })
        setSuccessMsg('공통코드가 수정되었습니다.')
        const params = {}
        if (searchCode)   params.commonCode   = searchCode
        if (searchCodeNm) params.commonCodeNm = searchCodeNm
        await fetchCodes(params)
      }
    } catch (err) {
      setErrorMsg(err?.response?.data?.message || '저장에 실패했습니다.')
      return
    }

    if (dtlMode !== 'view' && selectedCode) {
      try {
        if (dtlMode === 'new') {
          await createDetail(selectedCode.commonCode, { ...dtlForm, createdBy })
          setSuccessMsg('상세코드가 등록되었습니다.')
          setDtlMode('view')
          setDtlForm(EMPTY_DTL_FORM)
        } else if (dtlMode === 'edit') {
          await updateDetail(selectedCode.commonCode, dtlForm.commonDtlCode, { ...dtlForm, createdBy })
          setSuccessMsg('상세코드가 수정되었습니다.')
        }
        await fetchDetails(selectedCode.commonCode)
      } catch (err) {
        setErrorMsg(err?.response?.data?.message || '상세코드 저장에 실패했습니다.')
      }
    }
  }

  const handleCancel = () => {
    setCodeMode('view')
    setDtlMode('view')
    setCodeForm(selectedCode ? { ...selectedCode } : EMPTY_CODE_FORM)
    setDtlForm(selectedDtl ? { ...selectedDtl } : EMPTY_DTL_FORM)
  }

  const activeMode = codeMode !== 'view' || dtlMode !== 'view' ? 'active' : 'view'

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-4">
        <h1 className="text-xl font-bold text-gray-800">공통코드 관리</h1>

        {/* 조회영역 */}
        <div className="bg-white rounded-lg shadow-sm p-4 flex items-end gap-4">
          <div>
            <label className="block text-xs text-gray-500 mb-1">공통코드</label>
            <input
              value={searchCode}
              onChange={e => setSearchCode(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleSearch()}
              className="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
            />
          </div>
          <div>
            <label className="block text-xs text-gray-500 mb-1">공통코드명</label>
            <input
              value={searchCodeNm}
              onChange={e => setSearchCodeNm(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleSearch()}
              className="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
            />
          </div>
          <button
            onClick={handleSearch}
            disabled={isLoading}
            className="h-8 px-4 bg-blue-600 text-white text-sm rounded disabled:opacity-50"
          >
            조회
          </button>
        </div>

        {/* 마스터-디테일 목록 영역 */}
        <div className="flex gap-4">
          {/* 좌측: 공통코드 목록 */}
          <div className="w-1/3 space-y-2">
            <CommonCodeList
              codes={codes}
              selectedCode={selectedCode?.commonCode}
              onRowClick={handleCodeRowClick}
            />
            <div className="flex justify-end">
              <button
                onClick={handleCodeNewClick}
                className="h-8 px-4 bg-blue-600 text-white text-sm rounded"
              >
                등록
              </button>
            </div>
          </div>

          {/* 우측: 공통상세코드 목록 */}
          <div className="w-2/3 space-y-2">
            <CommonDtlCodeList
              details={details}
              selectedDtl={selectedDtl?.commonDtlCode}
              onRowClick={handleDtlRowClick}
            />
            <div className="flex justify-end gap-2">
              <button
                onClick={() => { setDtlMode('new'); setDtlForm(EMPTY_DTL_FORM) }}
                disabled={!selectedCode}
                className="h-8 px-4 bg-blue-600 text-white text-sm rounded disabled:opacity-40 disabled:cursor-not-allowed"
              >
                등록
              </button>
              <button
                onClick={() => selectedDtl && setDtlMode('edit')}
                disabled={!selectedDtl}
                className="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                수정
              </button>
              <button
                onClick={() => selectedDtl && setDtlConfirmOpen(true)}
                disabled={!selectedDtl}
                className="h-8 px-4 border border-red-300 text-sm rounded text-red-600 hover:bg-red-50 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                삭제
              </button>
            </div>
          </div>
        </div>

        {/* 폼 영역 */}
        {codeMode !== 'view' && (
          <CommonCodeForm
            data={codeForm}
            onChange={handleCodeFormChange}
            isEdit={codeMode === 'edit'}
          />
        )}
        {dtlMode !== 'view' && (
          <CommonDtlCodeForm
            data={dtlForm}
            onChange={handleDtlFormChange}
            isEdit={dtlMode === 'edit'}
          />
        )}
      </div>

      <CommonCodeActionBar
        onSave={handleSave}
        onCancel={handleCancel}
        activeMode={activeMode}
      />

      {dtlConfirmOpen && (
        <ConfirmDialog
          message="상세코드를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
          onConfirm={handleDtlDeleteConfirm}
          onCancel={() => setDtlConfirmOpen(false)}
        />
      )}
    </MainLayout>
  )
}
