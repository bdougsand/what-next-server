(ns what-next-server.ring.datomic-session-store
  (:require [ring.middleware.session.store :refer [SessionStore]]))

(deftype DatomicStore [conn]
  SessionStore
  (read-sess))
