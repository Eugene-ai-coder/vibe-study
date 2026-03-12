import apiClient from './apiClient'

export const wfEngineApi = {
  startProcess:       (data)           => apiClient.post('/wf/engine/start', data).then(r => r.data),
  getProcessInst:     (processInstId)  => apiClient.get(`/wf/engine/process-inst/${processInstId}`).then(r => r.data),
  getProcessInstByEntity: (params)     => apiClient.get('/wf/engine/process-inst', { params }).then(r => r.data),
  transition:         (processInstId, data) => apiClient.post(`/wf/engine/process-inst/${processInstId}/transition`, data).then(r => r.data),
  getWfAutoStartConfig: ()             => apiClient.get('/config/wf-auto-start').then(r => r.data),
}
