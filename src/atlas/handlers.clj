(ns atlas.handlers
  (:require [cheshire.core :as json]
            ;[ring.middleware.json :refer [wrap-json-response]]
            [next.jdbc.sql :as sql]
            [clojure.java.jdbc :as jdbc]
            [atlas.configuration.database-config :as db]))

(defn home
  [_]
  "Hello world")

(defn all-projects "Fetches all projects"
  [_]
  (jdbc/with-db-connection [conn db/pg-db]
    (let [projects (jdbc/query conn ["SELECT * FROM project"])]
      (if (empty? projects)
        {:status 200 
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:message "No projects found" :data []})}
        {:status 200 
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string projects)}))))

(defn create-project "Inserts new project to DB"
  [request]
  (let [project-data (json/parse-string (slurp (:body request)) true)]
    (try 
      (jdbc/with-db-connection [conn db/pg-db]
        (let [inserted-project (jdbc/insert! conn :project {:name (:name project-data)
                                                                :description (:description project-data)})]
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string (first inserted-project))}))
      (catch Exception e
        (println "Error creating project:" e)
        {:status 400
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:message "Saving project failed"})}))))

(defn create-task "Inserts new task to DB where it is required to have a parent"
  [request]
  (let [task-data (json/parse-string (slurp (:body request)) true)]
    (try
      (jdbc/with-db-connection [conn db/pg-db]
        (let [inserted-task (jdbc/insert! conn :task {:project_id (:projectId task-data)
                                                      :name (:name task-data)
                                                      :description (:description task-data)})]
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string (first inserted-task))}))
      (catch Exception e
        (println "Error creating task:" e)
        {:status 400
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:message "Saving task failed"})}))))

(defn get-tasks-by-project "Fetches tasks under the project"
  [project-id]
  (jdbc/with-db-connection [conn db/pg-spec]
    (try
      (let [tasks-by-project (sql/find-by-keys conn :task {:project_id (Integer/parseInt project-id)}
                                               {:builder-fn next.jdbc.result-set/as-unqualified-maps})]
        (if (empty? tasks-by-project)
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string [])}
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string tasks-by-project)}))
      (catch Exception e
        (println "Error fetching tasks: " e)))
    ))

(defn create-todo "Inserts new todo to DB where it is required to have a parent task"
  [request]
  (let [todo-data (json/parse-string (slurp (:body request)) true)]
    ((try
       (jdbc/with-db-connection [conn db/pg-db]
         (let [inserted-todo (jdbc/insert! conn :todo {:task_id (:taskId todo-data)
                                                       :text (:text todo-data)
                                                       :description (:description todo-data)})]
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string (first inserted-todo))}))
       (catch Exception e
         (println "Error creating todo: " e)
         {:status 400
          :headers {"Content-Type" "application/json"}
          :body {json/generate-string {:message "Saving todo failed"}}}
         )))))

(defn get-todos-by-task "Fetches todos under the task"
  [task-id]
  (jdbc/with-db-connection [conn db/pg-spec]
    (try
      (let [todos-by-task (sql/find-by-keys conn :todo {:task_id (Integer/parseInt task-id)}
                                            {:builder-fn next.jdbc.result-set/as-unqualified-maps})]
        (if (empty? todos-by-task)
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string [])}
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string todos-by-task)}))
      (catch Exception e
        (println "Error fetching todos: " e)))
    ))