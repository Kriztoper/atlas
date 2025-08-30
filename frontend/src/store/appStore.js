import { createContext, useContext, useReducer } from 'react'

// Initial state
const initialState = {
  projects: [],
  tasks: [],
  todos: [],
  activeProject: null,
  activeTask: null,
  newProjectName: '',
  newTaskName: '',
  newTodoText: ''
}

// Action types
export const ACTIONS = {
  SET_PROJECTS: 'SET_PROJECTS',
  ADD_PROJECT: 'ADD_PROJECT',
  UPDATE_PROJECT: 'UPDATE_PROJECT',
  DELETE_PROJECT: 'DELETE_PROJECT',
  SET_ACTIVE_PROJECT: 'SET_ACTIVE_PROJECT',
  SET_ACTIVE_TASK: 'SET_ACTIVE_TASK',
  SET_TASKS: 'SET_TASKS',
  ADD_TASK: 'ADD_TASK',
  DELETE_TASK: 'DELETE_TASK',
  SET_TODOS: 'SET_TODOS',
  ADD_TODO: 'ADD_TODO',
  UPDATE_TODO: 'UPDATE_TODO',
  DELETE_TODO: 'DELETE_TODO',
  SET_NEW_PROJECT_NAME: 'SET_NEW_PROJECT_NAME',
  SET_NEW_TASK_NAME: 'SET_NEW_TASK_NAME',
  SET_NEW_TODO_TEXT: 'SET_NEW_TODO_TEXT',
  RESET_FORM: 'RESET_FORM',
  RESET_ACTIVE_SELECTION: 'RESET_ACTIVE_SELECTION'
}

// Reducer function
const appReducer = (state, action) => {
  switch (action.type) {
    case ACTIONS.SET_PROJECTS:
      return { ...state, projects: action.payload }
    
    case ACTIONS.ADD_PROJECT:
      return {
        ...state,
        projects: [...state.projects, { ...action.payload, tasks: action.payload.tasks || [] }]
      }
    
    case ACTIONS.UPDATE_PROJECT:
      return {
        ...state,
        projects: state.projects.map(project => 
          project.id === action.payload.id 
            ? { ...project, ...action.payload.updates } 
            : project
        )
      }
    
    case ACTIONS.DELETE_PROJECT:
      return {
        ...state,
        projects: state.projects.filter(p => p.id !== action.payload),
        activeProject: state.activeProject === action.payload ? null : state.activeProject,
        activeTask: state.activeProject === action.payload ? null : state.activeTask
      }
    
    case ACTIONS.SET_ACTIVE_PROJECT:
      return {
        ...state,
        activeProject: action.payload,
        activeTask: null
      }
    
    case ACTIONS.SET_ACTIVE_TASK:
      return { ...state, activeTask: action.payload }
    
    case ACTIONS.SET_TASKS:
      return { ...state, tasks: action.payload }
    
    case ACTIONS.ADD_TASK:
      return {
        ...state,
        tasks: [...state.tasks, { ...action.payload, todos: action.payload.todos || [] }],
        projects: state.projects.map(project => 
          project.id === state.activeProject 
            ? { 
                ...project, 
                tasks: [...(project.tasks || []), { ...action.payload, todos: action.payload.todos || [] }] 
              }
            : project
        )
      }
    
    case ACTIONS.DELETE_TASK:
      return {
        ...state,
        projects: state.projects.map(project => 
          project.id === state.activeProject
            ? {
                ...project,
                tasks: project.tasks.filter(task => task.id !== action.payload)
              }
            : project
        ),
        tasks: state.tasks.filter(task => task.id !== action.payload),
        activeTask: state.activeTask === action.payload ? null : state.activeTask
      }
    
    case ACTIONS.SET_TODOS:
      return { ...state, todos: action.payload }
    
    case ACTIONS.ADD_TODO:
      return {
        ...state,
        todos: [...state.todos, { ...action.payload, isCompleted: action.payload.isCompleted || false }],
        projects: state.projects.map(project => 
          project.id === state.activeProject
            ? {
                ...project,
                tasks: project.tasks.map(task =>
                  task.id === state.activeTask
                    ? { 
                        ...task, 
                        todos: [...(task.todos || []), { ...action.payload, isCompleted: action.payload.isCompleted || false }]
                      }
                    : task
                )
              }
            : project
        )
      }
    
    case ACTIONS.UPDATE_TODO:
      return {
        ...state,
        todos: state.todos.map(todo => 
          todo.id === action.payload.id ? { ...todo, ...action.payload.updates } : todo
        ),
        projects: state.projects.map(project => 
          project.id === state.activeProject
            ? {
                ...project,
                tasks: project.tasks.map(task =>
                  task.id === state.activeTask
                    ? {
                        ...task,
                        todos: task.todos.map(todo =>
                          todo.id === action.payload.id 
                            ? { ...todo, ...action.payload.updates } 
                            : todo
                        )
                      }
                    : task
                )
              }
            : project
        )
      }
    
    case ACTIONS.DELETE_TODO:
      return {
        ...state,
        todos: state.todos.filter(todo => todo.id !== action.payload),
        projects: state.projects.map(project => 
          project.id === state.activeProject
            ? {
                ...project,
                tasks: project.tasks.map(task =>
                  task.id === state.activeTask
                    ? {
                        ...task,
                        todos: task.todos.filter(todo => todo.id !== action.payload)
                      }
                    : task
                )
              }
            : project
        )
      }
    
    case ACTIONS.SET_NEW_PROJECT_NAME:
      return { ...state, newProjectName: action.payload }
    
    case ACTIONS.SET_NEW_TASK_NAME:
      return { ...state, newTaskName: action.payload }
    
    case ACTIONS.SET_NEW_TODO_TEXT:
      return { ...state, newTodoText: action.payload }
    
    case ACTIONS.RESET_FORM:
      return {
        ...state,
        ...(action.payload === 'project' && { newProjectName: '' }),
        ...(action.payload === 'task' && { newTaskName: '' }),
        ...(action.payload === 'todo' && { newTodoText: '' })
      }
    
    case ACTIONS.RESET_ACTIVE_SELECTION:
      return {
        ...state,
        activeProject: null,
        activeTask: null
      }
    
    default:
      return state
  }
}

// Create context
const AppContext = createContext()

// Provider component
export const AppProvider = ({ children }) => {
  const [state, dispatch] = useReducer(appReducer, initialState)
  
  // Computed values
  const getCurrentProject = () => {
    return state.projects.find(p => p.id === state.activeProject)
  }
  
  const getCurrentTask = () => {
    return state.tasks.find(t => t.id === state.activeTask)
  }
  
  const getProjectTaskCount = (project) => project.tasks?.length || 0
  
  const getProjectTodoCount = (project) => {
    return project.tasks?.reduce((total, task) => 
      total + (task.todos?.length || 0), 0) || 0
  }
  
  const getProjectCompletedCount = (project) => {
    return project.tasks?.reduce((total, task) => 
      total + (task.todos?.filter(todo => todo.isCompleted).length || 0), 0) || 0
  }
  
  const getTaskTodoCount = (task) => task.todos?.length || 0
  
  const getTaskCompletedCount = (task) => task.todos?.filter(todo => todo.isCompleted).length || 0
  
  const value = {
    ...state,
    dispatch,
    getCurrentProject,
    getCurrentTask,
    getProjectTaskCount,
    getProjectTodoCount,
    getProjectCompletedCount,
    getTaskTodoCount,
    getTaskCompletedCount
  }
  
  return (
    <AppContext.Provider value={value}>
      {children}
    </AppContext.Provider>
  )
}

// Custom hook to use the context
export const useAppStore = () => {
  const context = useContext(AppContext)
  if (!context) {
    throw new Error('useAppStore must be used within an AppProvider')
  }
  return context
}

export default useAppStore
