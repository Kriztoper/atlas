(ns atlas.cli
  "Command line interface for Atlas application."
  (:require [atlas.db.migrations :as migrations]))

(def command-help
  "Available commands and their descriptions."
  {"migrate"           "Run all pending database migrations"
   "rollback"          "Rollback the last database migration"
   "migration-status"  "Show database migration status"
   "create-migration"  "Create a new migration file (requires name argument)"
   "help"             "Show this help message"})

(defn print-help
  "Print available commands and usage information."
  []
  (println "\nAtlas - Task Management Application CLI")
  (println "Usage: lein run [command] [args...]")
  (println "\nAvailable commands:")
  (doseq [[cmd desc] command-help]
    (println (format "  %-18s %s" cmd desc)))
  (println "\nExamples:")
  (println "  lein run migrate")
  (println "  lein run create-migration add-user-table")
  (println "  lein run migration-status")
  (println))

(defn handle-migration-command
  "Handle migration-related commands."
  [command args]
  (case command
    "migrate"
    (do
      (println "Running database migrations...")
      (migrations/migrate))
    
    "rollback"
    (do
      (println "Rolling back last migration...")
      (migrations/rollback))
    
    "migration-status"
    (migrations/status)
    
    "create-migration"
    (if-let [name (first args)]
      (migrations/create-migration name)
      (do
        (println "Error: Migration name is required")
        (println "Usage: lein run create-migration <migration-name>")
        (System/exit 1)))
    
    (do
      (println (format "Unknown migration command: %s" command))
      (System/exit 1))))

(defn parse-args
  "Parse command line arguments and return command and remaining args."
  [args]
  (if (empty? args)
    {:command "start" :args []}
    {:command (first args) :args (rest args)}))

(defn handle-command
  "Handle the given command with arguments."
  [command args]
  (case command
    "help"
    (print-help)
    
    ("migrate" "rollback" "migration-status" "create-migration")
    (handle-migration-command command args)
    
    "start"
    :start-server  ; Return signal to start server
    
    (do
      (println (format "Unknown command: %s" command))
      (print-help)
      (System/exit 1))))

(defn process-args
  "Process command line arguments and return the action to take."
  [args]
  (let [{:keys [command args]} (parse-args args)]
    (handle-command command args)))
