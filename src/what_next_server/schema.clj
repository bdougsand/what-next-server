(ns what-next-server.schema
  (:require [datomic-schema.schema :as s]))

(defn parts []
  [(s/part "app")])

(defn schema []
  [(s/schema
    user
    (s/fields
     [email :string :indexed :unique-identity]
     [password :string]
     [verify :string]))

   (s/schema
    task
    (s/fields
     [name :string :index]
     [user :ref]))

   (s/schema
    work
    (s/fields
     [start :instant :index]
     [duration :long :index]
     [task :ref]
     [notes :string]))])

