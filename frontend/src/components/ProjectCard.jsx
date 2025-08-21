import React from 'react'

const ProjectCard = ({ project, isActive, onSelect, onDelete }) => {
  const taskCount = project.tasks?.length || 0
  const totalTodos = project.tasks?.reduce((total, task) => total + (task.todos?.length || 0), 0) || 0
  const completedTodos = project.tasks?.reduce((total, task) => 
    total + (task.todos?.filter(todo => todo.completed).length || 0), 0) || 0

  return (
    <div className={`project-card ${isActive ? 'active' : ''}`}>
      <h3>{project.name}</h3>
      <p>{taskCount} task{taskCount !== 1 ? 's' : ''}</p>
      <p>{totalTodos} todo{totalTodos !== 1 ? 's' : ''}</p>
      <p className="completed-count">
        {completedTodos} completed
      </p>
      <div className="project-actions">
        <button 
          onClick={() => onSelect(project.id)}
          className={isActive ? 'active' : ''}
        >
          {isActive ? 'Active' : 'Select'}
        </button>
        <button 
          onClick={() => onDelete(project.id)}
          className="delete-btn"
        >
          Delete
        </button>
      </div>
    </div>
  )
}

export default ProjectCard
