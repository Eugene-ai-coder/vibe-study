import { useState, useEffect, useCallback } from 'react'
import { getUsers, register } from '../api/authApi'

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }

export default function useUser() {
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [errorMsg, setErrorMsg] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const fetchUsers = useCallback(async () => {
    try {
      setUsers(await getUsers())
    } catch {
      setErrorMsg('사용자 목록 조회에 실패했습니다.')
    }
  }, [])

  useEffect(() => { fetchUsers() }, [fetchUsers])

  const handleRegister = async () => {
    clearMessages()
    try {
      await register(form)
      setSuccessMsg('사용자가 등록되었습니다.')
      setForm(EMPTY_FORM)
      fetchUsers()
    } catch (err) {
      setErrorMsg(err?.response?.data?.message || '사용자 등록에 실패했습니다.')
    }
  }

  return {
    users, form, setForm,
    errorMsg, setErrorMsg,
    successMsg, setSuccessMsg,
    handleRegister,
  }
}
