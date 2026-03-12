import apiClient from './apiClient'

export const getSpclSubsMthBillElemList = (params) =>
  apiClient.get('/spcl-subs-mth-bill-elem', { params }).then(r => r.data)

export const createSpclSubsMthBillElem = (data) =>
  apiClient.post('/spcl-subs-mth-bill-elem', data).then(r => r.data)

export const updateSpclSubsMthBillElem = (spclSubsId, billMth, data) =>
  apiClient.put(`/spcl-subs-mth-bill-elem/${spclSubsId}/${billMth}`, data).then(r => r.data)

export const deleteSpclSubsMthBillElem = (spclSubsId, billMth) =>
  apiClient.delete(`/spcl-subs-mth-bill-elem/${spclSubsId}/${billMth}`)
