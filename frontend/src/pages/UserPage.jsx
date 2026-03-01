import { useState, useEffect } from 'react'
import { getUsers, register } from '../api/authApi'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }

const STATUS_LABEL = { 1: '활성', 2: '정지', 0: '탈퇴' }

export default function UserPage() {
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [errorMsg, setErrorMsg] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)

  const fetchUsers = async () => {
    try {
      setUsers(await getUsers())
    } catch {
      setErrorMsg('사용자 목록 조회에 실패했습니다.')
    }
  }

  useEffect(() => { fetchUsers() }, [])

  const onFieldChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const handleRegister = async () => {
    setErrorMsg(null)
    setSuccessMsg(null)
    try {
      await register(form)
      setSuccessMsg('사용자가 등록되었습니다.')
      setForm(EMPTY_FORM)
      fetchUsers()
    } catch (err) {
      setErrorMsg(err?.response?.data?.message || '사용자 등록에 실패했습니다.')
    }
  }

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />

      <div className="space-y-6">
        <h1 className="text-xl font-bold text-gray-800">사용자 관리</h1>

        {/* 목록 */}
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                {['아이디', '닉네임', '이메일', '상태'].map(h => (
                  <th key={h} className="px-4 py-2 text-left text-gray-600 font-medium">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {users.length === 0 ? (
                <tr><td colSpan={4} className="px-4 py-6 text-center text-gray-400">데이터가 없습니다.</td></tr>
              ) : users.map(u => (
                <tr key={u.userId} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="px-4 py-2">{u.userId}</td>
                  <td className="px-4 py-2">{u.nickname}</td>
                  <td className="px-4 py-2">{u.email}</td>
                  <td className="px-4 py-2">
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${
                      u.accountStatus === 1 ? 'bg-green-100 text-green-700' :
                      u.accountStatus === 2 ? 'bg-yellow-100 text-yellow-700' :
                      'bg-red-100 text-red-700'
                    }`}>
                      {STATUS_LABEL[u.accountStatus] ?? u.accountStatus}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 신규 등록 폼 */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-sm font-semibold text-gray-700 mb-4">신규 사용자 등록</h2>
          <div className="grid grid-cols-2 gap-4">
            {[
              { name: 'userId',   label: '아이디',   type: 'text' },
              { name: 'nickname', label: '닉네임',   type: 'text' },
              { name: 'password', label: '비밀번호', type: 'password' },
              { name: 'email',    label: '이메일',   type: 'email' },
            ].map(({ name, label, type }) => (
              <div key={name}>
                <label className="block text-xs text-gray-500 mb-1">{label}</label>
                <input
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={onFieldChange}
                  className="w-full h-10 px-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-blue-500"
                />
              </div>
            ))}
          </div>

          <div className="mt-4 flex justify-end">
            <button
              onClick={handleRegister}
              className="h-10 px-6 bg-[#2563EB] hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors"
            >
              등록
            </button>
          </div>
        </div>
      </div>
    </MainLayout>
  )
}
