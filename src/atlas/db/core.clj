(ns atlas.db.core
  "Database core functionality - connection management and utilities."
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [atlas.config.database :as db-config]
            [atlas.config.server :as server-config]))

;; Connection management
(def ^:private db-spec (atom nil))

(defn get-datasource
  "Get or create database datasource."
  []
  (when-not @db-spec
    (let [config (db-config/get-final-db-config)]
      (db-config/log-db-config config)
      (reset! db-spec (jdbc/get-datasource config))))
  @db-spec)

(defn with-connection
  "Execute function with a database connection."
  [f]
  (jdbc/with-transaction [tx (get-datasource)]
    (f tx)))

(defn with-readonly-connection
  "Execute function with a read-only database connection."
  [f]
  (let [conn (jdbc/get-connection (get-datasource))]
    (try
      (.setReadOnly conn true)
      (f conn)
      (finally
        (.close conn)))))

;; Query execution helpers
(defn execute-query!
  "Execute a query with parameters."
  [conn query params]
  (sql/query conn (cons query params)
             {:builder-fn rs/as-unqualified-maps}))

(defn execute-update!
  "Execute an update/insert/delete with parameters."
  [conn query params]
  (sql/query conn (cons query params)))

(defn find-by-id
  "Find a record by ID."
  [conn table id]
  (first (sql/find-by-keys conn table {:id id}
                          {:builder-fn rs/as-unqualified-maps})))

(defn find-by-keys
  "Find records by key-value pairs."
  [conn table key-map]
  (sql/find-by-keys conn table key-map
                    {:builder-fn rs/as-unqualified-maps}))

(defn insert!
  "Insert a record and return the created record."
  [conn table data]
  (first (sql/insert! conn table data
                     {:return-keys true
                      :builder-fn rs/as-unqualified-maps})))

(defn update!
  "Update records by key-value pairs."
  [conn table data where-clause]
  (sql/update! conn table data where-clause))

(defn delete!
  "Delete records by key-value pairs."
  [conn table where-clause]
  (sql/delete! conn table where-clause))

;; Health check
(defn health-check
  "Check database connection health."
  []
  (try
    (with-readonly-connection
      (fn [conn]
        (sql/query conn ["SELECT 1 as health_check"]
                  {:builder-fn rs/as-unqualified-maps})))
    {:status :healthy :timestamp (java.time.Instant/now)}
    (catch Exception e
      {:status :unhealthy 
       :error (.getMessage e) 
       :timestamp (java.time.Instant/now)})))

;; Development utilities
(defn reset-connection!
  "Reset the database connection (useful for development)."
  []
  (reset! db-spec nil)
  (get-datasource))
