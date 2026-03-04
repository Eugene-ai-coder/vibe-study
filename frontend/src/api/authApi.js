import apiClient from './apiClient'

export const login = (userId, password) =>
  apiClient.post('/auth/login', { userId, password }).then(r => r.data)

export const logout = () =>
  apiClient.post('/auth/logout')

export const getMe = () =>
  apiClient.get('/auth/me').then(r => r.data)

export const register = (data) =>
  apiClient.post('/auth/register', data).then(r => r.data)

export const getUsers = () =>
  apiClient.get('/auth/users').then(r => r.data)

export const getUsersPage = (params) =>
  apiClient.get('/auth/users/page', { params }).then(r => r.data)
