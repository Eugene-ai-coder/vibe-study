import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import Sidebar from '../main/Sidebar'

export default function MainLayout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <div className="flex flex-col min-h-screen">
      {/* 헤더 */}
      <header className="h-14 bg-white border-b border-gray-200 flex items-center justify-between px-6 flex-shrink-0">
        <span className="font-bold text-gray-800">종량가입관리 시스템</span>
        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600">
            <strong className="text-gray-800">{user?.nickname}</strong>님
          </span>
          <button
            onClick={handleLogout}
            className="text-sm text-gray-500 hover:text-red-600 border border-gray-300 hover:border-red-400 px-3 h-8 rounded-lg transition-colors"
          >
            로그아웃
          </button>
        </div>
      </header>

      {/* 바디 */}
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-auto bg-[#F8FAFC] p-6">
          {children}
        </main>
      </div>
    </div>
  )
}
