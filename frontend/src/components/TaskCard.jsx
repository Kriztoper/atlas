import React from 'react'

const TaskCard = ({ task, isActive, onSelect, onDelete, todoCount, completedCount }) => {
  return (
    <div className={`task-card ${isActive ? 'active' : ''}`}>
      <h3>{task.name}</h3>
      <p>{todoCount} todo{todoCount !== 1 ? 's' : ''}</p>
      <p className="completed-count">
        {completedCount} completed
      </p>
      <div className="task-actions">
        <button 
          onClick={() => onSelect(task.id)}
          className={isActive ? 'active' : ''}
        >
          {isActive ? 'Active' : 'Select'}
        </button>
        <button 
          onClick={() => onDelete(task.id)}
          className="delete-btn"
        >
          Delete
        </button>
      </div>
    </div>
  )
}

export default TaskCard
