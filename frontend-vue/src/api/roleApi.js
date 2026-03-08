import apiClient from './apiClient'

export const roleApi = {
  getRoles:          ()                => apiClient.get('/roles').then(r => r.data),
  getUsersByRole:    (roleCd)          => apiClient.get(`/roles/${roleCd}/users`).then(r => r.data),
  getAvailableUsers: (roleCd)          => apiClient.get('/roles/available-users', { params: { roleCd } }).then(r => r.data),
  saveRoleUsers:     (roleCd, userIds) => apiClient.put(`/roles/${roleCd}/users`, userIds).then(r => r.data),
}
