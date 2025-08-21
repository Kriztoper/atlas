import { useState } from 'react'
import './App.css'
import ProjectCard from './components/ProjectCard'
import TaskCard from './components/TaskCard'
import TodoItem from './components/TodoItem'

function App() {
  const [projects, setProjects] = useState([])
  const [activeProject, setActiveProject] = useState(null)
  const [activeTask, setActiveTask] = useState(null)
  const [newProjectName, setNewProjectName] = useState('')
  const [newTaskName, setNewTaskName] = useState('')
  const [newTodoText, setNewTodoText] = useState('')

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
  const createProject = () => {
    if (newProjectName.trim()) {
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
  const createTask = () => {
    if (newTaskName.trim() && activeProject) {
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
  const addTodo = () => {
    if (newTodoText.trim() && activeProject && activeTask) {
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

  const toggleTodo = (todoId) => {
    setProjects(projects.map(project => 
      project.id === activeProject
        ? {
            ...project,
            tasks: project.tasks.map(task =>
              task.id === activeTask
                ? {
                    ...task,
                    todos: task.todos.map(todo =>
                      todo.id === todoId ? { ...todo, completed: !todo.completed } : todo
                    )
                  }
                : task
            )
          }
        : project
    ))
  }

  const deleteTodo = (todoId) => {
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

  const currentProject = projects.find(p => p.id === activeProject)
  const currentTask = currentProject?.tasks?.find(t => t.id === activeTask)

  return (
    <div className="app">
      <h1>📋 Atlas</h1>
      
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
          />
          <button onClick={createProject} disabled={!newProjectName.trim()}>
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
              />
              <button onClick={createTask} disabled={!newTaskName.trim()}>
                Create Task
              </button>
            </div>
          </div>

          {/* Tasks List */}
          {currentProject.tasks && currentProject.tasks.length > 0 ? (
            <div className="tasks-grid">
              {currentProject.tasks.map(task => (
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
          
          {/* Add Todo */}
          <div className="input-group">
            <input
              type="text"
              value={newTodoText}
              onChange={(e) => setNewTodoText(e.target.value)}
              placeholder="Add a new todo..."
              onKeyPress={(e) => e.key === 'Enter' && addTodo()}
            />
            <button onClick={addTodo} disabled={!newTodoText.trim()}>
              Add Todo
            </button>
          </div>

          {/* Todos List */}
          {currentTask.todos && currentTask.todos.length > 0 ? (
            <div className="todos-list">
              {currentTask.todos.map(todo => (
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
