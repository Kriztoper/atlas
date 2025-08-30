import React from 'react'

const TodoItem = ({ todo, onToggle, onDelete }) => {
  return (
    <div className={`todo-item ${todo.isCompleted ? 'completed' : ''}`}>
      <div className="todo-content">
        <input
          type="checkbox"
          checked={todo.isCompleted}
          onChange={() => onToggle(todo.id)}
        />
        <span className={`todo-text ${todo.isCompleted ? 'strikethrough' : ''}`}>
          {todo.text}
        </span>
      </div>
      <button 
        onClick={() => onDelete(todo.id)}
        className="delete-btn small"
        aria-label="Delete todo"
      >
        ✗
      </button>
    </div>
  )
}

export default TodoItem
