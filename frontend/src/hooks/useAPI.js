import { useState, useCallback } from 'react'
import { handleAPIError } from '../services/api'

// Custom hook for managing API operations with loading and error states
export const useAPI = () => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const executeAPI = useCallback(async (apiCall, onSuccess, onError) => {
    setLoading(true)
    setError(null)

    try {
      const result = await apiCall()
      if (onSuccess) {
        onSuccess(result)
      }
      return result
    } catch (err) {
      const errorMessage = handleAPIError(err)
      setError(errorMessage)
      
      if (onError) {
        onError(errorMessage)
      }
      
      // Log error for debugging
      console.error('API Error:', err)
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  const clearError = useCallback(() => {
    setError(null)
  }, [])

  return {
    loading,
    error,
    executeAPI,
    clearError,
  }
}

// Specific hook for project operations
export const useProjectAPI = () => {
  const { loading, error, executeAPI, clearError } = useAPI()
  const [projects, setProjects] = useState([])

  const createProject = useCallback(async (projectData, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ projectAPI }) => projectAPI.create(projectData)),
      (newProject) => {
        setProjects(prev => Array.isArray(prev) ? [...prev, newProject] : [newProject])
        onSuccess?.(newProject)
      }
    )
  }, [executeAPI])

  const getAllProjects = useCallback(async (onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ projectAPI }) => projectAPI.getAll()),
      (fetchedProjects) => {
        setProjects(fetchedProjects)
        onSuccess?.(fetchedProjects)
      }
    )
  }, [executeAPI])

  const updateProjects = useCallback((updatedProjects) => {
    setProjects(updatedProjects)
  }, [])

  return {
    loading,
    error,
    projects,
    createProject,
    getAllProjects,
    updateProjects,
    clearError,
  }
}

// Specific hook for task operations
export const useTaskAPI = () => {
  const { loading, error, executeAPI, clearError } = useAPI()
  const [tasks, setTasks] = useState([])

  const createTask = useCallback(async (projectId, taskData, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ taskAPI }) => taskAPI.create(projectId, taskData)),
      onSuccess
    )
  }, [executeAPI])

  const getTasksByProject = useCallback(async (projectId, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ taskAPI }) => taskAPI.getByProject(projectId)),
      (fetchedTasks) => {
        setTasks(fetchedTasks)
        onSuccess?.(fetchedTasks)
      }
    )
  }, [executeAPI])

  return {
    loading,
    error,
    createTask,
    getTasksByProject,
    clearError,
  }
}

// Specific hook for todo operations
export const useTodoAPI = () => {
  const { loading, error, executeAPI, clearError } = useAPI()
  const [todos, setTodos] = useState([])

  const getTodosByTask = useCallback(async (taskId, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ todoAPI }) => todoAPI.getByTask(taskId)),
      (fetchedTodos) => {
        setTodos(fetchedTodos)
        onSuccess?.(fetchedTodos)
      }
    )
  }, [executeAPI])

  const createTodo = useCallback(async (taskId, todoData, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ todoAPI }) => todoAPI.create(taskId, todoData)),
      onSuccess
    )
  }, [executeAPI])

  const markTodoComplete = useCallback(async (todoId, completed, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ todoAPI }) => todoAPI.markComplete(todoId, completed)),
      onSuccess
    )
  }, [executeAPI])

  const deleteTodo = useCallback(async (todoId, onSuccess) => {
    return executeAPI(
      () => import('../services/api').then(({ todoAPI }) => todoAPI.delete(todoId)),
      onSuccess
    )
  }, [executeAPI])

  return {
    loading,
    error,
    getTodosByTask,
    createTodo,
    markTodoComplete,
    deleteTodo,
    clearError,
  }
}

export default useAPI
