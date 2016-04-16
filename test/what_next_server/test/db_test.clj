(ns what-next-server.test.db-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]

            [com.stuartsierra.component :as component]

            [what-next-server.db :refer :all]
            [what-next-server.users :as u]))

(def db-component
  (make-database {:url "datomic:mem://localhost:4334/what-next"}))

(defn make-db []
  (alter-var-root #'db-component component/start))

(defn teardown-db []
  (delete! db-component)
  (alter-var-root #'db-component component/stop))

(defn db-fixture [f]
  (make-db)
  (f)
  (teardown-db))

(deftest users
  (testing "User creation:"
    (make-db)
    (let [conn (:conn db-component)]
      (d/transact conn (u/new-user "bdougsand@gmail.com"
                                   "password"))
      (d/transact conn (u/new-user "bdougsand@gmail.com"
                                   "password")))))

(use-fixtures :each db-fixture)
