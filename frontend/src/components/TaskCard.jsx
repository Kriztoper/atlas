import React from 'react'
import ProgressBar from './ProgressBar'

const TaskCard = ({ task, isActive, onSelect, onDelete, todoCount, completedCount }) => {
  return (
    <div className={`task-card ${isActive ? 'active' : ''}`} onClick={() => onSelect(task.id)}>
      <h3>
        📝 {task.name}
        {isActive && <span className="status-badge in-progress" style={{ marginLeft: 'var(--space-3)', fontSize: 'var(--font-size-xs)' }}>ACTIVE</span>}
      </h3>
      
      <div style={{ marginBottom: 'var(--space-4)' }}>
        <p>📋 {todoCount} todo{todoCount !== 1 ? 's' : ''}</p>
        {todoCount > 0 && (
          <ProgressBar 
            total={todoCount} 
            completed={completedCount} 
            size="sm" 
            showLabel={false}
          />
        )}
        <p className="completed-count">
          ✅ {completedCount} completed
        </p>
      </div>
      
      <div className="task-actions">
        <button 
          onClick={(e) => {
            e.stopPropagation()
            onSelect(task.id)
          }}
          className={isActive ? 'active' : ''}
        >
          {isActive ? '👁️ Active' : '👀 Select'}
        </button>
        <button 
          onClick={(e) => {
            e.stopPropagation()
            onDelete(task.id)
          }}
          className="delete-btn"
        >
          🗑️ Delete
        </button>
      </div>
    </div>
  )
}

export default TaskCard
