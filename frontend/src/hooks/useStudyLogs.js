import { useState, useEffect, useCallback } from 'react'
import { getLogs, createLog, updateLog, deleteLog } from '../api/studyLogApi'

export default function useStudyLogs() {
  const [logs, setLogs] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)
  const [editingLog, setEditingLog] = useState(null)

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const fetchLogs = useCallback(() => {
    setIsLoading(true)
    clearMessages()
    getLogs()
      .then(setLogs)
      .catch(() => setErrorMsg('서버와 연결할 수 없습니다.'))
      .finally(() => setIsLoading(false))
  }, [])

  useEffect(() => {
    fetchLogs()
  }, [fetchLogs])

  const handleCreate = async (data) => {
    clearMessages()
    try {
      const newLog = await createLog(data)
      setLogs(prev => [...prev, newLog])
      setSuccessMsg('등록이 완료되었습니다.')
    } catch (err) {
      const status = err?.response?.status
      if (status === 400) {
        setErrorMsg(err?.response?.data?.message || '입력값을 확인해 주세요.')
      } else {
        setErrorMsg('저장에 실패했습니다.')
      }
      throw err
    }
  }

  const handleDelete = async (id) => {
    clearMessages()
    try {
      await deleteLog(id)
      setLogs(prev => prev.filter(l => l.id !== id))
      setSuccessMsg('삭제가 완료되었습니다.')
    } catch {
      setErrorMsg('삭제에 실패했습니다.')
    }
  }

  const handleUpdate = async (id, data) => {
    clearMessages()
    try {
      const updated = await updateLog(id, data)
      setLogs(prev => prev.map(l => l.id === updated?.id ? updated : l))
      setEditingLog(null)
      setSuccessMsg('수정이 완료되었습니다.')
    } catch (err) {
      const status = err?.response?.status
      if (status === 400) {
        setErrorMsg(err?.response?.data?.message || '입력값을 확인해 주세요.')
      } else {
        setErrorMsg('수정에 실패했습니다.')
      }
      throw err
    }
  }

  return {
    logs,
    isLoading,
    errorMsg,
    successMsg,
    setErrorMsg,
    setSuccessMsg,
    editingLog,
    setEditingLog,
    fetchLogs,
    handleCreate,
    handleDelete,
    handleUpdate,
  }
}
