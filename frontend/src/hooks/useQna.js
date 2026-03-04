import { useState, useCallback } from 'react'
import { qnaApi } from '../api/qnaApi'

export default function useQna() {
  const [isLoading, setIsLoading] = useState(false)

  const fetchList = useCallback(async (params = {}) => {
    setIsLoading(true)
    try {
      return await qnaApi.getAll(params)
    } finally {
      setIsLoading(false)
    }
  }, [])

  const fetchOne = useCallback(async (qnaId) => {
    return qnaApi.getOne(qnaId)
  }, [])

  const createQna = useCallback(async (data) => {
    return qnaApi.create(data)
  }, [])

  const updateQna = useCallback(async (qnaId, data) => {
    return qnaApi.update(qnaId, data)
  }, [])

  const deleteQna = useCallback(async (qnaId) => {
    await qnaApi.delete(qnaId)
  }, [])

  const fetchComments = useCallback(async (qnaId) => {
    return qnaApi.getComments(qnaId)
  }, [])

  const createComment = useCallback(async (qnaId, data) => {
    await qnaApi.createComment(qnaId, data)
  }, [])

  const deleteComment = useCallback(async (qnaId, commentId) => {
    await qnaApi.deleteComment(qnaId, commentId)
  }, [])

  return {
    isLoading,
    fetchList, fetchOne,
    createQna, updateQna, deleteQna,
    fetchComments, createComment, deleteComment,
  }
}
