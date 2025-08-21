# API Implementation Documentation

## Overview

This document outlines the API implementation for the Atlas project management application. The frontend includes API calls for the specified actions with graceful fallback to local state when the backend is unavailable.

## API Structure

### Base Configuration
- **Base URL**: `http://localhost:3001/api` (configurable via `VITE_API_URL` environment variable)
- **Content Type**: `application/json`
- **Error Handling**: Graceful fallback to local state if API fails

## Implemented API Endpoints

### 1. Create Project
- **Method**: `POST`
- **Endpoint**: `/api/projects`
- **Payload**:
  ```json
  {
    "name": "string",
    "description": "string" (optional)
  }
  ```
- **Response**: Project object with `id`, `name`, `tasks`, `createdAt`

### 2. Create Task
- **Method**: `POST`
- **Endpoint**: `/api/projects/:projectId/tasks`
- **Payload**:
  ```json
  {
    "name": "string",
    "description": "string" (optional),
    "projectId": "number"
  }
  ```
- **Response**: Task object with `id`, `name`, `todos`, `createdAt`

### 3. Add Todo
- **Method**: `POST`
- **Endpoint**: `/api/tasks/:taskId/todos`
- **Payload**:
  ```json
  {
    "text": "string",
    "description": "string" (optional),
    "taskId": "number",
    "completed": false
  }
  ```
- **Response**: Todo object with `id`, `text`, `completed`, `createdAt`

### 4. Mark Todo as Completed
- **Method**: `PATCH`
- **Endpoint**: `/api/todos/:todoId/complete`
- **Payload**:
  ```json
  {
    "completed": "boolean",
    "completedAt": "string (ISO date)" | null
  }
  ```
- **Response**: Updated todo object

### 5. Remove Todo
- **Method**: `DELETE`
- **Endpoint**: `/api/todos/:todoId`
- **Response**: 204 No Content or success confirmation

## Implementation Details

### File Structure
```
src/
├── services/
│   └── api.js              # API service functions
├── hooks/
│   └── useAPI.js           # Custom React hooks for API operations
└── components/
    └── APIStatus.jsx       # Loading and error state component
```

### Key Features

#### 1. **Graceful Fallback**
All API calls include try-catch blocks that fall back to local state if the API is unavailable:

```javascript
try {
  await todoAPI.createTodo(taskId, todoData, onSuccess)
} catch (error) {
  console.warn('API call failed, using local state:', error)
  // Fallback to local state operations
}
```

#### 2. **Loading States**
- Visual loading spinners on buttons during API operations
- Disabled form inputs during API calls
- Loading indicators in API status components

#### 3. **Error Handling**
- Network error detection and user-friendly messages
- Dismissible error notifications
- Automatic fallback behavior

#### 4. **React Hooks Integration**
Custom hooks provide clean interfaces for API operations:
- `useProjectAPI()` - Project-related operations
- `useTaskAPI()` - Task-related operations  
- `useTodoAPI()` - Todo-related operations

## Usage Examples

### Creating a Project
```javascript
const projectAPI = useProjectAPI()

await projectAPI.createProject(
  { name: 'My New Project' },
  (newProject) => {
    // Handle successful creation
    setProjects([...projects, newProject])
  }
)
```

### Adding a Todo
```javascript
const todoAPI = useTodoAPI()

await todoAPI.createTodo(
  taskId,
  { text: 'Complete documentation' },
  (newTodo) => {
    // Handle successful creation
    updateTodosList(newTodo)
  }
)
```

## Environment Configuration

Set the API base URL via environment variable:
```bash
# .env.local
VITE_API_URL=https://your-api-domain.com/api
```

## Error Scenarios Handled

1. **Network Unavailable**: Falls back to local state with console warning
2. **Server Error (4xx/5xx)**: Shows error message, maintains local state
3. **Timeout**: Graceful degradation to local functionality
4. **Invalid Response**: Error handling with user notification

## Benefits

- **Resilient**: Application works offline or with API unavailable
- **User-Friendly**: Clear loading states and error messages
- **Maintainable**: Clean separation of API logic and UI components
- **Scalable**: Easy to add new API endpoints using existing patterns
- **Type-Safe**: Consistent data structures between API and local state

## Future Enhancements

- Add request caching and retry logic
- Implement optimistic updates
- Add request queuing for offline scenarios
- Include data synchronization when connection restored
