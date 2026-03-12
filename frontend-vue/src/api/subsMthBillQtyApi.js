import apiClient from './apiClient'

export const getSubsMthBillQtyList = (params = {}) =>
  apiClient.get('/subs-mth-bill-qty', { params }).then(res => res.data)
