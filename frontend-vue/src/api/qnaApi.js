import apiClient from './apiClient'

export const qnaApi = {
  getAll:        (params)             => apiClient.get('/qna', { params }).then(r => r.data),
  getOne:        (qnaId)              => apiClient.get(`/qna/${qnaId}`).then(r => r.data),
  create:        (data)               => apiClient.post('/qna', data).then(r => r.data),
  update:        (qnaId, data)        => apiClient.put(`/qna/${qnaId}`, data).then(r => r.data),
  delete:        (qnaId)              => apiClient.delete(`/qna/${qnaId}`).then(r => r.data),
  getComments:   (qnaId)              => apiClient.get(`/qna/${qnaId}/comments`).then(r => r.data),
  createComment: (qnaId, data)        => apiClient.post(`/qna/${qnaId}/comments`, data).then(r => r.data),
  deleteComment: (qnaId, commentId)   => apiClient.delete(`/qna/${qnaId}/comments/${commentId}`).then(r => r.data),
}
