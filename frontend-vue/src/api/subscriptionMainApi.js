import apiClient from './apiClient'

export const getSubscriptionMainList = (params) =>
  apiClient.get('/subscription-main', { params }).then(r => r.data)

export const saveSubscriptionMain = (data) =>
  apiClient.post('/subscription-main', data).then(r => r.data)
