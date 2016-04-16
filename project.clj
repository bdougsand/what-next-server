(defproject didapptic "0.1.0-SNAPSHOT"
  :description "Autodidact"
  :url "http://did.apptic.xyz"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]

                 [com.stuartsierra/component "0.3.1"]

                 ;; Datomic:
                 [com.datomic/datomic-pro "0.9.5344"]
                 [ring "1.4.0"]
                 [ring-transit "0.1.4"]
                 [http-kit "2.2.0-SNAPSHOT"]
                 [datomic-schema "1.3.0"]
                 [compojure "1.5.0"]
                 [jbcrypt "0.3"]
                 [org.clojure/data.json "0.2.6"]
                 [com.cemerick/friend "0.2.2-SNAPSHOT"]]
  :plugins [[cider/cider-nrepl "0.11.0"]
            [refactor-nrepl "2.2.0"]]
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :username :env/datomic_username
                                   :password :env/datomic_password}}

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["target"])

