(ns atlas.config.server
  "Server configuration and environment management.")

(def ^:private default-config
  {:port 3001
   :host "localhost"
   :send-server-version? false
   :send-date-header? false
   :join? true})

(defn get-port
  "Get server port from environment or use default."
  []
  (if-let [port-str (System/getenv "PORT")]
    (try
      (Integer/parseInt port-str)
      (catch NumberFormatException _
        (println (format "Invalid PORT environment variable: %s, using default: %d" 
                        port-str (:port default-config)))
        (:port default-config)))
    (:port default-config)))

(defn get-host
  "Get server host from environment or use default."
  []
  (or (System/getenv "HOST") (:host default-config)))

(defn get-server-config
  "Get complete server configuration."
  []
  (assoc default-config
         :port (get-port)
         :host (get-host)))

(defn get-environment
  "Get current environment (development, test, production)."
  []
  (keyword (or (System/getenv "ATLAS_ENV") 
               (System/getenv "NODE_ENV") 
               "development")))

(defn development?
  "Check if running in development mode."
  []
  (= (get-environment) :development))

(defn production?
  "Check if running in production mode."
  []
  (= (get-environment) :production))
