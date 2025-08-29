(ns atlas.config.core
  "Centralized configuration management for Atlas application."
  (:require [atlas.config.server :as server-config]
            [atlas.config.database :as db-config]
            [atlas.utils.helpers :as helpers]
            [clojure.spec.alpha :as s]))

;; Configuration specifications
(s/def ::environment #{:development :test :production})
(s/def ::port (s/int-in 1000 65536))
(s/def ::host string?)

(s/def ::server-config
  (s/keys :req-un [::port ::host]
          :opt-un [::environment]))

(defn get-application-config
  "Get complete application configuration."
  []
  (let [env (server-config/get-environment)
        server-cfg (server-config/get-server-config)
        db-cfg (db-config/get-final-db-config)]
    {:environment env
     :server server-cfg
     :database db-cfg
     :version "1.0.0"
     :features {:cors-enabled true
                :health-checks true
                :request-logging (server-config/development?)
                :cache-enabled true}}))

(defn validate-config
  "Validate application configuration."
  [config]
  (let [server-valid? (s/valid? ::server-config (:server config))
        env-valid? (s/valid? ::environment (:environment config))]
    {:valid? (and server-valid? env-valid?)
     :errors (cond-> []
               (not server-valid?) (conj "Invalid server configuration")
               (not env-valid?) (conj "Invalid environment configuration"))}))

(defn log-startup-config
  "Log startup configuration (without sensitive data)."
  [config]
  (println "🔧 Application Configuration:")
  (println (format "   Environment: %s" (name (:environment config))))
  (println (format "   Version: %s" (:version config)))
  (println (format "   Server: %s:%d" 
                   (get-in config [:server :host])
                   (get-in config [:server :port])))
  (println (format "   Database: %s:%d/%s"
                   (get-in config [:database :host])
                   (get-in config [:database :port])
                   (get-in config [:database :dbname])))
  (println (format "   Features: %s" (pr-str (:features config))))
  (println ""))

(defn get-feature-flag
  "Check if a feature is enabled."
  [feature-key]
  (get-in (get-application-config) [:features feature-key] false))

(defn production-ready?
  "Check if configuration is ready for production."
  []
  (let [config (get-application-config)
        validation (validate-config config)]
    (and (:valid? validation)
         (not= (:environment config) :development)
         (get-in config [:database :password]))))  ; Ensure DB password is set

;; Environment-specific configurations
(def development-overrides
  {:features {:cors-enabled true
              :request-logging true
              :health-checks true
              :debug-mode true
              :cache-enabled false}})

(def production-overrides
  {:features {:cors-enabled false  ; Should be configured specifically
              :request-logging false
              :health-checks true
              :debug-mode false
              :cache-enabled true}})

(def test-overrides
  {:features {:cors-enabled true
              :request-logging false
              :health-checks false
              :debug-mode true
              :cache-enabled false}})

(defn apply-environment-overrides
  "Apply environment-specific configuration overrides."
  [base-config]
  (let [env (:environment base-config)
        overrides (case env
                    :development development-overrides
                    :production production-overrides
                    :test test-overrides
                    {})]
    (helpers/deep-merge base-config overrides)))

(defn get-final-config
  "Get final configuration with environment overrides applied."
  []
  (-> (get-application-config)
      apply-environment-overrides))

;; Configuration loading and initialization
(def ^:private config-cache (atom nil))

(defn load-config!
  "Load and cache configuration."
  []
  (let [config (get-final-config)
        validation (validate-config config)]
    (when-not (:valid? validation)
      (throw (ex-info "Invalid configuration" 
                      {:errors (:errors validation)
                       :config config})))
    (reset! config-cache config)
    config))

(defn get-config
  "Get cached configuration, loading if necessary."
  []
  (or @config-cache (load-config!)))

(defn reload-config!
  "Reload configuration from environment."
  []
  (reset! config-cache nil)
  (load-config!))

;; Hot reload support for development
(defn watch-config-changes!
  "Watch for configuration changes in development."
  []
  (when (server-config/development?)
    (println "👀 Watching for configuration changes...")
    ;; In a real implementation, you might watch environment files
    ;; For now, just provide the reload function
    (defn reload! [] (reload-config!))))
