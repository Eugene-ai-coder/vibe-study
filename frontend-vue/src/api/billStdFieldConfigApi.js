import apiClient from './apiClient'

export const getFieldConfigs = (params) =>
  apiClient.get('/bill-std-field-configs', { params }).then(res => res.data)

export const getFieldConfig = (svcCd, fieldCd, effStartDt) =>
  apiClient.get(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}`).then(res => res.data)

export const getEffectiveConfigs = (svcCd) =>
  apiClient.get(`/bill-std-field-configs/effective/${svcCd}`).then(res => res.data)

export const createFieldConfig = (data) =>
  apiClient.post('/bill-std-field-configs', data).then(res => res.data)

export const updateFieldConfig = (svcCd, fieldCd, effStartDt, data) =>
  apiClient.put(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}`, data).then(res => res.data)

export const deleteFieldConfig = (svcCd, fieldCd, effStartDt) =>
  apiClient.delete(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}`)

export const expireFieldConfig = (svcCd, fieldCd, effStartDt) =>
  apiClient.put(`/bill-std-field-configs/${svcCd}/${fieldCd}/${effStartDt}/expire`).then(res => res.data)
