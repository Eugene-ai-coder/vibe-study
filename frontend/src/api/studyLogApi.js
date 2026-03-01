import apiClient from './apiClient'

export const getLogs = () => apiClient.get('/logs').then(res => res.data)

export const createLog = (data) => apiClient.post('/logs', data).then(res => res.data)

export const updateLog = (id, data) => apiClient.put(`/logs/${id}`, data).then(res => res.data)

export const deleteLog = (id) => apiClient.delete(`/logs/${id}`)
