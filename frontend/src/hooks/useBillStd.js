import { useState, useCallback } from 'react'
import { getBillStd, getBillStdBySubsId, createBillStd, updateBillStd, deleteBillStd } from '../api/billStdApi'

export default function useBillStd() {
  const [isSearching, setIsSearching] = useState(false)

  const searchBySubsId = useCallback(async (subsId) => {
    setIsSearching(true)
    try {
      return await getBillStdBySubsId(subsId)
    } finally {
      setIsSearching(false)
    }
  }, [])

  const searchById = useCallback(async (billStdId) => {
    setIsSearching(true)
    try {
      return await getBillStd(billStdId)
    } finally {
      setIsSearching(false)
    }
  }, [])

  const handleCreate = (data) => createBillStd(data)
  const handleUpdate = (id, data) => updateBillStd(id, data)
  const handleDelete = (id) => deleteBillStd(id)

  return { isSearching, searchBySubsId, searchById, handleCreate, handleUpdate, handleDelete }
}
