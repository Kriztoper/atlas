(ns atlas.routes
  "Main application routes."
  (:require [compojure.core :refer [defroutes routes GET POST PUT PATCH DELETE OPTIONS]]
            [compojure.route :as route]
            [atlas.handlers :as handlers]
            [atlas.routes.projects :refer [projects-routes]]
            [atlas.routes.tasks :refer [tasks-routes]]
            [atlas.routes.todos :refer [todos-routes]]
            [atlas.middleware.core :as middleware]))

(defroutes core-routes
  "Core application routes."
  (GET "/" [] handlers/home)
  (GET "/api" [] handlers/home))  ; API root

(defroutes legacy-routes
  "Legacy routes for backward compatibility."
  ;; Keep existing routes for backward compatibility
  (POST "/api/projects" [] handlers/create-project)
  (GET "/api/projects" [] handlers/all-projects)
  (POST "/api/projects/:project-id/tasks" [] handlers/create-task)
  (GET "/api/projects/:project-id/tasks" [project-id] (handlers/get-tasks-by-project project-id))
  (POST "/api/tasks/:task-id/todos" [] handlers/create-todo)
  (GET "/api/tasks/:task-id/todos" [task-id] (handlers/get-todos-by-task task-id)))

(def all-routes
  "Combined routes from all modules."
  (routes
    core-routes
    projects-routes
    tasks-routes  
    todos-routes
    legacy-routes ; Keep legacy routes last as fallback
    (route/not-found {:status 404
                      :headers {"Content-Type" "application/json"}
                      :body "{\"error\": \"Not Found\", \"message\": \"The requested resource was not found\"}"})))


(def app
  "Main application with middleware stack."
  (middleware/standard-middleware-stack all-routes))
