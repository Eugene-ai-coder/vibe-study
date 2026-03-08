import apiClient from './apiClient'

export const getSpecialSubscriptions = (params) =>
  apiClient.get('/special-subscriptions', { params }).then(r => r.data)

export const getSpecialSubscription = (subsBillStdId, effStaDt) =>
  apiClient.get(`/special-subscriptions/${subsBillStdId}/${effStaDt}`).then(r => r.data)

export const createSpecialSubscription = (data) =>
  apiClient.post('/special-subscriptions', data).then(r => r.data)

export const updateSpecialSubscription = (subsBillStdId, effStaDt, data) =>
  apiClient.put(`/special-subscriptions/${subsBillStdId}/${effStaDt}`, data).then(r => r.data)

export const deleteSpecialSubscription = (subsBillStdId, effStaDt) =>
  apiClient.delete(`/special-subscriptions/${subsBillStdId}/${effStaDt}`)
