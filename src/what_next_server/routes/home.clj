(ns what-next-server.routes.home
  (:require [compojure.core :refer :all]
            [what-next-server.views.layout :as layout]))

(defn home []
  (layout/common [:h1 "Hello World!"]))

(defroutes home-routes
  (GET "/" [] (home)))
