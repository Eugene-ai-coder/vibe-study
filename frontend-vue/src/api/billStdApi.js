import apiClient from './apiClient'

export const getBillStds = () => apiClient.get('/bill-std').then(res => res.data)

export const getBillStd = (id) => apiClient.get(`/bill-std/${id}`).then(res => res.data)

export const getBillStdBySubsId = (subsId) => apiClient.get(`/bill-std/by-subs/${subsId}`).then(res => res.data)

export const createBillStd = (data) => apiClient.post('/bill-std', data).then(res => res.data)

export const updateBillStd = (id, data) => apiClient.put(`/bill-std/${id}`, data).then(res => res.data)

export const deleteBillStd = (id) => apiClient.delete(`/bill-std/${id}`)
