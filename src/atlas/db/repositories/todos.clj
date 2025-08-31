(ns atlas.db.repositories.todos
  "Todo repository - data access layer for todos."
  (:require [atlas.db.core :as db]
            [clojure.spec.alpha :as s]))

;; Todo data specifications
(s/def ::id pos-int?)
(s/def ::task-id pos-int?)
(s/def ::text (s/and string? #(not (empty? %))))
(s/def ::description (s/nilable string?))
(s/def ::is_completed boolean?)
(s/def ::created-at inst?)
(s/def ::updated-at inst?)

(s/def ::todo
  (s/keys :req-un [::id ::task-id ::text]
          :opt-un [::description ::is_completed ::created-at ::updated-at]))

(s/def ::new-todo
  (s/keys :req-un [::task-id ::text]
          :opt-un [::description ::is_completed]))

;; Repository functions
(defn find-all
  "Find all todos."
  []
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn
                         "SELECT id, task_id, text, description, is_completed, created_at, updated_at
                          FROM todo
                          ORDER BY created_at DESC"
                         []))))

(defn find-by-id
  "Find todo by ID."
  [todo-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/find-by-id conn :todo todo-id))))

(defn find-by-task-id
  "Find all todos for a specific task."
  [task-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/find-by-keys conn :todo {:task_id task-id}))))

(defn create!
  "Create a new todo."
  [todo-data]
  {:pre [(s/valid? ::new-todo todo-data)]}
  (db/with-connection
    (fn [conn]
      (let [todo-record (merge {:is_completed false}
                               (select-keys todo-data [:task_id :text :description :is_completed]))]
        (db/insert! conn :todo todo-record)))))

(defn update!
  "Update an existing todo."
  [todo-id updates]
  (db/with-connection
    (fn [conn]
      (let [allowed-fields (select-keys updates [:text :description :is_completed])]
        (when (seq allowed-fields)
          (db/update! conn :todo
                     (assoc allowed-fields :updated_at (java.time.Instant/now))
                     {:id todo-id})
          (db/find-by-id conn :todo todo-id))))))

(defn toggle-completion!
  "Toggle the completion status of a todo."
  [todo-id]
  (db/with-connection
    (fn [conn]
      (let [todo (db/find-by-id conn :todo todo-id)]
        (when todo
          (db/update! conn :todo
                     {:is_completed (not (:is_completed todo))
                      :updated_at (java.time.Instant/now)}
                     {:id todo-id})
          (db/find-by-id conn :todo todo-id))))))

(defn mark-completed!
  "Mark a todo as completed."
  [todo-id]
  (update! todo-id {:is_completed true}))

(defn mark-pending!
  "Mark a todo as pending (not completed)."
  [todo-id]
  (update! todo-id {:is_completed false}))

(defn delete!
  "Delete a todo by ID."
  [todo-id]
  (db/with-connection
    (fn [conn]
      (let [result (db/delete! conn :todo {:id todo-id})]
        (> (first result) 0)))))

(defn exists?
  "Check if todo exists."
  [todo-id]
  (db/with-readonly-connection
    (fn [conn]
      (boolean (db/find-by-id conn :todo todo-id)))))

(defn count-by-task
  "Count todos by task ID with completion status."
  [task-id]
  (db/with-readonly-connection
    (fn [conn]
      (first (db/execute-query! conn
               "SELECT
                  COUNT(*) as total_count,
                  COUNT(CASE WHEN is_completed = true THEN 1 END) as completed_count,
                  COUNT(CASE WHEN is_completed = false THEN 1 END) as pending_count
                FROM todo
                WHERE task_id = ?"
               [task-id])))))

(defn find-completed-by-task
  "Find all completed todos for a specific task."
  [task-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn
                         "SELECT id, task_id, text, description, is_completed, created_at, updated_at
                          FROM todo
                          WHERE task_id = ? AND is_completed = true
                          ORDER BY updated_at DESC"
                         [task-id]))))

(defn find-pending-by-task
  "Find all pending todos for a specific task."
  [task-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn
                         "SELECT id, task_id, text, description, is_completed, created_at, updated_at
                          FROM todo
                          WHERE task_id = ? AND is_completed = false
                          ORDER BY created_at DESC"
                         [task-id]))))
