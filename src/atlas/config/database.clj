(ns atlas.config.database
  "Database configuration and connection management."
  (:require [atlas.config.server :as server-config]))

(def ^:private default-db-config
  {:dbtype "postgresql"
   :host "localhost"
   :port 5432
   :dbname "atlas_dev"
   :user "admin"
   :password ""})

(defn get-db-config
  "Get database configuration from environment variables or defaults."
  []
  (merge default-db-config
         (when-let [host (System/getenv "DB_HOST")]
           {:host host})
         (when-let [port (System/getenv "DB_PORT")]
           {:port (Integer/parseInt port)})
         (when-let [dbname (System/getenv "DB_NAME")]
           {:dbname dbname})
         (when-let [user (System/getenv "DB_USER")]
           {:user user})
         (when-let [password (System/getenv "DB_PASSWORD")]
           {:password password})))

(defn get-jdbc-url
  "Build JDBC URL from database configuration."
  [db-config]
  (format "jdbc:postgresql://%s:%d/%s"
          (:host db-config)
          (:port db-config)
          (:dbname db-config)))

(defn get-legacy-db-spec
  "Get database specification for legacy JDBC usage."
  []
  (let [config (get-db-config)]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (format "//%s:%d/%s" (:host config) (:port config) (:dbname config))
     :user (:user config)
     :password (:password config)}))

(defn get-connection-pool-config
  "Get connection pool configuration for production use."
  []
  (merge (get-db-config)
         {:maximum-pool-size 10
          :minimum-pool-size 2
          :connection-timeout 30000
          :idle-timeout 600000
          :max-lifetime 1800000}))

(defn database-url-from-env
  "Get database URL from DATABASE_URL environment variable (Heroku style)."
  []
  (System/getenv "DATABASE_URL"))

(defn parse-database-url
  "Parse DATABASE_URL into database configuration map."
  [url]
  (when url
    (let [uri (java.net.URI. url)
          user-info (.getUserInfo uri)
          [user password] (when user-info (clojure.string/split user-info #":"))]
      {:dbtype "postgresql"
       :host (.getHost uri)
       :port (or (.getPort uri) 5432)
       :dbname (subs (.getPath uri) 1) ; Remove leading slash
       :user user
       :password password})))

(defn get-final-db-config
  "Get final database configuration, preferring DATABASE_URL if available."
  []
  (if-let [db-url (database-url-from-env)]
    (parse-database-url db-url)
    (get-db-config)))

(defn log-db-config
  "Log database configuration (without sensitive data)."
  [config]
  (when (server-config/development?)
    (println "📊 Database Configuration:")
    (println (format "   Host: %s" (:host config)))
    (println (format "   Port: %d" (:port config)))
    (println (format "   Database: %s" (:dbname config)))
    (println (format "   User: %s" (:user config)))
    (println "")))
