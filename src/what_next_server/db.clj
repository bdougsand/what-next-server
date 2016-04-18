(ns what-next-server.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [datomic-schema.schema :as s]

            [what-next-server.schema :refer [parts schema]]))

(defrecord Database [options]
  component/Lifecycle
  (start [this]
    (let [uri (:uri options)]
      (d/create-database uri)
      (let [db (d/connect uri)]
        (d/transact
         db
         (concat (s/generate-parts (parts))
                 (s/generate-schema (schema))))
        (assoc this :conn db))))

  (stop [this]
    (when (:delete-on-stop options)
      (d/delete-database (:conn this)))
    (dissoc this :conn)))

(defn make-database [& [options]]
  (->Database (merge {:uri "datomic:dev://localhost:4334/what-next"}
                     options)))

(defn delete! [db-component]
  (d/delete-database (-> db-component :options :uri)))

