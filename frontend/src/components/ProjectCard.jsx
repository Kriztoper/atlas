import React from 'react'

const ProjectCard = ({ project, isActive, onSelect, onDelete }) => {
  const completedCount = project.todos.filter(todo => todo.completed).length
  const totalCount = project.todos.length

  return (
    <div className={`project-card ${isActive ? 'active' : ''}`}>
      <h3>{project.name}</h3>
      <p>{totalCount} todo{totalCount !== 1 ? 's' : ''}</p>
      <p className="completed-count">
        {completedCount} completed
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
