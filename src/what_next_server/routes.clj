(ns what-next-server.routes
  (:require [compojure.core :refer [ANY GET POST context defroutes]]
            [compojure.route :refer [not-found]]
            [ring.util.response :as resp :refer [response]]

            [clojure.data.json :as json]

            [what-next-server.users :as users]
            [datomic.api :as d]
            [clojure.string :as str]))


(defn json-response [x]
  (-> (json/write-str x)
      (response)
      (resp/content-type "application/json")))


(defn create-user [{params :params, conn :conn}]
  (let [{:keys [email password]} params]
    (if (users/user-id (d/db conn) email)
      (resp/status {:error "That email address is already in use."} 403)
      (let [tx @(d/transact
                 conn
                 (users/new-user email password))]))))


(defn do-login [req]
  (let [{:keys [login password]} (:params req)]
    (if (and login password)
      (let [db (d/db (:conn req))
            user (users/authorize-user db login password)]
        (if user
          (-> (response (str "Logged in as user: " user))
              (assoc-in [:session :user-id] (:db/id user)))

          "No such user"))

      "Missing required keys.")))


(defn user-list [req]
  (let [emails (users/find-emails (d/db (:conn req)))]
    (str/join "<br>" emails)))


(defn save-work [req]
  (let [tasks (json/read-str (get-in req [:params :work]))]
    "Done"))


(defn add-work [{{:keys []} :params}])


(defn work-list [req]
  (if-let [user-id (get-in req [:session :user-id])]
    (json-response
     {:work (for [work (users/find-work (d/db (:conn req)) user-id)]
              {:task (:task/name (:work/task work))
               :start (:work/start work)
               :duration (:work/duration work)
               :notes (:work/notes work)})})))


;; Only for testing!
(defn force-login [req]
  (let [login (get-in req [:params :login])]
    (if-let [user (users/find-user (d/db (:conn req)) login)]
      (-> (response (str "Logged in as user: " user))
          (assoc-in [:session :user-id] (:db/id user)))

      "No such user!")))


(defn logout [req]
  (-> (json-response {:success "Logged out"})
      (update :session dissoc :user-id)))


(defn whoami [{:keys [user-id conn]}]
  (let [user (d/pull (d/db conn) '[*] user-id)]
    (prn-str user)))


(defn wrap-user [handler]
  (fn [req]
    (if-let [user-id (:user-id (:session req))]
      (handler (assoc req :user-id user-id))

      (-> (response (json/write-str
                     {:error "You are not logged in"}))
          (resp/status 403)))))


(defroutes user-routes
  (GET "/work" [] work-list)
  (GET "/who" [] whoami))


(defroutes app-routes
  (POST "/create" [] create-user)
  (GET "/users" [] user-list)
  (GET "/force" [] force-login)
  (GET "/logout" [] logout)
  (GET "/debug" request (prn-str request))

  (wrap-user
   (context "*" [] user-routes)))

