import apiClient from './apiClient'

export const getSubsBillStdList = (params = {}) =>
  apiClient.get('/subs-bill-std/list', { params }).then(res => res.data)
