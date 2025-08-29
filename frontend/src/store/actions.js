import { ACTIONS } from './appStore'

// Action creators for projects
export const setProjects = (projects) => ({
  type: ACTIONS.SET_PROJECTS,
  payload: projects
})

export const addProject = (project) => ({
  type: ACTIONS.ADD_PROJECT,
  payload: project
})

export const updateProject = (projectId, updates) => ({
  type: ACTIONS.UPDATE_PROJECT,
  payload: { id: projectId, updates }
})

export const deleteProject = (projectId) => ({
  type: ACTIONS.DELETE_PROJECT,
  payload: projectId
})

// Action creators for navigation
export const setActiveProject = (projectId) => ({
  type: ACTIONS.SET_ACTIVE_PROJECT,
  payload: projectId
})

export const setActiveTask = (taskId) => ({
  type: ACTIONS.SET_ACTIVE_TASK,
  payload: taskId
})

export const resetActiveSelection = () => ({
  type: ACTIONS.RESET_ACTIVE_SELECTION
})

// Action creators for tasks
export const setTasks = (tasks) => ({
  type: ACTIONS.SET_TASKS,
  payload: tasks
})

export const addTask = (task) => ({
  type: ACTIONS.ADD_TASK,
  payload: task
})

export const deleteTask = (taskId) => ({
  type: ACTIONS.DELETE_TASK,
  payload: taskId
})

// Action creators for todos
export const setTodos = (todos) => ({
  type: ACTIONS.SET_TODOS,
  payload: todos
})

export const addTodo = (todo) => ({
  type: ACTIONS.ADD_TODO,
  payload: todo
})

export const updateTodo = (todoId, updates) => ({
  type: ACTIONS.UPDATE_TODO,
  payload: { id: todoId, updates }
})

export const deleteTodo = (todoId) => ({
  type: ACTIONS.DELETE_TODO,
  payload: todoId
})

// Action creators for forms
export const setNewProjectName = (name) => ({
  type: ACTIONS.SET_NEW_PROJECT_NAME,
  payload: name
})

export const setNewTaskName = (name) => ({
  type: ACTIONS.SET_NEW_TASK_NAME,
  payload: name
})

export const setNewTodoText = (text) => ({
  type: ACTIONS.SET_NEW_TODO_TEXT,
  payload: text
})

export const resetForm = (formType) => ({
  type: ACTIONS.RESET_FORM,
  payload: formType
})
