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
    (let [projects (jdbc/query conn ["SELECT * FROM projects"])]
      (let [response-data
             (if (empty? projects)
               (json/generate-string
                 {:status 200 :message "Projects not found"})
               (json/generate-string projects {:status 200})
             )
           ]
        {:status (:status response-data) :body response-data}
      )
    )))

(defn create-project
  [request]
  "Hello world")