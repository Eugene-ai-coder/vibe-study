import { useState, useEffect, useMemo, useCallback } from 'react'
import useUser from '../hooks/useUser'
import { useAuth } from '../context/AuthContext'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import DataGrid from '../components/common/DataGrid'
import FloatingActionBar from '../components/common/FloatingActionBar'

const STATUS_LABEL = { 1: '활성', 2: '정지', 0: '탈퇴' }

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }

export default function UserPage() {
  const { user } = useAuth()
  const {
    users, page, totalPages, totalElements, pageSize,
    form, setForm,
    errorMsg, setErrorMsg,
    successMsg, setSuccessMsg,
    fetchUsers, handleRegister,
  } = useUser()

  const [searchUserId, setSearchUserId]     = useState('')
  const [searchNickname, setSearchNickname] = useState('')
  const [searchEmail, setSearchEmail]       = useState('')

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const getSearchParams = useCallback(() => {
    const params = {}
    if (searchUserId)   params.userId   = searchUserId
    if (searchNickname) params.nickname = searchNickname
    if (searchEmail)    params.email    = searchEmail
    return params
  }, [searchUserId, searchNickname, searchEmail])

  /* 초기 조회 */
  useEffect(() => { fetchUsers() }, [fetchUsers])

  const handleSearch = () => {
    clearMessages()
    fetchUsers(getSearchParams(), 0)
  }

  const handlePageChange = (newPage) => {
    fetchUsers(getSearchParams(), newPage)
  }

  const onFieldChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const onRegister = async () => {
    const ok = await handleRegister(user?.userId)
    if (ok) fetchUsers(getSearchParams(), 0)
  }

  /* 컬럼 정의 — pinnedCount=2 → userId, nickname 고정 */
  const columns = useMemo(() => [
    { accessorKey: 'userId',   header: '아이디', size: 120, minSize: 60 },
    { accessorKey: 'nickname', header: '닉네임', size: 120, minSize: 60 },
    { accessorKey: 'email',    header: '이메일', size: 200, minSize: 80 },
    {
      accessorKey: 'accountStatus',
      header: '상태',
      size: 80,
      minSize: 60,
      cell: ({ getValue }) => {
        const v = getValue()
        const cls = v === 1 ? 'bg-green-100 text-green-700'
                  : v === 2 ? 'bg-yellow-100 text-yellow-700'
                  : 'bg-red-100 text-red-700'
        return (
          <span className={`px-2 py-0.5 rounded text-xs font-medium ${cls}`}>
            {STATUS_LABEL[v] ?? v}
          </span>
        )
      },
      enableColumnFilter: false,
    },
    { accessorKey: 'createdBy', header: '등록자', size: 100, minSize: 60 },
    {
      accessorKey: 'createdDt',
      header: '등록일시',
      size: 150,
      minSize: 100,
      cell: ({ getValue }) => {
        const v = getValue()
        return v ? v.replace('T', ' ').substring(0, 19) : ''
      },
      enableColumnFilter: false,
    },
  ], [])

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-6">
        <h1 className="text-xl font-bold text-gray-800">사용자 관리</h1>

        {/* 조회영역 */}
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-3">
          <div className="flex items-center gap-2">
            <div>
              <label className="block text-xs text-gray-500 mb-1">사용자ID</label>
              <input
                value={searchUserId}
                onChange={e => setSearchUserId(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSearch()}
                className="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
              />
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">사용자명</label>
              <input
                value={searchNickname}
                onChange={e => setSearchNickname(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSearch()}
                className="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
              />
            </div>
            <div>
              <label className="block text-xs text-gray-500 mb-1">이메일</label>
              <input
                value={searchEmail}
                onChange={e => setSearchEmail(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSearch()}
                className="h-8 px-3 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
              />
            </div>
            <div className="self-end">
              <button
                onClick={handleSearch}
                className="h-8 px-4 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
              >
                조회
              </button>
            </div>
          </div>
        </div>

        {/* 목록 (DataGrid) */}
        <DataGrid
          columns={columns}
          data={users}
          pinnedCount={2}
          page={page}
          totalPages={totalPages}
          totalElements={totalElements}
          pageSize={pageSize}
          onPageChange={handlePageChange}
          rowIdAccessor="userId"
          storageKey="userPage"
          title="사용자 목록"
        />

        {/* 신규 등록 폼 */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">신규 사용자 등록</h3>
          <div className="grid grid-cols-3 gap-x-4 gap-y-3">
            {[
              { name: 'userId',   label: '아이디',   type: 'text' },
              { name: 'nickname', label: '닉네임',   type: 'text' },
              { name: 'password', label: '비밀번호', type: 'password' },
              { name: 'email',    label: '이메일',   type: 'email' },
            ].map(({ name, label, type }) => (
              <div key={name}>
                <label className="block text-xs text-gray-500 mb-1">{label} <span className="text-blue-400">*</span></label>
                <input
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={onFieldChange}
                  className="w-full h-8 px-2 border border-gray-300 rounded text-sm focus:outline-none focus:border-blue-400"
                />
              </div>
            ))}
          </div>
        </div>
      </div>

      <FloatingActionBar>
        <button
          onClick={() => setForm(EMPTY_FORM)}
          className="h-8 px-4 border border-gray-300 text-sm rounded text-gray-600 hover:bg-gray-50 transition-colors"
        >
          취소
        </button>
        <button
          onClick={onRegister}
          className="h-8 px-6 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition-colors"
        >
          등록
        </button>
      </FloatingActionBar>
    </MainLayout>
  )
}
