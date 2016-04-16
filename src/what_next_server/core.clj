(ns what-next-server.core
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]

            [compojure.core :refer [ANY GET POST defroutes]]
            [compojure.route :refer [not-found]]

            [ring.middleware.session :refer [wrap-session]]

            [what-next-server.db :as db]
            [what-next-server.routes :as r]
            [what-next-server.users :as u]))

(defn wrap-conn [handler webserver]
  (fn [req]
    (let [conn (get-in webserver [:db :conn])]
      (handler (assoc req :conn conn)))))

(defroutes app-routes
  (GET "/" [] r/do-login))

(defn app [webserver]
  (-> #'app-routes
      (wrap-conn webserver)
      (wrap-session)))

(defrecord Webserver [config]
  component/Lifecycle
  (start [this]
    (assoc this :stop-fn
           (run-server (app this)
                       (merge {:port 3002} config))))

  (stop [this]
    (when-let [stop (:stop-fn this)]
      (stop))
    (dissoc this :stop-fn)))

(defn web-server [& [options]]
  (->Webserver options))

(defn what-next-system [config]
  (component/system-map
   :app (component/using
         (web-server (:web-config config))
         [:db])
   :db (db/make-database (:db-config config))))

(def system (what-next-system nil))

(defn init []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system component/stop))

(defn main []
  (component/start (what-next-system {})))

