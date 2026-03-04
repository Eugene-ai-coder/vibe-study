import { useState, useCallback } from 'react'
import { commonCodeApi } from '../api/commonCodeApi'

export default function useCommonCode() {
  const [codes, setCodes]       = useState([])
  const [details, setDetails]   = useState([])
  const [isLoading, setIsLoading] = useState(false)

  const fetchCodes = useCallback(async (params = {}) => {
    setIsLoading(true)
    try {
      const data = await commonCodeApi.getAll(params)
      setCodes(data)
      return data
    } finally {
      setIsLoading(false)
    }
  }, [])

  const fetchDetails = useCallback(async (commonCode) => {
    const data = await commonCodeApi.getDetails(commonCode)
    setDetails(data)
  }, [])

  const createCode = useCallback(async (data) => {
    return commonCodeApi.create(data)
  }, [])

  const updateCode = useCallback(async (code, data) => {
    return commonCodeApi.update(code, data)
  }, [])

  const deleteCode = useCallback(async (code) => {
    await commonCodeApi.delete(code)
  }, [])

  const createDetail = useCallback(async (code, data) => {
    return commonCodeApi.createDetail(code, data)
  }, [])

  const updateDetail = useCallback(async (code, dtl, data) => {
    return commonCodeApi.updateDetail(code, dtl, data)
  }, [])

  const deleteDetail = useCallback(async (code, dtl) => {
    await commonCodeApi.deleteDetail(code, dtl)
  }, [])

  return {
    codes, details, isLoading,
    fetchCodes, fetchDetails,
    createCode, updateCode, deleteCode,
    createDetail, updateDetail, deleteDetail,
  }
}
