import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <header className="bg-white border-b border-gray-200">
      <div className="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
        <nav className="flex items-center gap-4">
          <NavLink
            to="/study-logs"
            className={({ isActive }) =>
              isActive ? 'text-blue-700 font-bold' : 'text-gray-500 hover:text-blue-600'
            }
          >
            학습 로그
          </NavLink>
          <NavLink
            to="/bill-std"
            className={({ isActive }) =>
              isActive ? 'text-blue-700 font-bold' : 'text-gray-500 hover:text-blue-600'
            }
          >
            과금 기준
          </NavLink>
          <NavLink
            to="/subscriptions"
            className={({ isActive }) =>
              isActive ? 'text-blue-700 font-bold' : 'text-gray-500 hover:text-blue-600'
            }
          >
            가입 관리
          </NavLink>
        </nav>

        <div className="flex items-center gap-3">
          <span className="text-sm text-gray-600">
            안녕하세요, <strong className="text-gray-800">{user?.nickname}</strong>님
          </span>
          <button
            onClick={handleLogout}
            className="text-sm text-gray-500 hover:text-red-600 border border-gray-300 hover:border-red-400 px-3 py-1.5 rounded-lg transition-colors"
          >
            로그아웃
          </button>
        </div>
      </div>
    </header>
  )
}
