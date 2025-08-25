import { useState, useEffect } from 'react'
import './App.css'
import ProjectCard from './components/ProjectCard'
import TaskCard from './components/TaskCard'
import TodoItem from './components/TodoItem'
import APIStatus from './components/APIStatus'
import Breadcrumb from './components/Breadcrumb'
import ProgressBar from './components/ProgressBar'
import { useProjectAPI, useTaskAPI, useTodoAPI } from './hooks/useAPI'

function App() {
  const [projects, setProjects] = useState([])
  const [tasks, setTasks] = useState([])
  const [todos, setTodos] = useState([])
  const [activeProject, setActiveProject] = useState(null)
  const [activeTask, setActiveTask] = useState(null)
  const [newProjectName, setNewProjectName] = useState('')
  const [newTaskName, setNewTaskName] = useState('')
  const [newTodoText, setNewTodoText] = useState('')

  // API hooks
  const projectAPI = useProjectAPI()
  const taskAPI = useTaskAPI()
  const todoAPI = useTodoAPI()

  // Load projects on initial app mount (only once)
  useEffect(() => {
    const loadInitialProjects = async () => {
      try {
        await projectAPI.getAllProjects((fetchedProjects) => {
          if (fetchedProjects.data && fetchedProjects.data.length === 0) {
            return;
          }

          // On API success, use the fetched projects with proper nested structure
          const structuredProjects = fetchedProjects.map(project => ({
            ...project,
            tasks: project.tasks || [],
          }))
          setProjects(structuredProjects)
        })
      } catch (error) {
        // If API fails, gracefully continue with empty state (no need to set error state here)
        // The error will already be handled by the API hook and shown in the UI
        console.warn('Failed to load initial projects, starting with empty state:', error)
      }
    }

    loadInitialProjects()
  }, []) // Empty dependency array ensures this runs only once on mount

  const loadTasksByProject = async () => {
    if (activeProject) {
      try {
        await taskAPI.getTasksByProject(activeProject, (fetchedTasks) => {
          setTasks(fetchedTasks)
        })
      } catch (error) {
        // If API fails, gracefully continue with empty state (no need to set error state here)
        // The error will already be handled by the API hook and shown in the UI
        console.warn('Failed to load tasks for current project:', error)
      }
    }
  }

  useEffect(() => {
    loadTasksByProject()
  }, [activeProject])

  const loadTodosByTask = async () => {
    if (activeTask) {
      try {
        console.log('Fetching todos for task with id = ' + activeTask)
        await todoAPI.getTodosByTask(activeTask, (fetchedTodos) => {
          setTodos(fetchedTodos)
        })
      } catch (error) {
        // If API fails, gracefully continue with empty state (no need to set error state here)
        // The error will already be handled by the API hook and shown in the UI
        console.warn('Failed to load todos for current task:', error)
      }
    }
  }

  useEffect(() => {
    loadTodosByTask()
  }, [activeTask])

  // Helper functions to get totals
  const getProjectTaskCount = (project) => project.tasks?.length || 0
  const getProjectTodoCount = (project) => {
    return project.tasks?.reduce((total, task) => total + (task.todos?.length || 0), 0) || 0
  }
  const getProjectCompletedCount = (project) => {
    return project.tasks?.reduce((total, task) => 
      total + (task.todos?.filter(todo => todo.completed).length || 0), 0) || 0
  }
  const getTaskTodoCount = (task) => task.todos?.length || 0
  const getTaskCompletedCount = (task) => task.todos?.filter(todo => todo.completed).length || 0

  // Project management functions
  const createProject = async () => {
    if (!newProjectName.trim()) return
    
    try {
      // Try API call first
      await projectAPI.createProject(
        { name: newProjectName.trim() },
        (apiProject) => {
          // On API success, use the response from the API
          const newProject = {
            ...apiProject,
            tasks: apiProject.tasks || [],
          }
          setProjects([...projects, newProject])
          setNewProjectName('')
          setActiveProject(newProject.id)
          setActiveTask(null)
        }
      )
    } catch (error) {
      // Fallback to local state if API fails
      console.warn('API call failed, using local state:', error)
      const newProject = {
        id: Date.now(),
        name: newProjectName.trim(),
        tasks: [],
        createdAt: new Date().toISOString()
      }
      setProjects([...projects, newProject])
      setNewProjectName('')
      setActiveProject(newProject.id)
      setActiveTask(null)
    }
  }

  const deleteProject = (projectId) => {
    setProjects(projects.filter(p => p.id !== projectId))
    if (activeProject === projectId) {
      setActiveProject(null)
      setActiveTask(null)
    }
  }

  // Task management functions
  const createTask = async () => {
    if (!newTaskName.trim() || !activeProject) return
    
    try {
      // Try API call first
      await taskAPI.createTask(
        activeProject,
        { name: newTaskName.trim() },
        (apiTask) => {
          // On API success, use the response from the API
          const newTask = {
            ...apiTask,
            todos: apiTask.todos || [],
          }
          setProjects(projects.map(project => 
            project.id === activeProject 
              ? { ...project, tasks: [...(project.tasks || []), newTask] }
              : project
          ))
          setNewTaskName('')
          setActiveTask(newTask.id)
          loadTasksByProject()
        }
      )
    } catch (error) {
      // Fallback to local state if API fails
      console.warn('API call failed, using local state:', error)
      const newTask = {
        id: Date.now(),
        name: newTaskName.trim(),
        todos: [],
        createdAt: new Date().toISOString()
      }
      setProjects(projects.map(project => 
        project.id === activeProject 
          ? { ...project, tasks: [...(project.tasks || []), newTask] }
          : project
      ))
      setNewTaskName('')
      setActiveTask(newTask.id)
    }
  }

  const deleteTask = (taskId) => {
    setProjects(projects.map(project => 
      project.id === activeProject
        ? {
            ...project,
            tasks: project.tasks.filter(task => task.id !== taskId)
          }
        : project
    ))
    if (activeTask === taskId) {
      setActiveTask(null)
    }
  }

  // Todo management functions
  const addTodo = async () => {
    if (!newTodoText.trim() || !activeProject || !activeTask) return
    
    try {
      // Try API call first
      await todoAPI.createTodo(
        activeTask,
        { text: newTodoText.trim() },
        (apiTodo) => {
          // On API success, use the response from the API
          const newTodo = {
            ...apiTodo,
            completed: apiTodo.completed || false,
          }
          setProjects(projects.map(project => 
            project.id === activeProject
              ? {
                  ...project,
                  tasks: project.tasks.map(task =>
                    task.id === activeTask
                      ? { ...task, todos: [...(task.todos || []), newTodo] }
                      : task
                  )
                }
              : project
          ))
          setNewTodoText('')
        }
      )
    } catch (error) {
      // Fallback to local state if API fails
      console.warn('API call failed, using local state:', error)
      const newTodo = {
        id: Date.now(),
        text: newTodoText.trim(),
        completed: false,
        createdAt: new Date().toISOString()
      }
      setProjects(projects.map(project => 
        project.id === activeProject
          ? {
              ...project,
              tasks: project.tasks.map(task =>
                task.id === activeTask
                  ? { ...task, todos: [...(task.todos || []), newTodo] }
                  : task
              )
            }
          : project
      ))
      setNewTodoText('')
    }
  }

  const toggleTodo = async (todoId) => {
    // Find the current todo to get its current state
    const currentTodo = currentTask?.todos?.find(t => t.id === todoId)
    if (!currentTodo) return
    
    const newCompletedState = !currentTodo.completed
    
    try {
      // Try API call first
      await todoAPI.markTodoComplete(
        todoId,
        newCompletedState,
        () => {
          // On API success, update local state
          setProjects(projects.map(project => 
            project.id === activeProject
              ? {
                  ...project,
                  tasks: project.tasks.map(task =>
                    task.id === activeTask
                      ? {
                          ...task,
                          todos: task.todos.map(todo =>
                            todo.id === todoId ? { ...todo, completed: newCompletedState } : todo
                          )
                        }
                      : task
                  )
                }
              : project
          ))
        }
      )
    } catch (error) {
      // Fallback to local state if API fails
      console.warn('API call failed, using local state:', error)
      setProjects(projects.map(project => 
        project.id === activeProject
          ? {
              ...project,
              tasks: project.tasks.map(task =>
                task.id === activeTask
                  ? {
                      ...task,
                      todos: task.todos.map(todo =>
                        todo.id === todoId ? { ...todo, completed: newCompletedState } : todo
                      )
                    }
                  : task
              )
            }
          : project
      ))
    }
  }

  const deleteTodo = async (todoId) => {
    try {
      // Try API call first
      await todoAPI.deleteTodo(
        todoId,
        () => {
          // On API success, update local state
          setProjects(projects.map(project => 
            project.id === activeProject
              ? {
                  ...project,
                  tasks: project.tasks.map(task =>
                    task.id === activeTask
                      ? {
                          ...task,
                          todos: task.todos.filter(todo => todo.id !== todoId)
                        }
                      : task
                  )
                }
              : project
          ))
        }
      )
    } catch (error) {
      // Fallback to local state if API fails
      console.warn('API call failed, using local state:', error)
      setProjects(projects.map(project => 
        project.id === activeProject
          ? {
              ...project,
              tasks: project.tasks.map(task =>
                task.id === activeTask
                  ? {
                      ...task,
                      todos: task.todos.filter(todo => todo.id !== todoId)
                    }
                  : task
              )
            }
          : project
      ))
    }
  }

  const currentProject = projects.find(p => p.id === activeProject)
  const currentTask = tasks.find(t => t.id === activeTask)

  // Breadcrumb items
  const breadcrumbItems = []
  
  breadcrumbItems.push({
    label: 'Projects',
    icon: '🏠',
    active: !currentProject,
    onClick: () => {
      setActiveProject(null)
      setActiveTask(null)
    }
  })
  
  if (currentProject) {
    breadcrumbItems.push({
      label: currentProject.name,
      icon: '📁',
      active: !currentTask,
      onClick: () => setActiveTask(null)
    })
  }
  
  if (currentTask) {
    breadcrumbItems.push({
      label: currentTask.name,
      icon: '📝',
      active: true
    })
  }

  return (
    <div className="app">
      <h1>
        <img
          src="/atlas-logo.svg"
          alt="SVG icon"
          style={{
            width: 40,
            height: 40,
            borderRadius: '50%',
            marginRight: 0
          }}
        />
        <span>Atlas</span>
      </h1>
      
      {/* Breadcrumb Navigation */}
      {breadcrumbItems.length > 1 && (
        <Breadcrumb items={breadcrumbItems} />
      )}
      
      {/* API Status for Project Operations */}
      <APIStatus
        loading={projectAPI.loading}
        error={projectAPI.error}
        onClearError={projectAPI.clearError}
      />
      
      {/* Project Creation Section */}
      <div className="project-creation">
        <h2>Create New Project</h2>
        <div className="input-group">
          <input
            type="text"
            value={newProjectName}
            onChange={(e) => setNewProjectName(e.target.value)}
            placeholder="Enter project name..."
            onKeyPress={(e) => e.key === 'Enter' && createProject()}
            disabled={projectAPI.loading}
          />
          <button 
            onClick={createProject} 
            disabled={!newProjectName.trim() || projectAPI.loading}
            className={projectAPI.loading ? 'loading' : ''}
          >
            Create Project
          </button>
        </div>
      </div>

      {/* Projects List */}
      {projects.length > 0 && (
        <div className="projects-section">
          <h2>Your Projects</h2>
          <div className="projects-grid">
            {projects.map(project => (
              <ProjectCard
                key={project.id}
                project={project}
                isActive={activeProject === project.id}
                onSelect={setActiveProject}
                onDelete={deleteProject}
              />
            ))}
          </div>
        </div>
      )}

      {/* Active Project Task Management */}
      {currentProject && (
        <div className="tasks-section">
          <h2>🎯 {currentProject.name} - Tasks</h2>
          
          {/* API Status for Task Operations */}
          <APIStatus
            loading={taskAPI.loading}
            error={taskAPI.error}
            onClearError={taskAPI.clearError}
          />
          
          {/* Add Task */}
          <div className="task-creation">
            <h3>Create New Task</h3>
            <div className="input-group">
              <input
                type="text"
                value={newTaskName}
                onChange={(e) => setNewTaskName(e.target.value)}
                placeholder="Enter task name..."
                onKeyPress={(e) => e.key === 'Enter' && createTask()}
                disabled={taskAPI.loading}
              />
              <button 
                onClick={createTask} 
                disabled={!newTaskName.trim() || taskAPI.loading}
                className={taskAPI.loading ? 'loading' : ''}
              >
                Create Task
              </button>
            </div>
          </div>

          {/* Tasks List */}
          {tasks && tasks.length > 0 ? (
            <div className="tasks-grid">
              {tasks.map(task => (
                <TaskCard
                  key={task.id}
                  task={task}
                  isActive={activeTask === task.id}
                  onSelect={setActiveTask}
                  onDelete={deleteTask}
                  todoCount={getTaskTodoCount(task)}
                  completedCount={getTaskCompletedCount(task)}
                />
              ))}
            </div>
          ) : (
            <div className="empty-state">
              <p>📝 Create your first task to start adding todos!</p>
            </div>
          )}
        </div>
      )}

      {/* Active Task Todo Management */}
      {currentTask && (
        <div className="todos-section">
          <h2>✅ {currentTask.name} - Todos</h2>
          
          {/* API Status for Todo Operations */}
          <APIStatus
            loading={todoAPI.loading}
            error={todoAPI.error}
            onClearError={todoAPI.clearError}
          />
          
          {/* Add Todo */}
          <div className="input-group">
            <input
              type="text"
              value={newTodoText}
              onChange={(e) => setNewTodoText(e.target.value)}
              placeholder="Add a new todo..."
              onKeyPress={(e) => e.key === 'Enter' && addTodo()}
              disabled={todoAPI.loading}
            />
            <button 
              onClick={addTodo} 
              disabled={!newTodoText.trim() || todoAPI.loading}
              className={todoAPI.loading ? 'loading' : ''}
            >
              Add Todo
            </button>
          </div>

          {/* Todos List */}
          {todos && todos.length > 0 ? (
            <div className="todos-list">
              {todos.map(todo => (
                <TodoItem
                  key={todo.id}
                  todo={todo}
                  onToggle={toggleTodo}
                  onDelete={deleteTodo}
                />
              ))}
            </div>
          ) : (
            <p className="empty-state">No todos yet. Add one above! 👆</p>
          )}
        </div>
      )}

      {projects.length === 0 && (
        <div className="empty-state">
          <p>🚀 Create your first project to get started!</p>
        </div>
      )}
    </div>
  )
}

export default App
