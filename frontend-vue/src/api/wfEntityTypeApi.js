import apiClient from './apiClient'

export const wfEntityTypeApi = {
  getAll: () => apiClient.get('/wf/entity-types').then(r => r.data),
}
