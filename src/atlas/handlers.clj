(ns atlas.handlers
  (:require [cheshire.core :as json]
            ;[ring.middleware.json :refer [wrap-json-response]]
            [clojure.java.jdbc :as jdbc]
            [atlas.configuration.database-config :as db]))

(defn home
  [request]
  "Hello world")

(defn all-projects "Fetches all projects"
  [_]
  (jdbc/with-db-connection [conn db/pg-db]
    (let [projects (jdbc/query conn ["SELECT * FROM pjm.project"])]
      (if (empty? projects)
        {:status 200 
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:message "No projects found" :data []})}
        {:status 200 
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string projects)}))))

(defn create-project "Inserts new project to DB"
  [request]
  (let [project-data (json/parse-string (slurp (:body request)) true)]
    (try 
      (jdbc/with-db-connection [conn db/pg-db]
        (let [inserted-project (jdbc/insert! conn :projects {
                                                      :name (:name project-data)
                                                      :description (:description project-data)})]
          {:status 200
           :headers {"Content-Type" "application/json"}
           :body (json/generate-string (first inserted-project))}))
      (catch Exception e
        (println "Error creating project:" e)
        {:status 400
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:message "Saving project failed"})}))))
