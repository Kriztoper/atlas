(ns atlas.utils.response
  "Utilities for creating consistent HTTP responses."
  (:require [cheshire.core :as json]
            [clojure.string :as str]))

(defn- snake-kw->camel-str
  "Converts a snake_case keyword to a camelCase string."
  [kw]
  (let [s (name kw)
        parts (str/split s #"_")]
    (apply str (first parts) (map str/capitalize (rest parts)))))

(def ^:private default-headers
  {"Content-Type" "application/json"
   "Cache-Control" "no-cache"})

(defn success-response
  "Create a successful HTTP response with data."
  ([data]
   (success-response data 200))
  ([data status]
   (success-response data status {}))
  ([data status extra-headers]
   {:status status
    :headers (merge default-headers extra-headers)
    :body (json/generate-string data {:key-fn snake-kw->camel-str})}))

(defn error-response
  "Create an error HTTP response."
  ([message]
   (error-response message 400))
  ([message status]
   (error-response message status {}))
  ([message status extra-data]
   {:status status
    :headers default-headers
    :body (json/generate-string
           (merge {:error true
                   :message message}
                  extra-data)
           {:key-fn snake-kw->camel-str})}))

(defn not-found-response
  "Create a 404 not found response."
  ([]
   (not-found-response "Resource not found"))
  ([message]
   (error-response message 404)))

(defn validation-error-response
  "Create a validation error response."
  [errors]
  (error-response "Validation failed" 422 {:validation_errors errors}))

(defn server-error-response
  "Create a server error response."
  ([]
   (server-error-response "Internal server error"))
  ([message]
   (error-response message 500)))

(defn created-response
  "Create a 201 created response with the created resource."
  [data]
  (success-response data 201))

(defn no-content-response
  "Create a 204 no content response."
  []
  {:status 204
   :headers default-headers
   :body ""})

(defn empty-data-response
  "Create a response for empty data sets."
  ([message]
   (success-response {:message message :data []}))
  ([message data]
   (success-response {:message message :data data})))

(defn paginated-response
  "Create a paginated response."
  [data total page limit]
  (success-response
   {:data data
    :pagination {:total total
                 :page page
                 :limit limit
                 :pages (int (Math/ceil (/ total limit)))}}))

(defn with-cors-headers
  "Add CORS headers to a response."
  [response]
  (update response :headers merge
          {"Access-Control-Allow-Origin" "*"
           "Access-Control-Allow-Methods" "GET, POST, PUT, PATCH, DELETE, OPTIONS"
           "Access-Control-Allow-Headers" "Content-Type, Authorization"}))

(defn handle-exception
  "Convert an exception to an appropriate HTTP response."
  [exception]
  (cond
    (instance? java.sql.SQLException exception)
    (server-error-response "Database error occurred")
    
    (instance? java.lang.IllegalArgumentException exception)
    (validation-error-response [(.getMessage exception)])
    
    :else
    (server-error-response "An unexpected error occurred")))
