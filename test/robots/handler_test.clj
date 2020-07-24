(ns robots.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [robots.handler :refer :all]
            [robots.simulation :refer :all]))

(defn before [f]
  (add-simulation)
  (f)
  )

(use-fixtures :each before)

(deftest test-app
  (testing "get-simulation"
    (let [response (app (mock/request :get "/api/simulation"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing "add-simulation"
    (let [response (app (mock/request :post "/api/simulation"))]
      (is (= (:status response) 200))))

  (testing "add-robot"
    (let [response (app (mock/request :post "/api/robot?x=0&y=0&side=L"))]
      (is (= (:status response) 200))))

  (testing "add-dinosaur"
    (let [response (app (mock/request :post "/api/dinosaur?x=0&y=1"))]
      (is (= (:status response) 200))))

  (testing "update-robot"
    (let [response (app (mock/request :put "/api/robot/atack?x=0&y=0"))]
      (is (= (:status response) 200))
      (is (= (:body response) "1"))))
  )
