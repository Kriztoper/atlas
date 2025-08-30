(ns atlas.db.repositories.tasks
  "Task repository - data access layer for tasks."
  (:require [atlas.db.core :as db]
            [clojure.spec.alpha :as s]))

;; Task data specifications
(s/def ::id pos-int?)
(s/def ::project-id pos-int?)
(s/def ::name (s/and string? #(not (empty? %))))
(s/def ::description (s/nilable string?))
(s/def ::created-at inst?)
(s/def ::updated-at inst?)

(s/def ::task
  (s/keys :req-un [::id ::project-id ::name]
          :opt-un [::description ::created-at ::updated-at]))

(s/def ::new-task
  (s/keys :req-un [::project-id ::name]
          :opt-un [::description]))

;; Repository functions
(defn find-all
  "Find all tasks."
  []
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn 
                         "SELECT id, project_id, name, description, created_at, updated_at 
                          FROM task 
                          ORDER BY created_at DESC" 
                         []))))

(defn find-by-id
  "Find task by ID."
  [task-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/find-by-id conn :task task-id))))

(defn find-by-project-id
  "Find all tasks for a specific project."
  [project-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/find-by-keys conn :task {:project_id project-id}))))

(defn create!
  "Create a new task."
  [task-data]
  {:pre [(s/valid? ::new-task task-data)]}
  (db/with-connection
    (fn [conn]
      (db/insert! conn :task 
                  (select-keys task-data [:project_id :name :description])))))

(defn update!
  "Update an existing task."
  [task-id updates]
  (db/with-connection
    (fn [conn]
      (let [allowed-fields (select-keys updates [:name :description])]
        (when (seq allowed-fields)
          (db/update! conn :task 
                     (assoc allowed-fields :updated_at (java.time.Instant/now))
                     {:id task-id})
          (db/find-by-id conn :task task-id))))))

(defn delete!
  "Delete a task by ID."
  [task-id]
  (db/with-connection
    (fn [conn]
      (let [result (db/delete! conn :task {:id task-id})]
        (> (first result) 0)))))

(defn exists?
  "Check if task exists."
  [task-id]
  (db/with-readonly-connection
    (fn [conn]
      (boolean (db/find-by-id conn :task task-id)))))

(defn find-with-stats
  "Find all tasks with todo statistics."
  [project-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn
        "SELECT t.id, t.project_id, t.name, t.description, t.created_at, t.updated_at,
                COUNT(todo.id) as todo_count,
                COUNT(CASE WHEN todo.completed = true THEN todo.id END) as completed_todo_count
         FROM task t
         LEFT JOIN todo ON t.id = todo.task_id
         WHERE t.project_id = ?
         GROUP BY t.id, t.project_id, t.name, t.description, t.created_at, t.updated_at
         ORDER BY t.created_at DESC"
        [project-id]))))
