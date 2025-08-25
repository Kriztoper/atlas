(ns atlas.routes
  (:require [compojure.core :refer [defroutes GET POST PUT PATCH DELETE OPTIONS]]
            [compojure.route :as route]
            [atlas.handlers :as handlers]
            [ring.middleware.cors :refer [wrap-cors]]))

(defroutes app-routes
  (GET "/" [] handlers/home)

  ;; Project routes
  (POST "/api/projects" [] handlers/create-project)
  (GET "/api/projects" [] handlers/all-projects)

  ;; Task routes
  (POST "/api/projects/:project-id/tasks" [] handlers/create-task)
  (GET "/api/projects/:project-id/tasks" [project-id] (handlers/get-tasks-by-project project-id))

  ;; To do routes
  (POST "/api/tasks/:task-id/todos" [] handlers/create-todo)
  (GET "/api/tasks/:task-id/todos" [task-id] (handlers/get-todos-by-task task-id))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin #".*"
                 :access-control-allow-methods [:get :post :put :patch :delete :options]
                 :access-control-allow-headers ["Content-Type"])))
