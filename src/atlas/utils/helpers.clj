(ns atlas.utils.helpers
  "Common utility functions for the Atlas application."
  (:require [clojure.string :as str]
            [cheshire.core :as json]))

;; String utilities
(defn normalize-string
  "Normalize a string by trimming and converting to lowercase."
  [s]
  (when s
    (str/lower-case (str/trim s))))

(defn slug-from-string
  "Create a URL-friendly slug from a string."
  [s]
  (when s
    (-> s
        str/lower-case
        (str/replace #"[^a-z0-9]+" "-")
        (str/replace #"^-|-$" ""))))

(defn truncate-string
  "Truncate a string to the specified length, adding ellipsis if needed."
  [s max-length & [ellipsis]]
  (when s
    (if (<= (count s) max-length)
      s
      (str (subs s 0 (- max-length (count (or ellipsis "..."))))
           (or ellipsis "...")))))

;; Collection utilities
(defn find-by-key
  "Find the first item in a collection where the specified key matches the value."
  [coll key-name value]
  (first (filter #(= (get % key-name) value) coll)))

(defn group-by-key
  "Group items in a collection by the specified key."
  [coll key-name]
  (group-by #(get % key-name) coll))

(defn map-by-key
  "Create a map from a collection using the specified key as the map key."
  [coll key-name]
  (into {} (map #(vector (get % key-name) %) coll)))

;; Date/time utilities
(defn current-timestamp
  "Get the current timestamp as an Instant."
  []
  (java.time.Instant/now))

(defn format-timestamp
  "Format a timestamp for display."
  [timestamp & [format-str]]
  (when timestamp
    (let [formatter (java.time.format.DateTimeFormatter/ofPattern 
                      (or format-str "yyyy-MM-dd HH:mm:ss"))
          zone-id (java.time.ZoneId/systemDefault)]
      (.format (.atZone timestamp zone-id) formatter))))

(defn parse-timestamp
  "Parse a timestamp string into an Instant."
  [timestamp-str]
  (when (and timestamp-str (not (str/blank? timestamp-str)))
    (try
      (java.time.Instant/parse timestamp-str)
      (catch Exception _
        nil))))

;; Validation utilities
(defn valid-email?
  "Check if a string is a valid email address."
  [email]
  (when email
    (re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" email)))

(defn valid-uuid?
  "Check if a string is a valid UUID."
  [uuid-str]
  (when uuid-str
    (try
      (java.util.UUID/fromString uuid-str)
      true
      (catch Exception _
        false))))

(defn non-empty-string?
  "Check if a value is a non-empty string."
  [s]
  (and (string? s) (not (str/blank? s))))

;; JSON utilities
(defn parse-json-safe
  "Safely parse JSON string, returning nil if parsing fails."
  [json-str]
  (when (non-empty-string? json-str)
    (try
      (json/parse-string json-str true)
      (catch Exception _
        nil))))

(defn generate-json-safe
  "Safely generate JSON string, returning nil if generation fails."
  [data]
  (when data
    (try
      (json/generate-string data)
      (catch Exception _
        nil))))

;; Environment utilities
(defn get-env
  "Get environment variable with optional default value."
  [key & [default]]
  (or (System/getenv key) default))

(defn get-env-boolean
  "Get environment variable as boolean."
  [key & [default]]
  (let [value (get-env key)]
    (if value
      (contains? #{"true" "yes" "1" "on"} (str/lower-case value))
      default)))

(defn get-env-int
  "Get environment variable as integer."
  [key & [default]]
  (let [value (get-env key)]
    (if value
      (try
        (Integer/parseInt value)
        (catch NumberFormatException _
          default))
      default)))

;; ID generation
(defn generate-id
  "Generate a random ID."
  []
  (.toString (java.util.UUID/randomUUID)))

(defn generate-short-id
  "Generate a short random ID (8 characters)."
  []
  (subs (str/replace (generate-id) "-" "") 0 8))

;; Map utilities
(defn deep-merge
  "Deep merge two maps."
  [map1 map2]
  (merge-with (fn [v1 v2]
                (if (and (map? v1) (map? v2))
                  (deep-merge v1 v2)
                  v2))
              map1 map2))

(defn remove-nil-values
  "Remove keys with nil values from a map."
  [m]
  (into {} (filter (comp some? val) m)))

(defn kebab->snake
  "Convert kebab-case keywords to snake_case."
  [k]
  (when k
    (keyword (str/replace (name k) "-" "_"))))

(defn snake->kebab
  "Convert snake_case keywords to kebab-case."
  [k]
  (when k
    (keyword (str/replace (name k) "_" "-"))))

;; Error handling
(defn with-error-logging
  "Execute function with error logging."
  [f error-msg & [context]]
  (try
    (f)
    (catch Exception e
      (println (format "ERROR: %s - %s" error-msg (.getMessage e)))
      (when context
        (println "Context:" (pr-str context)))
      (.printStackTrace e)
      (throw e))))

;; Performance utilities
(defn time-execution
  "Time the execution of a function and return [result time-ms]."
  [f]
  (let [start (System/nanoTime)
        result (f)
        end (System/nanoTime)
        duration-ms (/ (- end start) 1000000.0)]
    [result duration-ms]))

(defn log-execution-time
  "Execute function and log execution time."
  [f description]
  (let [[result time-ms] (time-execution f)]
    (println (format "⏱️  %s completed in %.2fms" description time-ms))
    result))

;; Debugging utilities
(defn debug-tap
  "Tap into a value for debugging without affecting the flow."
  [value & [label]]
  (when label
    (println (format "DEBUG [%s]:" label)))
  (println (pr-str value))
  value)

(defn spy
  "Spy on a function call by logging arguments and result."
  [f & args]
  (println "SPY - Calling with args:" (pr-str args))
  (let [result (apply f args)]
    (println "SPY - Result:" (pr-str result))
    result))
