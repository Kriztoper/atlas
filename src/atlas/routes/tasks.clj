(ns atlas.routes.tasks
  "Task-related routes."
  (:require [compojure.core :refer [defroutes GET POST PUT PATCH DELETE]]
            [atlas.handlers.tasks :as handlers]))

(defroutes tasks-routes
  "Routes for task operations."
  
  ;; Task CRUD operations  
  (GET    "/api/tasks"                      []    handlers/get-all-tasks)
  (GET    "/api/tasks/:task-id"            []    handlers/get-task-by-id)
  (PUT    "/api/tasks/:task-id"            []    handlers/update-task)
  (DELETE "/api/tasks/:task-id"            []    handlers/delete-task)
  
  ;; Project-scoped task operations
  (GET    "/api/projects/:project-id/tasks"     []    handlers/get-tasks-by-project)
  (POST   "/api/projects/:project-id/tasks"     []    handlers/create-task)
  
  ;; Tasks with statistics
  (GET    "/api/projects/:project-id/tasks/stats" [] handlers/get-tasks-by-project-with-stats))
