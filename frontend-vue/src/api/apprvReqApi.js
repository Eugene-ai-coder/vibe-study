import apiClient from './apiClient'

export const getApprvReqList = (params = {}) =>
  apiClient.get('/apprv-req/list', { params }).then(res => res.data)

export const createApprvReq = (data) =>
  apiClient.post('/apprv-req', data).then(res => res.data)

export const getApprvReq = (apprvReqId) =>
  apiClient.get(`/apprv-req/${apprvReqId}`).then(res => res.data)
