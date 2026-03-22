import apiClient from './apiClient'

export const getBillStds = () => apiClient.get('/bill-std').then(res => res.data)

export const getBillStd = (id) => apiClient.get(`/bill-std/${id}`).then(res => res.data)

export const getBillStdBySubsId = (subsId) => apiClient.get(`/bill-std/by-subs/${subsId}`).then(res => res.data)
