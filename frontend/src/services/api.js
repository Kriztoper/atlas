// API Base URL - Update this when backend is available
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api'

// Generic API request handler
async function apiRequest(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  }

  try {
    const response = await fetch(url, config)
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
    }

    // Handle empty responses (like DELETE requests)
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      return await response.json()
    }
    
    return response
  } catch (error) {
    console.error(`API request failed for ${endpoint}:`, error)
    throw error
  }
}

// Project API calls
export const projectAPI = {
  // POST /api/projects
  create: async (projectData) => {
    return apiRequest('/projects', {
      method: 'POST',
      body: JSON.stringify({
        name: projectData.name,
        description: projectData.description || '',
      }),
    })
  },

  // GET /api/projects
  getAll: async () => {
    return apiRequest('/projects')
  },

  // GET /api/projects/:id
  getById: async (projectId) => {
    return apiRequest(`/projects/${projectId}`)
  },

  // PUT /api/projects/:id
  update: async (projectId, projectData) => {
    return apiRequest(`/projects/${projectId}`, {
      method: 'PUT',
      body: JSON.stringify(projectData),
    })
  },

  // DELETE /api/projects/:id
  delete: async (projectId) => {
    return apiRequest(`/projects/${projectId}`, {
      method: 'DELETE',
    })
  },
}

// Task API calls
export const taskAPI = {
  // POST /api/projects/:projectId/tasks
  create: async (projectId, taskData) => {
    return apiRequest(`/projects/${projectId}/tasks`, {
      method: 'POST',
      body: JSON.stringify({
        name: taskData.name,
        description: taskData.description || '',
        projectId: projectId,
      }),
    })
  },

  // GET /api/projects/:projectId/tasks
  getByProject: async (projectId) => {
    return apiRequest(`/projects/${projectId}/tasks`)
  },

  // GET /api/tasks/:id
  getById: async (taskId) => {
    return apiRequest(`/tasks/${taskId}`)
  },

  // PUT /api/tasks/:id
  update: async (taskId, taskData) => {
    return apiRequest(`/tasks/${taskId}`, {
      method: 'PUT',
      body: JSON.stringify(taskData),
    })
  },

  // DELETE /api/tasks/:id
  delete: async (taskId) => {
    return apiRequest(`/tasks/${taskId}`, {
      method: 'DELETE',
    })
  },
}

// Todo API calls
export const todoAPI = {
  // POST /api/tasks/:taskId/todos
  create: async (taskId, todoData) => {
    return apiRequest(`/tasks/${taskId}/todos`, {
      method: 'POST',
      body: JSON.stringify({
        text: todoData.text,
        description: todoData.description || '',
        taskId: taskId,
        completed: false,
      }),
    })
  },

  // GET /api/tasks/:taskId/todos
  getByTask: async (taskId) => {
    return apiRequest(`/tasks/${taskId}/todos`)
  },

  // GET /api/todos/:id
  getById: async (todoId) => {
    return apiRequest(`/todos/${todoId}`)
  },

  // PUT /api/todos/:id
  update: async (todoId, todoData) => {
    return apiRequest(`/todos/${todoId}`, {
      method: 'PUT',
      body: JSON.stringify(todoData),
    })
  },

  // PATCH /api/todos/:id/complete
  markComplete: async (todoId, completed = true) => {
    return apiRequest(`/todos/${todoId}/complete`, {
      method: 'PATCH',
      body: JSON.stringify({
        completed: completed,
        completedAt: completed ? new Date().toISOString() : null,
      }),
    })
  },

  // DELETE /api/todos/:id
  delete: async (todoId) => {
    return apiRequest(`/todos/${todoId}`, {
      method: 'DELETE',
    })
  },
}

// Error handling helper
export const handleAPIError = (error, fallbackMessage = 'An error occurred') => {
  if (error.name === 'TypeError' && error.message.includes('fetch')) {
    return 'Unable to connect to the server. Please check your internet connection.'
  }
  
  return error.message || fallbackMessage
}

// API status check
export const checkAPIHealth = async () => {
  try {
    const response = await apiRequest('/health')
    return { status: 'ok', ...response }
  } catch (error) {
    return { status: 'error', message: error.message }
  }
}

export default {
  projectAPI,
  taskAPI,
  todoAPI,
  handleAPIError,
  checkAPIHealth,
}
