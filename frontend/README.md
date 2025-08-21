# 📋 Project Todo Manager

A beautiful and intuitive todo list application built with React and Vite. Organize your tasks by creating projects and managing todos within each project.

## 📁 Project Structure:
```
frontend/
├── src/
│   ├── App.jsx           # Main application component
│   ├── App.css           # Main styling
│   ├── index.css         # Global styles
│   ├── main.jsx          # Application entry point
│   └── components/
│       ├── ProjectCard.jsx   # Project display component
│       └── TodoItem.jsx      # Individual todo component
├── public/               # Static assets
├── dist/                # Built production files
├── README.md            # Comprehensive documentation
└── package.json         # Dependencies and scripts
```

## 🚀 Features

- **Project Management**: Create and delete projects to organize your todos
  - ✅ Create new projects with custom names
  - ✅ Delete projects (with all their todos)
  - ✅ Visual project cards showing todo counts and completion status
  - ✅ Active project selection and highlighting
- **Todo Management**: Add, complete, and delete todos within each project
  - ✅ Add todos to the selected project
  - ✅ Mark todos as completed/incomplete with checkboxes
  - ✅ Delete individual todos
  - ✅ Visual distinction between completed and pending todos
- **Visual Progress**: See completion counts for each project
- **Responsive Design**: Works great on desktop and mobile devices
  - ✅ Modern, responsive design with gradient background
  - ✅ Clean white container with rounded corners and shadows
  - ✅ Smooth animations and hover effects
  - ✅ Mobile-responsive layout
  - ✅ Emojis and intuitive visual feedback
- **Modern UI**: Clean, professional interface with smooth animations
- **User Experience**:
  - ✅ Keyboard support (Enter key for quick actions)
  - ✅ Form validation (disabled buttons when inputs are empty)
  - ✅ Empty states with helpful messaging
  - ✅ Real-time project statistics
- **Real-time Updates**: Instant feedback as you interact with the app
- **Technical Quality**:
  - ✅ Component-based architecture
  - ✅ React hooks for state management
  - ✅ Clean, maintainable code structure
  - ✅ No external dependencies (pure React + Vite)
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

## 📖 How to Use

### Creating a Project
1. Enter a project name in the "Create New Project" section
2. Click "Create Project" or press Enter
3. Your new project will appear in the projects grid and be automatically selected

### Managing Todos
1. Select a project by clicking the "Select" button on any project card
2. In the active project section, enter a todo description
3. Click "Add Todo" or press Enter to add it to the project
4. Check the checkbox to mark todos as completed
5. Click the "✗" button to delete a todo

### Project Management
- **Active Project**: The currently selected project is highlighted with a blue border
- **Project Stats**: Each project card shows total todos and completion count
- **Delete Projects**: Use the red "Delete" button to remove a project and all its todos

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
