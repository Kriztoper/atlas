(ns atlas.handlers.projects
  "HTTP handlers for project operations."
  (:require [atlas.db.repositories.projects :as projects-repo]
            [atlas.utils.response :as response]
            [atlas.utils.request :as request]
            [atlas.utils.validation :as validation]))

(defn get-all-projects
  "Fetch all projects from the database."
  [_request]
  (try
    (let [projects (projects-repo/find-all)]
      (if (empty? projects)
        (response/empty-data-response "No projects found")
        (response/success-response projects)))
    (catch Exception e
      (println "Error fetching projects:" (.getMessage e))
      (response/server-error-response "Failed to fetch projects"))))

(defn get-all-projects-with-stats
  "Fetch all projects with statistics."
  [_request]
  (try
    (let [projects (projects-repo/find-with-stats)]
      (if (empty? projects)
        (response/empty-data-response "No projects found")
        (response/success-response projects)))
    (catch Exception e
      (println "Error fetching projects with stats:" (.getMessage e))
      (response/server-error-response "Failed to fetch projects"))))

(defn get-project-by-id
  "Fetch a single project by ID."
  [req]
  (try
    (let [project-id (request/parse-integer-param 
                       (get-in req [:route-params :project-id]) 
                       "project-id")]
      (if-let [project (projects-repo/find-by-id project-id)]
        (response/success-response project)
        (response/not-found-response "Project not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error fetching project:" (.getMessage e))
      (response/server-error-response "Failed to fetch project"))))

(defn create-project
  "Create a new project."
  [req]
  (try
    (let [project-data (request/parse-json-body req)]
      (if-not project-data
        (response/validation-error-response ["Request body is required"])
        (let [validation-errors (validation/validate-project project-data)]
          (if-not (validation/valid? validation-errors)
            (response/validation-error-response validation-errors)
            (let [created-project (projects-repo/create! project-data)]
              (response/created-response created-project))))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error creating project:" (.getMessage e))
      (response/server-error-response "Failed to create project"))))

(defn update-project
  "Update an existing project."
  [req]
  (try
    (let [project-id (request/parse-integer-param 
                       (get-in req [:route-params :project-id]) 
                       "project-id")
          project-data (request/parse-json-body req)]
      (if-not project-data
        (response/validation-error-response ["Request body is required"])
        (let [validation-errors (validation/validate-project project-data)]
          (if-not (validation/valid? validation-errors)
            (response/validation-error-response validation-errors)
            (if-let [updated-project (projects-repo/update! project-id project-data)]
              (response/success-response updated-project)
              (response/not-found-response "Project not found"))))))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error updating project:" (.getMessage e))
      (response/server-error-response "Failed to update project"))))

(defn delete-project
  "Delete a project."
  [req]
  (try
    (let [project-id (request/parse-integer-param 
                       (get-in req [:route-params :project-id]) 
                       "project-id")]
      (if (projects-repo/delete! project-id)
        (response/no-content-response)
        (response/not-found-response "Project not found")))
    (catch clojure.lang.ExceptionInfo e
      (response/validation-error-response [(.getMessage e)]))
    (catch Exception e
      (println "Error deleting project:" (.getMessage e))
      (response/server-error-response "Failed to delete project"))))
