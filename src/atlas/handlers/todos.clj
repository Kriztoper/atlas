(ns atlas.handlers.todos
  "HTTP handlers for todo operations."
  (:require [atlas.db.repositories.todos :as todos-repo]
            [atlas.db.repositories.tasks :as tasks-repo]
            [atlas.utils.response :as response]
            [atlas.utils.request :as request]
            [atlas.utils.validation :as validation]))

(defn get-all-todos
  "Fetch all todos from the database."
  [_request]
  (try
    (let [todos (todos-repo/find-all)]
      (if (empty? todos)
        (response/empty-data-response "No todos found")
        (response/success-response todos)))
    (catch Exception e
      (println "Error fetching todos:" (.getMessage e))
      (response/server-error-response "Failed to fetch todos"))))

(defn get-todos-by-task
  "Fetch all todos for a specific task."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")]
      ;; Verify task exists
      (if-not (tasks-repo/exists? task-id)
        (response/not-found-response "Task not found")
        (let [todos (todos-repo/find-by-task-id task-id)]
          (response/success-response todos))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching todos by task:" (.getMessage e))
      (response/server-error-response "Failed to fetch todos"))))

(defn get-completed-todos-by-task
  "Fetch all completed todos for a specific task."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")]
      ;; Verify task exists
      (if-not (tasks-repo/exists? task-id)
        (response/not-found-response "Task not found")
        (let [todos (todos-repo/find-completed-by-task task-id)]
          (response/success-response todos))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching completed todos:" (.getMessage e))
      (response/server-error-response "Failed to fetch completed todos"))))

(defn get-pending-todos-by-task
  "Fetch all pending todos for a specific task."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")]
      ;; Verify task exists
      (if-not (tasks-repo/exists? task-id)
        (response/not-found-response "Task not found")
        (let [todos (todos-repo/find-pending-by-task task-id)]
          (response/success-response todos))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching pending todos:" (.getMessage e))
      (response/server-error-response "Failed to fetch pending todos"))))

(defn get-todo-by-id
  "Fetch a single todo by ID."
  [req]
  (try
    (let [todo-id (request/parse-integer-param 
                    (get-in req [:route-params :todo-id]) 
                    "todo-id")]
      (if-let [todo (todos-repo/find-by-id todo-id)]
        (response/success-response todo)
        (response/not-found-response "Todo not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching todo:" (.getMessage e))
      (response/server-error-response "Failed to fetch todo"))))

(defn create-todo
  "Create a new todo."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")
          todo-data (request/parse-json-body req)]
      (if-not todo-data
        (response/validation-error-response ["Request body is required"])
        ;; Verify parent task exists
        (if-not (tasks-repo/exists? task-id)
          (response/not-found-response "Task not found")
          (let [todo-with-task (assoc todo-data :task-id task-id)
                validation-errors (validation/validate-todo todo-with-task)]
            (if-not (validation/valid? validation-errors)
              (response/validation-error-response validation-errors)
              (let [created-todo (todos-repo/create! todo-with-task)]
                (response/created-response created-todo)))))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error creating todo:" (.getMessage e))
      (response/server-error-response "Failed to create todo"))))

(defn update-todo
  "Update an existing todo."
  [req]
  (try
    (let [todo-id (request/parse-integer-param 
                    (get-in req [:route-params :todo-id]) 
                    "todo-id")
          todo-data (request/parse-json-body req)]
      (if-not todo-data
        (response/validation-error-response ["Request body is required"])
        (let [validation-errors (validation/validate-todo-update todo-data)]
          (if-not (validation/valid? validation-errors)
            (response/validation-error-response validation-errors)
            (if-let [updated-todo (todos-repo/update! todo-id todo-data)]
              (response/success-response updated-todo)
              (response/not-found-response "Todo not found"))))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error updating todo:" (.getMessage e))
      (response/server-error-response "Failed to update todo"))))

(defn toggle-todo-completion
  "Toggle the completion status of a todo."
  [req]
  (try
    (let [todo-id (request/parse-integer-param 
                    (get-in req [:route-params :todo-id]) 
                    "todo-id")]
      (if-let [updated-todo (todos-repo/toggle-completion! todo-id)]
        (response/success-response updated-todo)
        (response/not-found-response "Todo not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error toggling todo completion:" (.getMessage e))
      (response/server-error-response "Failed to toggle todo completion"))))

(defn mark-todo-completed
  "Mark a todo as completed."
  [req]
  (try
    (let [todo-id (request/parse-integer-param 
                    (get-in req [:route-params :todo-id]) 
                    "todo-id")]
      (if-let [updated-todo (todos-repo/mark-completed! todo-id)]
        (response/success-response updated-todo)
        (response/not-found-response "Todo not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error marking todo completed:" (.getMessage e))
      (response/server-error-response "Failed to mark todo completed"))))

(defn mark-todo-pending
  "Mark a todo as pending."
  [req]
  (try
    (let [todo-id (request/parse-integer-param 
                    (get-in req [:route-params :todo-id]) 
                    "todo-id")]
      (if-let [updated-todo (todos-repo/mark-pending! todo-id)]
        (response/success-response updated-todo)
        (response/not-found-response "Todo not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error marking todo pending:" (.getMessage e))
      (response/server-error-response "Failed to mark todo pending"))))

(defn delete-todo
  "Delete a todo."
  [req]
  (try
    (let [todo-id (request/parse-integer-param 
                    (get-in req [:route-params :todo-id]) 
                    "todo-id")]
      (if (todos-repo/delete! todo-id)
        (response/no-content-response)
        (response/not-found-response "Todo not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error deleting todo:" (.getMessage e))
      (response/server-error-response "Failed to delete todo"))))

(defn get-todo-stats-by-task
  "Get todo statistics for a specific task."
  [req]
  (try
    (let [task-id (request/parse-integer-param 
                    (get-in req [:route-params :task-id]) 
                    "task-id")]
      ;; Verify task exists
      (if-not (tasks-repo/exists? task-id)
        (response/not-found-response "Task not found")
        (let [stats (todos-repo/count-by-task task-id)]
          (response/success-response stats))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching todo stats:" (.getMessage e))
      (response/server-error-response "Failed to fetch todo statistics"))))
