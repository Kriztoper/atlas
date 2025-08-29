(ns atlas.routes.todos
  "Todo-related routes."
  (:require [compojure.core :refer [defroutes GET POST PUT PATCH DELETE]]
            [atlas.handlers.todos :as handlers]))

(defroutes todos-routes
  "Routes for todo operations."
  
  ;; Todo CRUD operations
  (GET    "/api/todos"                      []    handlers/get-all-todos)
  (GET    "/api/todos/:todo-id"            []    handlers/get-todo-by-id)
  (PUT    "/api/todos/:todo-id"            []    handlers/update-todo)
  (DELETE "/api/todos/:todo-id"            []    handlers/delete-todo)
  
  ;; Task-scoped todo operations
  (GET    "/api/tasks/:task-id/todos"           []    handlers/get-todos-by-task)
  (POST   "/api/tasks/:task-id/todos"           []    handlers/create-todo)
  
  ;; Todo completion operations
  (PATCH  "/api/todos/:todo-id/toggle"          []    handlers/toggle-todo-completion)
  (PATCH  "/api/todos/:todo-id/complete"        []    handlers/mark-todo-completed)
  (PATCH  "/api/todos/:todo-id/pending"         []    handlers/mark-todo-pending)
  
  ;; Todo filtering and stats
  (GET    "/api/tasks/:task-id/todos/completed" []    handlers/get-completed-todos-by-task)
  (GET    "/api/tasks/:task-id/todos/pending"   []    handlers/get-pending-todos-by-task)
  (GET    "/api/tasks/:task-id/todos/stats"     []    handlers/get-todo-stats-by-task))
