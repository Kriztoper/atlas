(ns atlas.utils.request
  "Request handling utilities."
  (:require [cheshire.core :as json]
            [clojure.string :as str]))

(defn parse-json-body
  "Parse JSON request body safely."
  [request]
  (try
    (when-let [body (:body request)]
      (let [body-str (if (string? body) body (slurp body))]
        (when-not (str/blank? body-str)
          (json/parse-string body-str true))))
    (catch Exception e
      (throw (ex-info "Invalid JSON in request body" 
                      {:type :invalid-json 
                       :message (.getMessage e)})))))

(defn get-path-param
  "Get path parameter and parse as integer if needed."
  [request param-key]
  (get-in request [:path-params param-key]))

(defn parse-integer-param
  "Parse string parameter as integer."
  [param-str param-name]
  (when param-str
    (try
      (Integer/parseInt param-str)
      (catch NumberFormatException _
        (throw (ex-info (format "Invalid %s: must be a number" param-name)
                        {:type :invalid-parameter
                         :parameter param-name
                         :value param-str}))))))

(defn get-query-param
  "Get query parameter from request."
  [request param-key & [default]]
  (get-in request [:query-params (name param-key)] default))

(defn extract-route-params
  "Extract common route parameters (ids) from request."
  [request]
  (let [params (:route-params request)]
    (into {} 
          (map (fn [[k v]]
                 [k (when v (parse-integer-param v (name k)))])
               params))))

(defn content-type
  "Get content type from request headers."
  [request]
  (get-in request [:headers "content-type"] ""))

(defn json-request?
  "Check if request has JSON content type."
  [request]
  (str/includes? (str/lower-case (content-type request)) "application/json"))

(defn with-request-validation
  "Wrapper to add common request validation."
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch clojure.lang.ExceptionInfo e
        (let [{:keys [type message]} (ex-data e)]
          (case type
            :invalid-json {:status 400
                          :headers {"Content-Type" "application/json"}
                          :body (json/generate-string {:error "Invalid JSON" :message message})}
            :invalid-parameter {:status 400
                              :headers {"Content-Type" "application/json"}
                              :body (json/generate-string {:error "Invalid parameter" :message message})}
            (throw e))))
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error "Internal server error" :message (.getMessage e)})}))))
