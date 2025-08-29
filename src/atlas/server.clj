(ns atlas.server
  "HTTP server management and lifecycle."
  (:require [ring.adapter.jetty :as jetty]
            [atlas.config.server :as config]
            [atlas.routes :as routes]))

(defn startup-banner
  "Display application startup banner."
  []
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
  (println " Environment:" (name (config/get-environment)))
  (println ""))

(defn start-server
  "Start the HTTP server with the given configuration."
  ([]
   (start-server (config/get-server-config)))
  ([server-config]
   (let [port (:port server-config)]
     (println (format "🚀 Starting Atlas server on port %d..." port))
     (try
       (jetty/run-jetty routes/app server-config)
       (catch Exception e
         (println "❌ Failed to start server:" (.getMessage e))
         (throw e))))))

(defn log-server-info
  "Log server information after successful startup."
  [port]
  (println (format "🌐 Server available at: http://localhost:%d" port))
  (println (format "📡 API endpoints available at: http://localhost:%d/api" port))
  (println "💡 Press Ctrl+C to stop the server")
  (println ""))

(defn graceful-shutdown
  "Handle graceful server shutdown."
  []
  (println "\n🛑 Shutting down Atlas server...")
  (println "👋 Goodbye!"))
