import React from 'react'

const ProgressBar = ({ total, completed, size = 'md', showLabel = true }) => {
  const percentage = total > 0 ? Math.round((completed / total) * 100) : 0
  
  const sizeClasses = {
    sm: 'progress-bar-sm',
    md: 'progress-bar-md',
    lg: 'progress-bar-lg'
  }

  return (
    <div className={`progress-bar ${sizeClasses[size]}`}>
      {showLabel && (
        <div className="progress-label">
          <span className="progress-text">{completed} of {total} completed</span>
          <span className="progress-percentage">{percentage}%</span>
        </div>
      )}
      <div className="progress-track">
        <div 
          className="progress-fill"
          style={{ 
            width: `${percentage}%`,
            backgroundColor: percentage === 100 ? 'var(--color-success-500)' : 'var(--color-primary-500)'
          }}
        />
      </div>
    </div>
  )
}

export default ProgressBar
