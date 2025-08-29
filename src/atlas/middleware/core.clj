(ns atlas.middleware.core
  "Core middleware utilities for the Atlas API."
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [atlas.utils.response :as response]
            [atlas.config.server :as server-config]))

(defn wrap-json-response
  "Middleware to ensure responses have proper JSON headers."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (if (and response (not (get-in response [:headers "Content-Type"])))
        (assoc-in response [:headers "Content-Type"] "application/json")
        response))))

(defn wrap-cors
  "CORS middleware with configurable origins."
  ([handler]
   (wrap-cors handler {}))
  ([handler options]
   (let [default-options {:access-control-allow-origin "*"
                         :access-control-allow-methods "GET, POST, PUT, PATCH, DELETE, OPTIONS"
                         :access-control-allow-headers "Content-Type, Authorization, X-Requested-With"
                         :access-control-max-age "3600"}
         cors-options (merge default-options options)]
     (fn [request]
       (if (= :options (:request-method request))
         ;; Handle preflight OPTIONS request
         {:status 200
          :headers (zipmap (map #(str/join "-" (map str/capitalize (str/split (name %) #"-")))
                               (keys cors-options))
                          (vals cors-options))
          :body ""}
         ;; Add CORS headers to actual response
         (let [response (handler request)]
           (if response
             (update response :headers merge
                     (zipmap (map #(str/join "-" (map str/capitalize (str/split (name %) #"-")))
                                 (keys cors-options))
                            (vals cors-options)))
             response)))))))

(defn wrap-exception-handling
  "Middleware to catch and handle exceptions gracefully."
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch clojure.lang.ExceptionInfo e
        (let [{:keys [type status] :or {status 400}} (ex-data e)]
          (case type
            :validation-error (response/validation-error-response [(.getMessage e)])
            :not-found (response/not-found-response (.getMessage e))
            :unauthorized (response/error-response (.getMessage e) 401)
            :forbidden (response/error-response (.getMessage e) 403)
            (response/error-response (.getMessage e) status))))
      (catch java.sql.SQLException e
        (println "Database error:" (.getMessage e))
        (if (server-config/development?)
          (response/server-error-response (str "Database error: " (.getMessage e)))
          (response/server-error-response "Database error occurred")))
      (catch Exception e
        (println "Unexpected error:" (.getMessage e))
        (.printStackTrace e)
        (if (server-config/development?)
          (response/server-error-response (str "Unexpected error: " (.getMessage e)))
          (response/server-error-response "An unexpected error occurred"))))))

(defn wrap-request-logging
  "Middleware to log incoming requests."
  [handler]
  (fn [request]
    (when (server-config/development?)
      (println (format "%s %s" 
                      (str/upper-case (name (:request-method request)))
                      (:uri request))))
    (handler request)))

(defn wrap-response-time
  "Middleware to add response time header."
  [handler]
  (fn [request]
    (let [start-time (System/nanoTime)
          response (handler request)
          response-time-ms (/ (- (System/nanoTime) start-time) 1000000.0)]
      (when (server-config/development?)
        (println (format "Response time: %.2fms" response-time-ms)))
      (if response
        (assoc-in response [:headers "X-Response-Time"] (str response-time-ms "ms"))
        response))))

(defn wrap-api-version
  "Middleware to add API version header."
  [handler & [version]]
  (let [api-version (or version "1.0")]
    (fn [request]
      (let [response (handler request)]
        (if response
          (assoc-in response [:headers "X-API-Version"] api-version)
          response)))))

(defn wrap-health-check
  "Middleware to handle health check requests."
  [handler]
  (fn [request]
    (if (and (= :get (:request-method request))
             (= "/health" (:uri request)))
      (response/success-response {:status "healthy" 
                                 :timestamp (java.time.Instant/now)
                                 :version "1.0.0"})
      (handler request))))

(defn wrap-authentication
  "Basic authentication middleware (placeholder)."
  [handler & [auth-config]]
  (fn [request]
    ;; For now, just pass through - can be extended for actual auth
    (handler request)))

(defn standard-middleware-stack
  "Apply the standard middleware stack to a handler."
  [handler]
  (-> handler
      wrap-exception-handling
      wrap-json-response
      (wrap-cors)
      wrap-request-logging
      wrap-response-time
      (wrap-api-version "1.0")
      wrap-health-check))
