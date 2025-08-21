import { useState } from 'react'
import './App.css'
import ProjectCard from './components/ProjectCard'
import TodoItem from './components/TodoItem'

function App() {
  const [projects, setProjects] = useState([])
  const [activeProject, setActiveProject] = useState(null)
  const [newProjectName, setNewProjectName] = useState('')
  const [newTodoText, setNewTodoText] = useState('')

  // Project management functions
  const createProject = () => {
    if (newProjectName.trim()) {
      const newProject = {
        id: Date.now(),
        name: newProjectName.trim(),
        todos: [],
        createdAt: new Date().toISOString()
      }
      setProjects([...projects, newProject])
      setNewProjectName('')
      setActiveProject(newProject.id)
    }
  }

  const deleteProject = (projectId) => {
    setProjects(projects.filter(p => p.id !== projectId))
    if (activeProject === projectId) {
      setActiveProject(null)
    }
  }

  // Todo management functions
  const addTodo = () => {
    if (newTodoText.trim() && activeProject) {
      const newTodo = {
        id: Date.now(),
        text: newTodoText.trim(),
        completed: false,
        createdAt: new Date().toISOString()
      }
      
      setProjects(projects.map(project => 
        project.id === activeProject 
          ? { ...project, todos: [...project.todos, newTodo] }
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
            todos: project.todos.map(todo =>
              todo.id === todoId ? { ...todo, completed: !todo.completed } : todo
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
            todos: project.todos.filter(todo => todo.id !== todoId)
          }
        : project
    ))
  }

  const currentProject = projects.find(p => p.id === activeProject)

  return (
    <div className="app">
      <h1>📋 Project Todo Manager</h1>
      
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

      {/* Active Project Todo Management */}
      {currentProject && (
        <div className="todos-section">
          <h2>📝 {currentProject.name} - Todos</h2>
          
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
          {currentProject.todos.length > 0 ? (
            <div className="todos-list">
              {currentProject.todos.map(todo => (
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
