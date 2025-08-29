import { forwardRef } from 'react'

const Button = forwardRef(({ 
  children, 
  variant = 'primary',
  size = 'medium', 
  loading = false,
  disabled = false,
  icon = null,
  iconPosition = 'left',
  fullWidth = false,
  className = '',
  onClick,
  type = 'button',
  ...props 
}, ref) => {
  const baseClass = 'btn'
  const variantClass = `btn--${variant}`
  const sizeClass = `btn--${size}`
  const loadingClass = loading ? 'btn--loading' : ''
  const disabledClass = (disabled || loading) ? 'btn--disabled' : ''
  const fullWidthClass = fullWidth ? 'btn--full-width' : ''
  const iconClass = icon ? `btn--with-icon btn--icon-${iconPosition}` : ''

  const classes = [
    baseClass,
    variantClass,
    sizeClass,
    loadingClass,
    disabledClass,
    fullWidthClass,
    iconClass,
    className
  ].filter(Boolean).join(' ')

  const handleClick = (e) => {
    if (!disabled && !loading && onClick) {
      onClick(e)
    }
  }

  return (
    <button
      ref={ref}
      type={type}
      className={classes}
      onClick={handleClick}
      disabled={disabled || loading}
      aria-disabled={disabled || loading}
      {...props}
    >
      {loading && <span className="btn__spinner" aria-hidden="true" />}
      {icon && iconPosition === 'left' && (
        <span className="btn__icon btn__icon--left" aria-hidden="true">
          {icon}
        </span>
      )}
      <span className="btn__content">
        {children}
      </span>
      {icon && iconPosition === 'right' && (
        <span className="btn__icon btn__icon--right" aria-hidden="true">
          {icon}
        </span>
      )}
    </button>
  )
})

Button.displayName = 'Button'

export default Button
