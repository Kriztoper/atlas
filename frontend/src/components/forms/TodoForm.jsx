import { useState } from 'react'

const TodoForm = ({ 
  onSubmit, 
  onCancel, 
  loading = false, 
  initialData = {},
  submitText = "Add Todo",
  showCancel = false,
  placeholder = "Add a new todo..." 
}) => {
  const [formData, setFormData] = useState({
    text: initialData.text || '',
    description: initialData.description || ''
  })

  const [errors, setErrors] = useState({})

  const validateForm = () => {
    const newErrors = {}
    
    if (!formData.text.trim()) {
      newErrors.text = 'Todo text is required'
    } else if (formData.text.trim().length < 1) {
      newErrors.text = 'Todo text cannot be empty'
    }

    if (formData.description && formData.description.length > 300) {
      newErrors.description = 'Description must not exceed 300 characters'
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
      text: formData.text.trim(),
      description: formData.description.trim()
    })

    // Reset form after successful submission
    setFormData({ text: '', description: '' })
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
    <form onSubmit={handleSubmit} className="todo-form">
      <div className="form-group">
        <label htmlFor="todo-text" className="sr-only">Todo Text</label>
        <input
          id="todo-text"
          type="text"
          value={formData.text}
          onChange={handleChange('text')}
          onKeyPress={handleKeyPress}
          placeholder={placeholder}
          disabled={loading}
          className={errors.text ? 'error' : ''}
          maxLength={300}
        />
        {errors.text && <span className="error-message">{errors.text}</span>}
      </div>

      {initialData.showDescription !== false && (
        <div className="form-group description-group">
          <label htmlFor="todo-description" className="sr-only">Todo Description</label>
          <textarea
            id="todo-description"
            value={formData.description}
            onChange={handleChange('description')}
            placeholder="Optional description..."
            disabled={loading}
            className={errors.description ? 'error' : ''}
            maxLength={300}
            rows={2}
          />
          {errors.description && <span className="error-message">{errors.description}</span>}
          {formData.description && (
            <small className="char-count">
              {formData.description.length}/300 characters
            </small>
          )}
        </div>
      )}

      <div className="form-actions">
        <button
          type="submit"
          disabled={!formData.text.trim() || loading}
          className={loading ? 'loading' : ''}
        >
          {loading ? 'Adding...' : submitText}
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

export default TodoForm
