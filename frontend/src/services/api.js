// Enhanced API client integration
import apiClient, { APIError } from './apiClient.js'

// Project API calls
export const projectAPI = {
  // POST /api/projects
  create: async (projectData) => {
    return apiClient.post('/projects', {
      name: projectData.name,
      description: projectData.description || '',
    })
  },

  // GET /api/projects
  getAll: async () => {
    return apiClient.get('/projects')
  },

  // GET /api/projects/:id
  getById: async (projectId) => {
    return apiClient.get(`/projects/${projectId}`)
  },

  // PUT /api/projects/:id
  update: async (projectId, projectData) => {
    return apiClient.put(`/projects/${projectId}`, projectData)
  },

  // DELETE /api/projects/:id
  delete: async (projectId) => {
    return apiClient.delete(`/projects/${projectId}`)
  },
}

// Task API calls
export const taskAPI = {
  // POST /api/projects/:projectId/tasks
  create: async (projectId, taskData) => {
    return apiClient.post(`/projects/${projectId}/tasks`, {
      name: taskData.name,
      description: taskData.description || '',
      projectId: projectId,
    })
  },

  // GET /api/projects/:projectId/tasks
  getByProject: async (projectId) => {
    return apiClient.get(`/projects/${projectId}/tasks`)
  },

  // GET /api/tasks/:id
  getById: async (taskId) => {
    return apiClient.get(`/tasks/${taskId}`)
  },

  // PUT /api/tasks/:id
  update: async (taskId, taskData) => {
    return apiClient.put(`/tasks/${taskId}`, taskData)
  },

  // DELETE /api/tasks/:id
  delete: async (taskId) => {
    return apiClient.delete(`/tasks/${taskId}`)
  },
}

// Todo API calls
export const todoAPI = {
  // POST /api/tasks/:taskId/todos
  create: async (taskId, todoData) => {
    return apiClient.post(`/tasks/${taskId}/todos`, {
      text: todoData.text,
      description: todoData.description || '',
      taskId: taskId,
      completed: false,
    })
  },

  // GET /api/tasks/:taskId/todos
  getByTask: async (taskId) => {
    return apiClient.get(`/tasks/${taskId}/todos`)
  },

  // GET /api/todos/:id
  getById: async (todoId) => {
    return apiClient.get(`/todos/${todoId}`)
  },

  // PUT /api/todos/:id
  update: async (todoId, todoData) => {
    return apiClient.put(`/todos/${todoId}`, todoData)
  },

  // PATCH /api/todos/:id/complete
  markComplete: async (todoId, completed = true) => {
    return apiClient.patch(`/todos/${todoId}/complete`, {
      completed: completed,
      completedAt: completed ? new Date().toISOString() : null,
    })
  },

  // DELETE /api/todos/:id
  delete: async (todoId) => {
    return apiClient.delete(`/todos/${todoId}`)
  },
}

// Error handling helper
export const handleAPIError = (error, fallbackMessage = 'An error occurred') => {
  // Handle enhanced API client errors
  if (error instanceof APIError) {
    return error.message || fallbackMessage
  }
  
  if (error.name === 'TypeError' && error.message.includes('fetch')) {
    return 'Unable to connect to the server. Please check your internet connection.'
  }
  
  if (error.code === 'OFFLINE') {
    return 'You appear to be offline. Please check your internet connection.'
  }
  
  if (error.code === 'TIMEOUT') {
    return 'Request timeout. Please try again.'
  }
  
  return error.message || fallbackMessage
}

// API status check
export const checkAPIHealth = async () => {
  try {
    const response = await apiClient.get('/health')
    return { status: 'ok', ...response }
  } catch (error) {
    return { status: 'error', message: handleAPIError(error) }
  }
}

export default {
  projectAPI,
  taskAPI,
  todoAPI,
  handleAPIError,
  checkAPIHealth,
}
