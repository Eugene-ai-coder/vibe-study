import apiClient from './apiClient'

export const wfTaskApi = {
  getMyTasks:    (params)         => apiClient.get('/wf/tasks/my', { params }).then(r => r.data),
  getByProcess:  (processInstId)  => apiClient.get(`/wf/tasks/process-inst/${processInstId}`).then(r => r.data),
  claimTask:     (taskInstId, data) => apiClient.post(`/wf/tasks/${taskInstId}/claim`, data).then(r => r.data),
  completeTask:  (taskInstId, data) => apiClient.post(`/wf/tasks/${taskInstId}/complete`, data).then(r => r.data),
}
