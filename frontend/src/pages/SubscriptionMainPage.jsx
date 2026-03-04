import { useState, useMemo } from 'react'
import useSubscriptionMain from '../hooks/useSubscriptionMain'
import { useAuth } from '../context/AuthContext'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import SubscriptionSearchPopup from '../components/common/SubscriptionSearchPopup'
import SubscriptionMainSearchBar from '../components/subscription-main/SubscriptionMainSearchBar'
import SubscriptionMainList from '../components/subscription-main/SubscriptionMainList'
import SubscriptionMainForm from '../components/subscription-main/SubscriptionMainForm'
import SubscriptionMainActionBar from '../components/subscription-main/SubscriptionMainActionBar'

const SVC_MAP = { 'IDC 전력': '서비스1', 'IDC NW': '서비스2', '비즈넷': '서비스3' }
const SVC_LABEL_MAP = Object.fromEntries(Object.entries(SVC_MAP).map(([k, v]) => [v, k]))

const EMPTY_FORM = {
  subsId: '',
  mainSubsYn: 'Y',
  mainSubsId: '',
  effStartDt: '',
  effEndDt: '',
}

const toFormData = (item) => ({
  subsId:     item.subsId     || '',
  mainSubsYn: item.mainSubsYn || 'Y',
  mainSubsId: item.mainSubsId || '',
  effStartDt: '',
  effEndDt:   '',
})

export default function SubscriptionMainPage() {
  const { user } = useAuth()
  const { isLoading, fetchList, handleSave } = useSubscriptionMain()

  const [svcNm, setSvcNm]           = useState('전체')
  const [searchType, setSearchType] = useState('가입ID')
  const [keyword, setKeyword]       = useState('')

  const [items, setItems]           = useState([])
  const [selected, setSelected]     = useState(null)
  const [formData, setFormData]     = useState(EMPTY_FORM)

  const displayItems = useMemo(
    () => items.map(item => ({ ...item, svcNm: SVC_LABEL_MAP[item.svcNm] || item.svcNm })),
    [items]
  )

  const [popupOpen, setPopupOpen]   = useState(false)
  const [successMsg, setSuccessMsg] = useState(null)
  const [errorMsg, setErrorMsg]     = useState(null)

  const getSearchError = () => {
    if (!keyword.trim()) return '조회조건을 입력해 주세요.'
    if (keyword.trim().length < 2) return '조회조건은 2자 이상 입력해 주세요.'
    return null
  }

  const handleSearch = async () => {
    const validationError = getSearchError()
    if (validationError) {
      setErrorMsg(validationError)
      return
    }
    setErrorMsg(null)
    setSuccessMsg(null)
    const params = {
      svcNm:      svcNm !== '전체' ? SVC_MAP[svcNm] : undefined,
      searchType: searchType || undefined,
      keyword:    keyword.trim(),
    }
    try {
      const result = await fetchList(params)
      setItems(result)
      setSelected(null)
      setFormData(EMPTY_FORM)
    } catch {
      setErrorMsg('조회에 실패했습니다.')
    }
  }

  const handleRowClick = (item) => {
    setSelected(item)
    setFormData(toFormData(item))
  }

  const handleFormChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handlePopupSelect = (subsId) => {
    setFormData(prev => ({ ...prev, mainSubsId: subsId }))
  }

  const handleSaveClick = async () => {
    setErrorMsg(null)
    if (!selected) {
      setErrorMsg('목록에서 가입을 선택해 주세요.')
      return
    }
    try {
      await handleSave({
        subsId:     formData.subsId,
        mainSubsYn: formData.mainSubsYn,
        mainSubsId: formData.mainSubsYn === 'Y' ? null : formData.mainSubsId,
        createdBy:  user?.userId ?? 'SYSTEM',
      })
      setSuccessMsg('저장이 완료되었습니다.')
      // 목록 갱신
      const params = {
        svcNm:      svcNm !== '전체' ? SVC_MAP[svcNm] : undefined,
        searchType: searchType || undefined,
        keyword:    keyword.trim(),
      }
      const result = await fetchList(params)
      setItems(result)
      const updated = result.find(r => r.subsId === formData.subsId)
      if (updated) {
        setSelected(updated)
        setFormData(toFormData(updated))
      }
    } catch (err) {
      const msg = err?.response?.data?.message
      setErrorMsg(msg || '저장에 실패했습니다.')
    }
  }

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <SubscriptionSearchPopup
        isOpen={popupOpen}
        onClose={() => setPopupOpen(false)}
        onSelect={handlePopupSelect}
      />

      <div className="space-y-4">
        <h1 className="text-xl font-bold text-gray-800">대표가입 관리</h1>

        <SubscriptionMainSearchBar
          svcNm={svcNm}
          onSvcNmChange={(e) => setSvcNm(e.target.value)}
          searchType={searchType}
          onSearchTypeChange={(e) => setSearchType(e.target.value)}
          keyword={keyword}
          onKeywordChange={(e) => setKeyword(e.target.value)}
          onSearch={handleSearch}
          isLoading={isLoading}
        />

        <SubscriptionMainList
          items={displayItems}
          selectedId={selected?.subsId}
          onRowClick={handleRowClick}
        />

        <SubscriptionMainForm
          data={formData}
          onChange={handleFormChange}
          onOpenPopup={() => setPopupOpen(true)}
        />
      </div>

      <SubscriptionMainActionBar onSave={handleSaveClick} />
    </MainLayout>
  )
}
