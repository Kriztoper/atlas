const LoadingSpinner = ({ 
  size = 'medium', 
  color = 'primary', 
  className = '',
  label = 'Loading...'
}) => {
  const sizeClass = `spinner--${size}`
  const colorClass = `spinner--${color}`
  
  const classes = [
    'spinner',
    sizeClass,
    colorClass,
    className
  ].filter(Boolean).join(' ')

  return (
    <div className={classes} role="status" aria-label={label}>
      <div className="spinner__circle">
        <div className="spinner__path"></div>
      </div>
      <span className="sr-only">{label}</span>
    </div>
  )
}

export default LoadingSpinner
