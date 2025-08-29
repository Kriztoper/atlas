(ns atlas.utils.validation
  "Data validation utilities.")

(defn required?
  "Check if a value is present and not empty."
  [value]
  (and (some? value)
       (not (and (string? value) (clojure.string/blank? value)))))

(defn valid-string?
  "Check if a value is a valid non-empty string."
  [value]
  (and (string? value)
       (not (clojure.string/blank? value))))

(defn valid-integer?
  "Check if a value is a valid integer."
  [value]
  (or (integer? value)
      (and (string? value)
           (re-matches #"^\d+$" value))))

(defn valid-boolean?
  "Check if a value is a valid boolean."
  [value]
  (or (boolean? value)
      (contains? #{"true" "false"} value)))

(defn max-length?
  "Check if string value doesn't exceed maximum length."
  [value max-len]
  (or (nil? value)
      (and (string? value)
           (<= (count value) max-len))))

(defn min-length?
  "Check if string value meets minimum length."
  [value min-len]
  (and (string? value)
       (>= (count value) min-len)))

(defn validate-field
  "Validate a single field with multiple validation rules."
  [field-name value rules]
  (reduce
   (fn [errors rule]
     (let [[validator message] rule]
       (if (validator value)
         errors
         (conj errors {:field field-name :message message}))))
   []
   rules))

(defn validate-fields
  "Validate multiple fields with their respective rules."
  [data field-rules]
  (reduce
   (fn [all-errors [field-name rules]]
     (let [field-value (get data field-name)
           field-errors (validate-field field-name field-value rules)]
       (concat all-errors field-errors)))
   []
   field-rules))

(defn valid?
  "Check if validation errors list is empty."
  [errors]
  (empty? errors))

;; Common validation rule sets
(def project-rules
  {:name [[required? "Name is required"]
          [valid-string? "Name must be a non-empty string"]
          [#(max-length? % 100) "Name must not exceed 100 characters"]]
   :description [[#(max-length? % 500) "Description must not exceed 500 characters"]]})

(def task-rules
  {:name [[required? "Name is required"]
          [valid-string? "Name must be a non-empty string"]
          [#(max-length? % 200) "Name must not exceed 200 characters"]]
   :description [[#(max-length? % 1000) "Description must not exceed 1000 characters"]]
   :project_id [[required? "Project ID is required"]
                [valid-integer? "Project ID must be a valid integer"]]})

(def todo-rules
  {:text [[required? "Text is required"]
          [valid-string? "Text must be a non-empty string"]
          [#(max-length? % 300) "Text must not exceed 300 characters"]]
   :task_id [[required? "Task ID is required"]
             [valid-integer? "Task ID must be a valid integer"]]
   :completed [[valid-boolean? "Completed must be a boolean value"]]})

(defn validate-project
  "Validate project data."
  [project-data]
  (validate-fields project-data project-rules))

(defn validate-task
  "Validate task data."
  [task-data]
  (validate-fields task-data task-rules))

(defn validate-todo
  "Validate todo data."
  [todo-data]
  (validate-fields todo-data todo-rules))

;; Update validation functions (for partial updates)
(defn validate-task-update
  "Validate task data for updates (no required fields)."
  [task-data]
  (let [update-rules (dissoc task-rules :project_id)] ; Remove required project_id for updates
    (validate-fields task-data update-rules)))

(defn validate-todo-update
  "Validate todo data for updates (no required fields)."
  [todo-data]
  (let [update-rules (dissoc todo-rules :task_id)] ; Remove required task_id for updates
    (validate-fields todo-data update-rules)))

(defn parse-integer
  "Safely parse integer from string or return the integer if already an integer."
  [value]
  (cond
    (integer? value) value
    (string? value)
    (try
      (Integer/parseInt value)
      (catch NumberFormatException _
        nil))
    :else nil))

(defn parse-boolean
  "Parse boolean from various formats."
  [value]
  (cond
    (boolean? value) value
    (string? value) (= "true" (clojure.string/lower-case value))
    :else false))
