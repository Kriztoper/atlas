(ns atlas.handlers.tasks
  "HTTP handlers for task operations."
  (:require [atlas.db.repositories.tasks :as tasks-repo]
            [atlas.db.repositories.projects :as projects-repo]
            [atlas.utils.response :as response]
            [atlas.utils.request :as request]
            [atlas.utils.validation :as validation]))

(defn get-all-tasks
  "Fetch all tasks from the database."
  [_request]
  (try
    (let [tasks (tasks-repo/find-all)]
      (if (empty? tasks)
        (response/empty-data-response "No tasks found")
        (response/success-response tasks)))
    (catch Exception e
      (println "Error fetching tasks:" (.getMessage e))
      (response/server-error-response "Failed to fetch tasks"))))

(defn get-tasks-by-project
  "Fetch all tasks for a specific project."
  [req]
  (try
    (let [project-id (request/parse-integer-param 
                       (get-in req [:route-params :project-id]) 
                       "project-id")]
      ;; Verify project exists
      (if-not (projects-repo/exists? project-id)
        (response/not-found-response "Project not found")
        (let [tasks (tasks-repo/find-by-project-id project-id)]
          (response/success-response tasks))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching tasks by project:" (.getMessage e))
      (response/server-error-response "Failed to fetch tasks"))))

(defn get-tasks-by-project-with-stats
  "Fetch all tasks for a specific project with todo statistics."
  [req]
  (try
    (let [project-id (request/parse-integer-param 
                       (get-in req [:route-params :project-id]) 
                       "project-id")]
      ;; Verify project exists
      (if-not (projects-repo/exists? project-id)
        (response/not-found-response "Project not found")
        (let [tasks (tasks-repo/find-with-stats project-id)]
          (response/success-response tasks))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching tasks with stats:" (.getMessage e))
      (response/server-error-response "Failed to fetch tasks"))))

(defn get-task-by-id
  "Fetch a single task by ID."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")]
      (if-let [task (tasks-repo/find-by-id task-id)]
        (response/success-response task)
        (response/not-found-response "Task not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching task:" (.getMessage e))
      (response/server-error-response "Failed to fetch task"))))

(defn create-task
  "Create a new task."
  [req]
  (try
    (let [project-id (request/parse-integer-param 
                       (get-in req [:route-params :project-id]) 
                       "project-id")
          task-data (request/parse-json-body req)]
      (if-not task-data
        (response/validation-error-response ["Request body is required"])
        ;; Verify parent project exists
        (if-not (projects-repo/exists? project-id)
          (response/not-found-response "Project not found")
          (let [task-with-project (assoc task-data :project-id project-id)
                validation-errors (validation/validate-task task-with-project)]
            (if-not (validation/valid? validation-errors)
              (response/validation-error-response validation-errors)
              (let [created-task (tasks-repo/create! task-with-project)]
                (response/created-response created-task)))))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error creating task:" (.getMessage e))
      (response/server-error-response "Failed to create task"))))

(defn update-task
  "Update an existing task."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")
          task-data (request/parse-json-body req)]
      (if-not task-data
        (response/validation-error-response ["Request body is required"])
        (let [validation-errors (validation/validate-task-update task-data)]
          (if-not (validation/valid? validation-errors)
            (response/validation-error-response validation-errors)
            (if-let [updated-task (tasks-repo/update! task-id task-data)]
              (response/success-response updated-task)
              (response/not-found-response "Task not found"))))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error updating task:" (.getMessage e))
      (response/server-error-response "Failed to update task"))))

(defn delete-task
  "Delete a task."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")]
      (if (tasks-repo/delete! task-id)
        (response/no-content-response)
        (response/not-found-response "Task not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error deleting task:" (.getMessage e))
      (response/server-error-response "Failed to delete task"))))
