import apiClient from './apiClient'

export const todoApi = {
  getAll: (params) => apiClient.get('/todos', { params }).then(r => r.data),
}
