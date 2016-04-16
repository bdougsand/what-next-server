(ns what-next-server.users
  (:require [datomic.api :as d]
            [cemerick.friend.credentials :as creds])
  (:import [org.mindrot.jbcrypt BCrypt]))

(defn hash-password [password]
  (BCrypt/hashpw password (BCrypt/gensalt)))

(defn verify-password [password hash]
  (BCrypt/checkpw password hash))

(defn new-user [email password]
  [{:db/id #db/id[:db.part/user -1]
    :user/password (hash-password password)
    :user/email email}])

(defn find-user [db email]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?email
         :where [?e :user/email ?email]]
       db email))

(defn find-emails [db]
  (d/q '[:find [?email]
         :in $
         :where [_ :user/email ?email]]
       db))

(defn user-id [db email]
  (d/q '[:find ?e .
         :in $ ?email
         :where [?e :user/email ?email]]
       db email))

(defn find-task [db user-ref name]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?name ?user
         :where
         [?e :task/name ?name]
         [?e :task/user ?user]]
       db name user-ref))

(defn find-task-id [db user-ref name]
  (d/q '[:find ?e .
         :in $ ?name ?user
         :where
         [?e :task/name ?name]
         [?e :task/user ?user]]
       db name user-ref))

(defn find-tasks [db user-ref]
  (d/q '[:find [(pull ?e [*]) ...]
         :in $ ?user
         :where [?e :task/user ?user]]
       db user-ref))

(defn find-work [db user-ref]
  (d/q '[:find [(pull [* {:work/task []}])]]))

(defn authorize-user [db login password]
  (when-let [[user] (find-user db login)]
    (when (verify-password password (:user/password user))
      user)))

(defn user-creds-fn [])

(defn add-task [user-ref name]
  [{:db/id #db/id[:db.part/user -1]
    :task/name name
    :task/user user-ref}])

(defn add-work [db user-ref ])

(defn rename-task [db user-ref old-name new-name]
  (when-let [task (find-task db user-ref old-name)]
    [{:db/id (:db/id task)
      :task/name new-name}]))

(defn record-work [db user-ref task-name start duration notes]
  (let [task-id (find-task-id db user-ref task-name)]
    (concat
     (when-not task-id
       [{:db/id #db/id[:db.part/user -1]
         :task/name task-name
         :task/user user-ref}])
     [{:db/id #db/id[:db.part/user -2]
       :work/start start
       :work/duration duration
       :work/notes notes
       :work/task (or task-id -1)}])))

(comment
  )
