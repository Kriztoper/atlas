import React from 'react'
import ProgressBar from './ProgressBar'

const ProjectCard = ({ project, isActive, onSelect, onDelete }) => {
  const taskCount = project.tasks?.length || 0
  const totalTodos = project.tasks?.reduce((total, task) => total + (task.todos?.length || 0), 0) || 0
  const completedTodos = project.tasks?.reduce((total, task) => 
    total + (task.todos?.filter(todo => todo.completed).length || 0), 0) || 0

  return (
    <div className={`project-card ${isActive ? 'active' : ''}`} onClick={() => onSelect(project.id)}>
      <h3>
        📁 {project.name}
        {isActive && <span className="status-badge completed" style={{ marginLeft: 'var(--space-3)', fontSize: 'var(--font-size-xs)' }}>ACTIVE</span>}
      </h3>
      
      <div style={{ marginBottom: 'var(--space-4)' }}>
        <p>📋 {taskCount} task{taskCount !== 1 ? 's' : ''}</p>
        <p>📝 {totalTodos} todo{totalTodos !== 1 ? 's' : ''}</p>
        {totalTodos > 0 && (
          <ProgressBar 
            total={totalTodos} 
            completed={completedTodos} 
            size="sm" 
            showLabel={false}
          />
        )}
        <p className="completed-count">
          ✅ {completedTodos} completed
        </p>
      </div>
      
      <div className="project-actions">
        <button 
          onClick={(e) => {
            e.stopPropagation()
            onSelect(project.id)
          }}
          className={isActive ? 'active' : ''}
        >
          {isActive ? '👁️ Active' : '👀 Select'}
        </button>
        <button 
          onClick={(e) => {
            e.stopPropagation()
            onDelete(project.id)
          }}
          className="delete-btn"
        >
          🗑️ Delete
        </button>
      </div>
    </div>
  )
}

export default ProjectCard
