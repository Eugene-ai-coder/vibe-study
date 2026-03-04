import { useState, useCallback } from 'react'
import { getUsersPage, register } from '../api/authApi'

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }

export default function useUser() {
  const [users, setUsers] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [form, setForm] = useState(EMPTY_FORM)
  const [errorMsg, setErrorMsg] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)

  const pageSize = 10

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const fetchUsers = useCallback(async (searchParams = {}, pageNum = 0) => {
    try {
      const params = {
        page: pageNum,
        size: pageSize,
        ...searchParams,
      }
      const data = await getUsersPage(params)
      setUsers(data.content)
      setPage(data.number)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch {
      setErrorMsg('사용자 목록 조회에 실패했습니다.')
    }
  }, [])

  const handleRegister = async (createdBy) => {
    clearMessages()
    try {
      await register({ ...form, createdBy: createdBy ?? 'SYSTEM' })
      setSuccessMsg('사용자가 등록되었습니다.')
      setForm(EMPTY_FORM)
      return true
    } catch (err) {
      setErrorMsg(err?.response?.data?.message || '사용자 등록에 실패했습니다.')
      return false
    }
  }

  return {
    users, page, totalPages, totalElements, pageSize,
    form, setForm,
    errorMsg, setErrorMsg,
    successMsg, setSuccessMsg,
    fetchUsers, handleRegister,
  }
}
