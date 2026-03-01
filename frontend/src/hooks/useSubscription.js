import { useState, useCallback } from 'react'
import { searchSubscriptions, createSubscription, updateSubscription, deleteSubscription } from '../api/subscriptionApi'

export default function useSubscription() {
  const [isSearching, setIsSearching] = useState(false)

  const handleSearch = useCallback(async (type, keyword) => {
    setIsSearching(true)
    try {
      return await searchSubscriptions(type, keyword)
    } finally {
      setIsSearching(false)
    }
  }, [])

  const handleCreate  = (data)       => createSubscription(data)
  const handleUpdate  = (id, data)   => updateSubscription(id, data)
  const handleDelete  = (id)         => deleteSubscription(id)

  return { isSearching, handleSearch, handleCreate, handleUpdate, handleDelete }
}
