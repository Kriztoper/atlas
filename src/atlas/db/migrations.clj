(ns atlas.db.migrations
  "Database migration management using Migratus."
  (:require [migratus.core :as migratus]
            [clojure.java.io :as io]
            [atlas.config.database :as db-config]))

(def ^:private migratus-config
  "Migratus configuration for database migrations."
  {:store :database
   :db (db-config/get-final-db-config)
   :migration-dir "resources/migrations"
   :migration-table-name "schema_migrations"
   :command-line-args []})

;; Check if database connection is available
(defn database-available? []
  (try
    (migratus/init migratus-config)
    true
    (catch Exception e
      (println "Database connection failed:" (.getMessage e))
      false)))

;; Initialize database and migration table if needed
(defn init-db []
  (println "Initializing database...")
  (try
    (migratus/init migratus-config)
    (println "Database initialization complete.")
    (catch Exception e
      (println "Failed to initialize database:" (.getMessage e))
      (throw e))))

;; Check for pending migrations
(defn pending-migrations []
  (try
    (migratus/pending-list migratus-config)
    (catch Exception e
      (println "Error checking for pending migrations:" (.getMessage e))
      [])))

;; Main migration function with enhanced error handling and logging
(defn migrate []
  (println "=== Starting Database Migration Process ===")
  
  ;; Check if database is available
  (when-not (database-available?)
    (println "Database is not available. Please ensure PostgreSQL is running.")
    (System/exit 1))
  
  ;; Initialize database if needed
  (init-db)
  
  ;; Check for pending migrations
  (let [pending (pending-migrations)]
    (if (empty? pending)
      (println "No pending migrations found.")
      (do
        (println (format "Found %d pending migration(s):" (count pending)))
        (doseq [migration pending]
          (println (format "  - %s" migration)))
        
        ;; Run migrations
        (println "Running database migrations...")
        (try
          (migratus/migrate migratus-config)
          (println "✅ All migrations completed successfully!")
          (catch Exception e
            (println "❌ Migration failed:" (.getMessage e))
            (throw e)))))
    
    (println "=== Migration Process Complete ===")))

;; Rollback last migration (for development/debugging)
(defn rollback []
  (println "Rolling back last migration...")
  (try
    (migratus/rollback migratus-config)
    (println "✅ Rollback completed successfully!")
    (catch Exception e
      (println "❌ Rollback failed:" (.getMessage e))
      (throw e))))

;; Create a new migration file
(defn create-migration [name]
  (println (format "Creating migration: %s" name))
  (try
    (migratus/create migratus-config name)
    (println (format "✅ Migration file created for: %s" name))
    (catch Exception e
      (println "❌ Failed to create migration:" (.getMessage e))
      (throw e))))

;; Get migration status
(defn status []
  (println "=== Migration Status ===")
  (try
    (let [completed (migratus/completed-list migratus-config)
          pending (pending-migrations)]
      (println (format "Completed migrations: %d" (count completed)))
      (println (format "Pending migrations: %d" (count pending)))
      
      (when (seq completed)
        (println "\nCompleted:")
        (doseq [migration completed]
          (println (format "  ✅ %s" migration))))
      
      (when (seq pending)
        (println "\nPending:")
        (doseq [migration pending]
          (println (format "  ⏳ %s" migration)))))
    
    (catch Exception e
      (println "❌ Failed to get migration status:" (.getMessage e))
      (throw e)))
  (println "=== End Status ==="))
