(ns atlas.routes
  (:require [compojure.core :as compojure]
            [compojure.route :as compojure-route]
            [atlas.handlers :as handlers]))

(compojure/defroutes app
  (compojure/GET "/" params handlers/home)
  (compojure/POST "/api/projects" params handlers/create-project))