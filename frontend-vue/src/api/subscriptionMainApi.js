import apiClient from './apiClient'

export const getSubscriptionMainList = (params) =>
  apiClient.get('/subscription-main', { params }).then(r => r.data)

export const saveSubscriptionMain = (data) =>
  apiClient.post('/subscription-main', data).then(r => r.data)

export const downloadSubscriptionMainExcel = (items) =>
  apiClient.post('/subscription-main/excel/download', items, {
    responseType: 'blob'
  }).then(r => r.data)

export const uploadSubscriptionMainExcel = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return apiClient.post('/subscription-main/excel/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(r => r.data)
}

export const saveSubscriptionMainBulk = (items) =>
  apiClient.post('/subscription-main/bulk', { items }).then(r => r.data)
