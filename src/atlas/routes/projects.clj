(ns atlas.routes.projects
  "Project-related routes."
  (:require [compojure.core :refer [defroutes GET POST PUT PATCH DELETE]]
            [atlas.handlers.projects :as handlers]))

(defroutes projects-routes
  "Routes for project operations."
  
  ;; Basic CRUD operations
  (GET    "/api/projects"           []       handlers/get-all-projects)
  (POST   "/api/projects"           []       handlers/create-project)
  (GET    "/api/projects/:project-id" []     handlers/get-project-by-id)
  (PUT    "/api/projects/:project-id" []     handlers/update-project)
  (DELETE "/api/projects/:project-id" []     handlers/delete-project)
  
  ;; Projects with statistics
  (GET    "/api/projects/stats"     []       handlers/get-all-projects-with-stats))
