(defproject atlas "0.1.0-SNAPSHOT"
  :description "Backend API for dev assistant tool"
  :url "https://github.com/Kriztoper/atlas"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.14.2"]
                 [ring/ring-jetty-adapter "1.14.2"]
                 [compojure "1.7.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.7.3"]
                 [cheshire "6.0.0"]
                 [ring-cors "0.1.13"]]
  :main ^:skip-aot atlas.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
