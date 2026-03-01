import apiClient from './apiClient'

export const searchSubscriptions = (type, keyword) =>
  apiClient.get('/subscriptions', { params: { type, keyword } }).then(res => res.data)

export const getSubscription = (subsId) =>
  apiClient.get(`/subscriptions/${subsId}`).then(res => res.data)

export const createSubscription = (data) =>
  apiClient.post('/subscriptions', data).then(res => res.data)

export const updateSubscription = (subsId, data) =>
  apiClient.put(`/subscriptions/${subsId}`, data).then(res => res.data)

export const deleteSubscription = (subsId) =>
  apiClient.delete(`/subscriptions/${subsId}`)
