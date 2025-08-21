(ns atlas.configuration.database-config)

(def pg-db {:classname "org.postgresql.Driver"
            :subprotocol "postgresql"
            :subname "//localhost:5432/atlas_dev"
            :user "admin"})