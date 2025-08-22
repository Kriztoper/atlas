import React from 'react'

const Breadcrumb = ({ items }) => {
  return (
    <nav className="breadcrumb">
      {items.map((item, index) => (
        <React.Fragment key={index}>
          <span 
            className={`breadcrumb-item ${item.active ? 'active' : ''}`}
            onClick={item.onClick}
            style={{ cursor: item.onClick ? 'pointer' : 'default' }}
          >
            {item.icon && <span style={{ marginRight: 'var(--space-2)' }}>{item.icon}</span>}
            {item.label}
          </span>
          {index < items.length - 1 && (
            <span className="breadcrumb-separator">›</span>
          )}
        </React.Fragment>
      ))}
    </nav>
  )
}

export default Breadcrumb
