(ns robots.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as json]
            [ring.util.response :refer [response]]
            [robots.simulation :refer :all]))


(defroutes app-routes
           ;get simulation space
           (GET "/api/simulation" []
             (response (get-simulation)))
           ;create simulation space
           (POST "/api/simulation" []
             (response (add-simulation)))
           ;create robot
           (POST "/api/robot" {:keys [params]}
             (let [{:keys [x y side]} params]
               (response (add-robot (Integer/parseInt x) (Integer/parseInt y) side))))
           ;Issue instructions to a robot: turn left, turn right, move forward, move backwards, and attack
           (PUT "/api/robot/:command" [command x y side]
             (response (update-robot (Integer/parseInt x) (Integer/parseInt y) command side)))
           ;create dinosaur
           (POST "/api/dinosaur" {:keys [params]}
             (let [{:keys [x y]} params]
               (response (add-dinosaur (Integer/parseInt x) (Integer/parseInt y)))))
           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (json/wrap-json-params)
      (json/wrap-json-response)))