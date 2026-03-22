import apiClient from './apiClient'

export const menuApi = {
  getTree:     ()              => apiClient.get('/menus/tree').then(r => r.data),
  getMyTree:   ()              => apiClient.get('/menus/tree/my').then(r => r.data),
  get:         (menuId)        => apiClient.get(`/menus/${menuId}`).then(r => r.data),
  create:      (data)          => apiClient.post('/menus', data).then(r => r.data),
  update:      (menuId, data)  => apiClient.put(`/menus/${menuId}`, data).then(r => r.data),
  delete:      (menuId)        => apiClient.delete(`/menus/${menuId}`).then(r => r.data),
  moveUp:      (menuId)        => apiClient.post(`/menus/${menuId}/move-up`).then(r => r.data),
  moveDown:    (menuId)        => apiClient.post(`/menus/${menuId}/move-down`).then(r => r.data),
  moveMenu:    (menuId, data)  => apiClient.post(`/menus/${menuId}/move`, data).then(r => r.data),
}
