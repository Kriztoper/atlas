import React from 'react'

const APIStatus = ({ loading, error, onClearError }) => {
  if (!loading && !error) return null

  return (
    <div className="api-status">
      {loading && (
        <div className="loading-indicator">
          <div className="loading-spinner"></div>
          <span>Loading...</span>
        </div>
      )}
      
      {error && (
        <div className="error-message">
          <span className="error-icon">⚠️</span>
          <span className="error-text">{error}</span>
          {onClearError && (
            <button 
              onClick={onClearError}
              className="error-close-btn"
              aria-label="Dismiss error"
            >
              ✕
            </button>
          )}
        </div>
      )}
    </div>
  )
}

export default APIStatus
