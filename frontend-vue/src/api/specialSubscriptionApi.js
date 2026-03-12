import apiClient from './apiClient'

export const getSpecialSubscriptions = (params) =>
  apiClient.get('/special-subscriptions', { params }).then(r => r.data)

export const getSpecialSubscription = (subsBillStdId, effStartDt) =>
  apiClient.get(`/special-subscriptions/${subsBillStdId}/${effStartDt}`).then(r => r.data)

export const createSpecialSubscription = (data) =>
  apiClient.post('/special-subscriptions', data).then(r => r.data)

export const updateSpecialSubscription = (subsBillStdId, effStartDt, data) =>
  apiClient.put(`/special-subscriptions/${subsBillStdId}/${effStartDt}`, data).then(r => r.data)

export const deleteSpecialSubscription = (subsBillStdId, effStartDt) =>
  apiClient.delete(`/special-subscriptions/${subsBillStdId}/${effStartDt}`)
