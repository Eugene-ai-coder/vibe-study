import { useState, useEffect, useCallback } from 'react'
import { getLogs, createLog, updateLog, deleteLog } from '../api/studyLogApi'

export default function useStudyLogs() {
  const [logs, setLogs] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)
  const [editingLog, setEditingLog] = useState(null)

  const fetchLogs = useCallback(() => {
    setIsLoading(true)
    setError(null)
    getLogs()
      .then(setLogs)
      .catch(() => setError('서버와 연결할 수 없습니다.'))
      .finally(() => setIsLoading(false))
  }, [])

  useEffect(() => {
    fetchLogs()
  }, [fetchLogs])

  const handleCreate = async (data) => {
    const newLog = await createLog(data)
    setLogs(prev => [...prev, newLog])
  }

  const handleDelete = async (id) => {
    await deleteLog(id)
    setLogs(prev => prev.filter(l => l.id !== id))
  }

  const handleUpdate = async (id, data) => {
    const updated = await updateLog(id, data)
    setLogs(prev => prev.map(l => l.id === updated?.id ? updated : l))
    setEditingLog(null)
  }

  return {
    logs,
    isLoading,
    error,
    editingLog,
    setEditingLog,
    fetchLogs,
    handleCreate,
    handleDelete,
    handleUpdate,
  }
}
