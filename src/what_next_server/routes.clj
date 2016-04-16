(ns what-next-server.routes
  (:require [compojure.core :refer [ANY GET POST context defroutes]]
            [compojure.route :refer [not-found]]
            [ring.util.response :as resp :refer [response]]

            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]

            [clojure.data.json :as json]

            [what-next-server.users :as users]
            [datomic.api :as d]))


;; (defn create-user [{params :params, conn :conn}]
;;   (let [{:keys [email password]}]
;;     (if (users/user-id (d/db conn) email)
;;       (resp/status {:error "That email address is already in use."} 403)
;;       (let [tx @(d/transact
;;                  conn
;;                  (users/new-user email password))]))))

(defn do-login [req]
  (let [{:keys [login password]} (:params req)]
    (if (and login password)
      (let [db (d/db (:conn req))
            user (users/authorize-user db login password)]
        (if user
          (-> (response (str "Logged in as user: " user))
              (assoc-in [:session :login] login))

          "No such user"))

      "Missing required keys")))

(defroutes app-routes
  (POST "/api/create" create-user))

(def secured-routes
  (-> app-routes
      (friend/authenticate
       {:allow-anon? true
        :login-uri "/login"
        })))


