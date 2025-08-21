(ns atlas.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [atlas.routes :as routes]))

(defn -main
  [& args]
  (jetty/run-jetty routes/app
                   {:port 8080
                    :join? true}))
