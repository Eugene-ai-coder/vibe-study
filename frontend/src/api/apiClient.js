import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401
        && !error.config.url.includes('/auth/login')
        && !error.config.url.includes('/auth/me')) {
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
