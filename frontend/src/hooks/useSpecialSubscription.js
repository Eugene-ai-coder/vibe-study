import { useState, useCallback } from 'react'
import {
  getSpecialSubscriptions,
  getSpecialSubscription,
  createSpecialSubscription,
  updateSpecialSubscription,
  deleteSpecialSubscription,
} from '../api/specialSubscriptionApi'

export default function useSpecialSubscription() {
  const [items, setItems] = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const fetchList = useCallback(async (params = {}) => {
    setIsLoading(true)
    try {
      const data = await getSpecialSubscriptions(params)
      setItems(data)
      return data
    } finally {
      setIsLoading(false)
    }
  }, [])

  const fetchOne = useCallback(async (subsBillStdId, effStaDt) => {
    return getSpecialSubscription(subsBillStdId, effStaDt)
  }, [])

  const handleCreate = useCallback(async (data) => {
    return createSpecialSubscription(data)
  }, [])

  const handleUpdate = useCallback(async (subsBillStdId, effStaDt, data) => {
    return updateSpecialSubscription(subsBillStdId, effStaDt, data)
  }, [])

  const handleDelete = useCallback(async (subsBillStdId, effStaDt) => {
    return deleteSpecialSubscription(subsBillStdId, effStaDt)
  }, [])

  return { items, isLoading, fetchList, fetchOne, handleCreate, handleUpdate, handleDelete }
}
