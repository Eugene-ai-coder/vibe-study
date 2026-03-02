import { useState } from 'react'
import { searchSubscriptions } from '../api/subscriptionApi'

export default function useSubscriptionSearch() {
  const [results, setResults] = useState([])
  const [isSearching, setIsSearching] = useState(false)

  const search = async (keyword) => {
    if (!keyword.trim()) return
    setIsSearching(true)
    try {
      const data = await searchSubscriptions('SUBS_ID', keyword.trim())
      setResults(data)
    } catch {
      setResults([])
    } finally {
      setIsSearching(false)
    }
  }

  const reset = () => setResults([])

  return { results, isSearching, search, reset }
}
