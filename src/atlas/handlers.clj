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
      (let [response-data
             (if (empty? projects)
               (json/generate-string
                 {:status 200 :message "Projects not found"})
               (json/generate-string projects {:status 200})
             )
           ]
        {:status (:status response-data) :body (json/generate-string response-data)}))))

(defn create-project "Inserts new project to DB"
  [request]
  (let [project-data (json/parse-string (slurp (:body request)) true)]
    (let [response-data
          (try (jdbc/with-db-connection [conn db/pg-db]
                 (let [inserted-project (jdbc/insert! conn :projects {
                                                             :name (:name project-data)
                                                             :description (:description project-data)
                                                             })]
                   (json/generate-string
                     {:status 200
                      :body {:projects inserted-project}}))
              )
              (catch Exception e
                (println :message e)
                (json/generate-string
                  {:status 400
                   :message "Saving project failed"}))
               )
          ]
      {:status (:status response-data) :body response-data}
      )
    ))