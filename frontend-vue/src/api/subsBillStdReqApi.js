import apiClient from './apiClient'

export const getSubsBillStdReqList = (params = {}) =>
  apiClient.get('/subs-bill-std-req/list', { params }).then(res => res.data)
