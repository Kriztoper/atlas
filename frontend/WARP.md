# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Atlas is a React-based task management application with a three-level hierarchy: **Projects** → **Tasks** → **Todos**. The frontend is built with React 18, Vite, and modern JavaScript, featuring a clean component-based architecture with API integration and graceful fallback to local state.

## Common Development Commands

### Development
```bash
# Start development server (runs on http://localhost:5173)
npm run dev

# Build for production
npm run build

# Preview production build locally
npm run preview

# Run linting
npm run lint
```

### Installation
```bash
# Install dependencies
npm install

# Clean install
rm -rf node_modules package-lock.json && npm install
```

## Architecture Overview

### Technology Stack
- **React 18** with modern hooks (useState, useCallback, custom hooks)
- **Vite** for build tooling and dev server
- **ESLint 9** with React hooks and refresh plugins
- **Pure CSS** with flexbox/grid (no external UI libraries)

### Core Architecture Patterns

#### 1. **Three-Tier State Management**
The application uses a hierarchical state structure where:
- **Projects** contain multiple **Tasks**  
- **Tasks** contain multiple **Todos**
- All state flows down via props, updates flow up via callbacks
- State is managed at the App.jsx level using React hooks

#### 2. **API-First with Graceful Fallback**
All operations attempt API calls first, then fallback to local state:
```javascript
try {
  await todoAPI.createTodo(taskId, todoData, onSuccess)
} catch (error) {
  console.warn('API call failed, using local state:', error)
  // Local state fallback logic
}
```

#### 3. **Custom Hooks Pattern**
- `useAPI()` - Generic API operations with loading/error states
- `useProjectAPI()` - Project-specific operations
- `useTaskAPI()` - Task-specific operations  
- `useTodoAPI()` - Todo-specific operations

### Directory Structure
```
src/
├── App.jsx                 # Main application component and state management
├── App.css                 # Main styling with CSS Grid/Flexbox
├── index.css               # Global styles
├── main.jsx                # React entry point
├── components/
│   ├── ProjectCard.jsx     # Project display with stats
│   ├── TaskCard.jsx        # Task display with completion tracking  
│   ├── TodoItem.jsx        # Individual todo with checkbox/delete
│   └── APIStatus.jsx       # Loading/error state component
├── hooks/
│   └── useAPI.js           # Custom hooks for API operations
└── services/
    └── api.js              # API service layer with REST endpoints
```

### Component Responsibilities

#### **App.jsx (Main Controller)**
- Manages all application state (projects, active selections)
- Orchestrates API calls with fallback logic
- Handles hierarchical data updates
- Renders conditional sections based on active selections

#### **Card Components (ProjectCard, TaskCard)**
- Display entity information with statistics
- Handle selection and deletion actions
- Show visual active/inactive states
- Calculate completion counts from nested data

#### **TodoItem**
- Manages individual todo interactions
- Handles checkbox toggling and deletion
- Shows completed/pending visual states

#### **API Layer**
- **services/api.js**: REST API calls with fetch
- **hooks/useAPI.js**: React hooks with loading/error states
- **APIStatus.jsx**: User-facing status indicators

## Key Development Patterns

### State Updates
When updating nested state (e.g., adding a todo to a task in a project):
```javascript
setProjects(projects.map(project => 
  project.id === activeProject
    ? {
        ...project,
        tasks: project.tasks.map(task =>
          task.id === activeTask
            ? { ...task, todos: [...task.todos, newTodo] }
            : task
        )
      }
    : project
))
```

### API Configuration
- Base URL: `http://localhost:3001/api` (configurable via `VITE_API_URL`)
- All endpoints follow REST conventions
- Graceful error handling with user-friendly messages
- Loading states disable forms during operations

### Component Props Pattern
- **Data down**: Pass entity objects and derived counts as props
- **Events up**: Pass callback functions for user actions
- **Active state**: Pass boolean flags for visual highlighting
- **Loading state**: Disable inputs during API operations

### Error Handling
- API failures log warnings and fall back to local state
- User-facing errors show in APIStatus components
- Network errors provide helpful connectivity messages
- All error states are dismissible by user

## Environment Variables

```bash
# API base URL (optional, defaults to localhost:3001)
VITE_API_URL=https://your-api-domain.com/api
```

## Testing Notes

While there are no tests currently implemented, when writing tests consider:
- Component rendering with various prop combinations
- State management and hierarchical updates
- API success/failure scenarios
- User interactions (keyboard events, form submissions)
- Loading and error state handling

## API Endpoints (Designed)

The frontend is designed to work with these REST endpoints:
- `POST /api/projects` - Create project
- `POST /api/projects/:id/tasks` - Create task
- `POST /api/tasks/:id/todos` - Add todo
- `PATCH /api/todos/:id/complete` - Toggle completion
- `DELETE /api/todos/:id` - Remove todo

See `API_DOCUMENTATION.md` for complete endpoint specifications and request/response formats.
