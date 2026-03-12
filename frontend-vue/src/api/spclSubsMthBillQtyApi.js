import apiClient from './apiClient'

export const getSpclSubsMthBillQtyList = (params) =>
  apiClient.get('/spcl-subs-mth-bill-qty', { params }).then(r => r.data)

export const createSpclSubsMthBillQty = (data) =>
  apiClient.post('/spcl-subs-mth-bill-qty', data).then(r => r.data)

export const updateSpclSubsMthBillQty = (spclSubsId, useMth, data) =>
  apiClient.put(`/spcl-subs-mth-bill-qty/${spclSubsId}/${useMth}`, data).then(r => r.data)

export const deleteSpclSubsMthBillQty = (spclSubsId, useMth) =>
  apiClient.delete(`/spcl-subs-mth-bill-qty/${spclSubsId}/${useMth}`)
