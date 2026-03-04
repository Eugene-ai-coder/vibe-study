import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [userId, setUserId] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)
  const [errorMsg, setErrorMsg] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const saved = localStorage.getItem('savedUserId')
    if (saved) { setUserId(saved); setRememberMe(true) }
  }, [])

  const handleLogin = async () => {
    if (!userId.trim() || !password.trim()) {
      setErrorMsg('아이디와 비밀번호를 입력해 주세요.')
      return
    }
    setLoading(true)
    setErrorMsg('')
    try {
      await login(userId, password)
      if (rememberMe) localStorage.setItem('savedUserId', userId)
      else localStorage.removeItem('savedUserId')
      navigate('/main')
    } catch (err) {
      const status = err?.response?.status
      setErrorMsg(
        status === 403
          ? '사용이 제한된 계정입니다.'
          : '아이디 또는 비밀번호가 일치하지 않습니다.'
      )
    } finally {
      setLoading(false)
    }
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleLogin()
  }

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex items-center justify-center">
      <div className="bg-white rounded-lg shadow p-8 w-96">
        <h1 className="text-xl font-bold text-gray-800 mb-6 text-center">종량가입관리 시스템</h1>

        <div className="space-y-4">
          <input
            type="text"
            placeholder="아이디"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            onKeyDown={handleKeyDown}
            className="w-full h-10 px-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-blue-500"
          />
          <input
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={handleKeyDown}
            className="w-full h-10 px-3 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-blue-500"
          />

          <label className="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
            />
            아이디 저장
          </label>

          {errorMsg && (
            <p className="text-sm text-red-600">{errorMsg}</p>
          )}

          <button
            onClick={handleLogin}
            disabled={loading}
            className="w-full h-10 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-60"
          >
            {loading ? '로그인 중...' : '로그인'}
          </button>

          <p className="text-center text-xs text-gray-400 cursor-default select-none">
            계정 찾기
          </p>
        </div>
      </div>
    </div>
  )
}
