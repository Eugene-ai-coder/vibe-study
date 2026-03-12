import apiClient from './apiClient'

export const wfApi = {
  // 프로세스 정의
  getProcessDefs:      ()           => apiClient.get('/wf/process-def').then(r => r.data),
  getProcessDef:       (id)         => apiClient.get(`/wf/process-def/${id}`).then(r => r.data),
  createProcessDef:    (data)       => apiClient.post('/wf/process-def', data).then(r => r.data),
  updateProcessDef:    (id, data)   => apiClient.put(`/wf/process-def/${id}`, data).then(r => r.data),
  deleteProcessDef:    (id)         => apiClient.delete(`/wf/process-def/${id}`),

  // 상태 정의
  getStates:           (pid)        => apiClient.get(`/wf/process-def/${pid}/states`).then(r => r.data),
  createState:         (pid, data)  => apiClient.post(`/wf/process-def/${pid}/states`, data).then(r => r.data),
  updateState:         (id, data)   => apiClient.put(`/wf/states/${id}`, data).then(r => r.data),
  deleteState:         (id)         => apiClient.delete(`/wf/states/${id}`),

  // 전이 정의
  getTransitions:      (pid)        => apiClient.get(`/wf/process-def/${pid}/transitions`).then(r => r.data),
  createTransition:    (pid, data)  => apiClient.post(`/wf/process-def/${pid}/transitions`, data).then(r => r.data),
  updateTransition:    (id, data)   => apiClient.put(`/wf/transitions/${id}`, data).then(r => r.data),
  deleteTransition:    (id)         => apiClient.delete(`/wf/transitions/${id}`),

  // Task 템플릿
  getTaskTemplates:    (sid)        => apiClient.get(`/wf/states/${sid}/task-templates`).then(r => r.data),
  createTaskTemplate:  (sid, data)  => apiClient.post(`/wf/states/${sid}/task-templates`, data).then(r => r.data),
  updateTaskTemplate:  (id, data)   => apiClient.put(`/wf/task-templates/${id}`, data).then(r => r.data),
  deleteTaskTemplate:  (id)         => apiClient.delete(`/wf/task-templates/${id}`),
}
