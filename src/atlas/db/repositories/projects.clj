(ns atlas.db.repositories.projects
  "Project repository - data access layer for projects."
  (:require [atlas.db.core :as db]
            [clojure.spec.alpha :as s]))

;; Project data specifications
(s/def ::id pos-int?)
(s/def ::name (s/and string? #(not (empty? %))))
(s/def ::description (s/nilable string?))
(s/def ::created-at inst?)
(s/def ::updated-at inst?)

(s/def ::project
  (s/keys :req-un [::id ::name]
          :opt-un [::description ::created-at ::updated-at]))

(s/def ::new-project
  (s/keys :req-un [::name]
          :opt-un [::description]))

;; Repository functions
(defn find-all
  "Find all projects."
  []
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn 
                         "SELECT id, name, description, created_at, updated_at 
                          FROM project 
                          ORDER BY created_at DESC" 
                         []))))

(defn find-by-id
  "Find project by ID."
  [project-id]
  (db/with-readonly-connection
    (fn [conn]
      (db/find-by-id conn :project project-id))))

(defn create!
  "Create a new project."
  [project-data]
  {:pre [(s/valid? ::new-project project-data)]}
  (db/with-connection
    (fn [conn]
      (db/insert! conn :project
                  (select-keys project-data [:name :description])))))

(defn update!
  "Update an existing project."
  [project-id updates]
  (db/with-connection
    (fn [conn]
      (let [allowed-fields (select-keys updates [:name :description])]
        (when (seq allowed-fields)
          (db/update! conn :project 
                     (assoc allowed-fields :updated_at (java.time.Instant/now))
                     {:id project-id})
          (db/find-by-id conn :project project-id))))))

(defn delete!
  "Delete a project by ID."
  [project-id]
  (db/with-connection
    (fn [conn]
      (let [result (db/delete! conn :project {:id project-id})]
        (> (first result) 0)))))

(defn exists?
  "Check if project exists."
  [project-id]
  (db/with-readonly-connection
    (fn [conn]
      (boolean (db/find-by-id conn :project project-id)))))

(defn find-with-stats
  "Find all projects with task/todo statistics."
  []
  (db/with-readonly-connection
    (fn [conn]
      (db/execute-query! conn
        "SELECT p.id, p.name, p.description, p.created_at, p.updated_at,
                COUNT(DISTINCT t.id) as task_count,
                COUNT(DISTINCT todo.id) as todo_count,
                COUNT(DISTINCT CASE WHEN todo.is_completed = true THEN todo.id END) as completed_todo_count
         FROM project p
         LEFT JOIN task t ON p.id = t.project_id
         LEFT JOIN todo ON t.id = todo.task_id
         GROUP BY p.id, p.name, p.description, p.created_at, p.updated_at
         ORDER BY p.created_at DESC"
        []))))
