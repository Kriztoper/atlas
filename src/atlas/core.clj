(ns atlas.core
  "Main application entry point and CLI command handling."
  (:gen-class)
  (:require [atlas.db.migrations :as migrations]
            [atlas.server :as server]
            [atlas.config.server :as config]))


(defn handle-migration-commands
  "Handle CLI commands for database migrations."
  [args]
  (cond
    (some #{"migrate"} args)
    (do
      (println "Running migrations only...")
      (migrations/migrate)
      (System/exit 0))
    
    (some #{"rollback"} args)
    (do
      (println "Rolling back last migration...")
      (migrations/rollback)
      (System/exit 0))
    
    (some #{"status"} args)
    (do
      (migrations/status)
      (System/exit 0))
    
    (some #{"create-migration"} args)
    (let [migration-name (second (drop-while #(not= "create-migration" %) args))]
      (if migration-name
        (do
          (migrations/create-migration migration-name)
          (System/exit 0))
        (do
          (println "Usage: lein run create-migration <migration-name>")
          (System/exit 1))))
    
    :else false))

(defn initialize-database
  "Initialize database with migrations."
  []
  (println "🔧 Running database migrations...")
  (try
    (migrations/migrate)
    (println "✅ Database migrations completed successfully!")
    (catch Exception e
      (println "❌ Migration failed, cannot start server:" (.getMessage e))
      (throw e))))

(defn start-application
  "Start the main application server."
  []
  (try
    (initialize-database)
    (let [server-config (config/get-server-config)
          port (:port server-config)]
      (server/log-server-info port)
      (server/start-server server-config))
    (catch Exception e
      (println "❌ Failed to start Atlas application:" (.getMessage e))
      (System/exit 1))))

(defn -main
  "Application entry point."
  [& args]
  (server/startup-banner)
  (println "📋 Initializing Atlas application...")
  
  ;; Handle migration commands first
  (when-not (handle-migration-commands args)
    ;; If no migration command, start the application
    (start-application)))
