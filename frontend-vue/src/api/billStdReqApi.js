import apiClient from './apiClient'

export const getBillStdReq = (billStdReqId) =>
  apiClient.get(`/bill-std-req/${billStdReqId}`).then(res => res.data)

export const searchBySubsId = (subsId) =>
  apiClient.get('/bill-std-req/search-by-subs', { params: { subsId } }).then(res => res.data)

export const createBillStdReq = (data) =>
  apiClient.post('/bill-std-req', data).then(res => res.data)

export const saveBillStdReq = (billStdReqId, data) =>
  apiClient.put(`/bill-std-req/${billStdReqId}`, data).then(res => res.data)

export const changeStatus = (billStdReqId, status, createdBy) =>
  apiClient.put(`/bill-std-req/${billStdReqId}/status`, { status, createdBy }).then(res => res.data)

export const deleteBillStdReq = (billStdReqId) =>
  apiClient.delete(`/bill-std-req/${billStdReqId}`)

export const getTodoList = () =>
  apiClient.get('/bill-std-req/todo').then(res => res.data)
