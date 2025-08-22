(ns atlas.core
  (:gen-class)
  (:require [atlas.db.migrations :as migrations]
            [ring.adapter.jetty :as jetty]
            [atlas.routes :as routes]))

(def default-port 3001)

(defn get-port []
  (if-let [port-str (System/getenv "PORT")]
    (try
      (Integer/parseInt port-str)
      (catch NumberFormatException _
        (println (format "Invalid PORT environment variable: %s, using default: %d" port-str default-port))
        default-port))
    default-port))

(defn start-server [port]
  (println (format "🚀 Starting Atlas server on port %d..." port))
  (try
    (jetty/run-jetty routes/app
                     {:port port
                      :join? true
                      :send-server-version? false
                      :send-date-header? false})
    (catch Exception e
      (println "❌ Failed to start server:" (.getMessage e))
      (System/exit 1))))

(defn startup-banner []
  (println "")
  (println "  █████╗ ████████╗██╗      █████╗ ███████╗")
  (println " ██╔══██╗╚══██╔══╝██║     ██╔══██╗██╔════╝")
  (println " ███████║   ██║   ██║     ███████║███████╗")
  (println " ██╔══██║   ██║   ██║     ██╔══██║╚════██║")
  (println " ██║  ██║   ██║   ███████╗██║  ██║███████║")
  (println " ╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝")
  (println "")
  (println " Task Management Application")
  (println " Version: 1.0.0")
  (println ""))

(defn -main [& args]
  (startup-banner)
  
  (println "📋 Initializing Atlas application...")
  
  ;; Handle command line arguments for migration operations
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
    (let [name (or (first (drop-while #(not= "create-migration" %) args))
                   (do
                     (println "Usage: lein run create-migration <migration-name>")
                     (System/exit 1)))]
      (migrations/create-migration name)
      (System/exit 0))
    
    :else
    (do
      ;; Normal startup process
      (println "🔧 Running database migrations...")
      
      ;; Run migrations before starting the server
      (try
        (migrations/migrate)
        (println "✅ Database migrations completed successfully!")
        (catch Exception e
          (println "❌ Migration failed, cannot start server:" (.getMessage e))
          (System/exit 1)))
      
      ;; Start the HTTP server
      (let [port (get-port)]
        (println (format "🌐 Server will be available at: http://localhost:%d" port))
        (println (format "📡 API endpoints will be available at: http://localhost:%d/api" port))
        (println "💡 Press Ctrl+C to stop the server")
        (println "")
        
        (start-server port)))))
