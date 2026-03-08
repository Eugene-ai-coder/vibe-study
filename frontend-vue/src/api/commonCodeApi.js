import apiClient from './apiClient'

export const commonCodeApi = {
  getAll:       (params)          => apiClient.get('/common-codes', { params }).then(r => r.data),
  create:       (data)            => apiClient.post('/common-codes', data).then(r => r.data),
  update:       (code, data)      => apiClient.put(`/common-codes/${code}`, data).then(r => r.data),
  delete:       (code)            => apiClient.delete(`/common-codes/${code}`).then(r => r.data),

  getDetails:   (code)            => apiClient.get(`/common-codes/${code}/details`).then(r => r.data),
  createDetail: (code, data)      => apiClient.post(`/common-codes/${code}/details`, data).then(r => r.data),
  updateDetail: (code, dtl, data) => apiClient.put(`/common-codes/${code}/details/${dtl}`, data).then(r => r.data),
  deleteDetail: (code, dtl)       => apiClient.delete(`/common-codes/${code}/details/${dtl}`).then(r => r.data),
}
