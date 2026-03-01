import { useState, useCallback } from 'react'
import { getSubscriptionMainList, saveSubscriptionMain } from '../api/subscriptionMainApi'

export default function useSubscriptionMain() {
  const [isLoading, setIsLoading] = useState(false)

  const fetchList = useCallback(async (params) => {
    setIsLoading(true)
    try { return await getSubscriptionMainList(params) }
    finally { setIsLoading(false) }
  }, [])

  const handleSave = (data) => saveSubscriptionMain(data)

  return { isLoading, fetchList, handleSave }
}
