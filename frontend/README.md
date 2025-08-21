# 📋 Atlas

A comprehensive task management application built with React and Vite. Organize your work using a powerful three-level hierarchy: **Projects** → **Tasks** → **Todos**.

## 📋 Project Structure:
```
frontend/
├── src/
│   ├── App.jsx           # Main application component
│   ├── App.css           # Main styling
│   ├── index.css         # Global styles
│   ├── main.jsx          # Application entry point
│   └── components/
│       ├── ProjectCard.jsx   # Project display component
│       ├── TaskCard.jsx      # Task display component
│       └── TodoItem.jsx      # Individual todo component
├── public/               # Static assets
├── dist/                # Built production files
├── README.md            # Comprehensive documentation
└── package.json         # Dependencies and scripts
```

## 🚀 Features

### 🗂 Three-Level Organization Hierarchy
- **Projects** → **Tasks** → **Todos**
- Perfect for complex project management and organization
- Color-coded sections for easy visual distinction

### 💼 Project Management
- ✅ Create new projects with custom names
- ✅ Delete projects (removes all tasks and todos)
- ✅ Visual project cards showing task counts and total completion progress
- ✅ Active project selection with blue highlighting
- ✅ Aggregate statistics across all tasks

### 🎯 Task Management
- ✅ Create tasks within selected projects
- ✅ Delete tasks (removes all associated todos)
- ✅ Visual task cards with yellow/amber styling
- ✅ Active task selection with enhanced highlighting
- ✅ Individual task completion tracking

### ✅ Todo Management
- ✅ Add todos to the selected task
- ✅ Mark todos as completed/incomplete with checkboxes
- ✅ Delete individual todos
- ✅ Visual distinction between completed and pending todos
- ✅ Strikethrough text for completed items

### 📊 Visual Progress Tracking
- ✅ Real-time completion counts at project level
- ✅ Individual task progress indicators
- ✅ Color-coded completion status
- ✅ Hierarchical progress aggregation

### 🎨 Modern UI Design
- ✅ Clean, professional interface with smooth animations
- ✅ Color-coded sections (Blue for projects, Amber for tasks, White for todos)
- ✅ Responsive design that works on desktop and mobile
- ✅ Gradient background with card-based layout
- ✅ Hover effects and visual feedback

### 🚀 User Experience
- ✅ Keyboard support (Enter key for quick actions)
- ✅ Form validation (disabled buttons when inputs are empty)
- ✅ Empty states with helpful messaging
- ✅ Intuitive navigation between hierarchy levels
- ✅ Real-time updates and instant feedback

### ⚙️ Technical Quality
- ✅ Component-based React architecture
- ✅ Modern React hooks for state management
- ✅ Clean, maintainable code structure
- ✅ No external dependencies (pure React + Vite)
- ✅ Fully responsive CSS Grid and Flexbox layout
- ✅ Production build ready

## 🛠️ Getting Started

### Prerequisites
- Node.js (v16 or higher)
- npm or yarn

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open your browser and visit `http://localhost:5173`

## 📝 How to Use

### 💼 Step 1: Creating a Project
1. Enter a project name in the "Create New Project" section
2. Click "Create Project" or press Enter
3. Your new project will appear in the projects grid and be automatically selected
4. The project shows with a **blue border** and displays task/todo counts

### 🎯 Step 2: Creating Tasks
1. With a project selected, you'll see the "Tasks" section appear
2. Enter a task name in the "Create New Task" section
3. Click "Create Task" or press Enter
4. Your new task will appear with **amber/yellow styling** and be automatically selected
5. Tasks show individual todo counts and completion progress

### ✅ Step 3: Managing Todos
1. With both a project and task selected, the "Todos" section will appear
2. Enter a todo description in the input field
3. Click "Add Todo" or press Enter to add it to the current task
4. Check the checkbox to mark todos as completed
5. Click the "✗" button to delete individual todos
6. Completed todos show with strikethrough text and green background

### 📊 Navigation & Management
- **Active Project**: Currently selected project has blue highlighting
- **Active Task**: Currently selected task has enhanced amber highlighting
- **Project Stats**: Each project card shows total tasks, todos, and completion counts
- **Task Stats**: Each task card shows todo count and completion progress
- **Hierarchical Deletion**: 
  - Deleting a project removes all its tasks and todos
  - Deleting a task removes all its todos
  - Deleting todos only affects individual items

### 🎨 Visual Cues
- **Blue sections**: Project-related areas
- **Amber sections**: Task-related areas  
- **White/Gray sections**: Todo-related areas
- **Green accents**: Completion indicators
- **Red buttons**: Deletion actions

## 🏗️ Built With

- **React 18** - UI Framework
- **Vite** - Build tool and development server
- **CSS3** - Modern styling with flexbox and grid
- **JavaScript (ES6+)** - Modern JavaScript features

## 📱 Responsive Design

The app is fully responsive and adapts to different screen sizes:
- **Desktop**: Grid layout for projects with optimal spacing
- **Tablet**: Responsive grid that adjusts to screen width
- **Mobile**: Single column layout with touch-friendly buttons

## 🎨 Features in Detail

- **Local State Management**: All data is managed in React state (no external dependencies)
- **Keyboard Support**: Press Enter in input fields to quickly add projects/todos
- **Visual Feedback**: Hover effects, transitions, and clear visual states
- **Error Prevention**: Buttons are disabled when inputs are empty
- **Intuitive UX**: Clear visual hierarchy and user-friendly interactions

## 🚀 Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build locally
- `npm run lint` - Run ESLint for code quality

Enjoy organizing your projects and todos! 🎉

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
