import { useState } from 'react'

const ProjectForm = ({ 
  onSubmit, 
  onCancel, 
  loading = false, 
  initialData = {},
  submitText = "Create Project",
  showCancel = false 
}) => {
  const [formData, setFormData] = useState({
    name: initialData.name || '',
    description: initialData.description || ''
  })

  const [errors, setErrors] = useState({})

  const validateForm = () => {
    const newErrors = {}
    
    if (!formData.name.trim()) {
      newErrors.name = 'Project name is required'
    } else if (formData.name.trim().length < 3) {
      newErrors.name = 'Project name must be at least 3 characters'
    }

    if (formData.description && formData.description.length > 500) {
      newErrors.description = 'Description must not exceed 500 characters'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    
    if (!validateForm()) {
      return
    }

    onSubmit({
      name: formData.name.trim(),
      description: formData.description.trim()
    })
  }

  const handleChange = (field) => (e) => {
    const value = e.target.value
    setFormData(prev => ({ ...prev, [field]: value }))
    
    // Clear field error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: null }))
    }
  }

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      handleSubmit(e)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="project-form">
      <div className="form-group">
        <label htmlFor="project-name">Project Name *</label>
        <input
          id="project-name"
          type="text"
          value={formData.name}
          onChange={handleChange('name')}
          onKeyPress={handleKeyPress}
          placeholder="Enter project name..."
          disabled={loading}
          className={errors.name ? 'error' : ''}
          maxLength={100}
        />
        {errors.name && <span className="error-message">{errors.name}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="project-description">Description</label>
        <textarea
          id="project-description"
          value={formData.description}
          onChange={handleChange('description')}
          placeholder="Optional project description..."
          disabled={loading}
          className={errors.description ? 'error' : ''}
          maxLength={500}
          rows={3}
        />
        {errors.description && <span className="error-message">{errors.description}</span>}
        <small className="char-count">
          {formData.description.length}/500 characters
        </small>
      </div>

      <div className="form-actions">
        <button
          type="submit"
          disabled={!formData.name.trim() || loading}
          className={loading ? 'loading' : ''}
        >
          {loading ? 'Creating...' : submitText}
        </button>
        
        {showCancel && (
          <button
            type="button"
            onClick={onCancel}
            disabled={loading}
            className="secondary"
          >
            Cancel
          </button>
        )}
      </div>
    </form>
  )
}

export default ProjectForm
