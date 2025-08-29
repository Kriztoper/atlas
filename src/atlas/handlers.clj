(ns atlas.handlers
  "Legacy handlers - DEPRECATED: Use specific handler modules instead."
  (:require [atlas.handlers.projects :as projects]
            [atlas.handlers.tasks :as tasks]
            [atlas.handlers.todos :as todos]
            [atlas.utils.response :as response]))

;; Home/Health endpoint
(defn home
  "Simple home endpoint."
  [request]
  (response/success-response {:message "Atlas API is running"
                              :version "1.0.0"
                              :timestamp (java.time.Instant/now)}))

;; Legacy handler delegations (DEPRECATED - use modular handlers directly)
(defn all-projects
  "DEPRECATED: Use atlas.handlers.projects/get-all-projects"
  [request]
  (projects/get-all-projects request))

(defn create-project
  "DEPRECATED: Use atlas.handlers.projects/create-project"
  [request]
  (projects/create-project request))

(defn create-task
  "DEPRECATED: Use atlas.handlers.tasks/create-task"
  [request]
  (tasks/create-task request))

(defn get-tasks-by-project
  "DEPRECATED: Use atlas.handlers.tasks/get-tasks-by-project"
  [project-id]
  ;; Convert old signature to new format
  (let [mock-request {:route-params {:project-id project-id}}]
    (tasks/get-tasks-by-project mock-request)))

(defn create-todo
  "DEPRECATED: Use atlas.handlers.todos/create-todo"
  [request]
  (todos/create-todo request))

(defn get-todos-by-task
  "DEPRECATED: Use atlas.handlers.todos/get-todos-by-task"
  [task-id]
  ;; Convert old signature to new format
  (let [mock-request {:route-params {:task-id task-id}}]
    (todos/get-todos-by-task mock-request)))
