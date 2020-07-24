(ns robots.core-test
  (:require [clojure.test :refer :all]
            [robots.simulation :refer :all]))

(defn before [f]
  (add-simulation)
  (f)
  )

(use-fixtures :each before)

(deftest map-test
  (is (= 0 (count  simulation)))
  )

(deftest test-is-valid
  (is (= true (is-valid-coord 0 0)))
  (is (= false (is-valid-coord -1 0)))
  (is (= false (is-valid-coord 0 -1)))
  (is (= false (is-valid-coord 50 0)))
  (is (= false (is-valid-coord 0 50)))
  (is (= true (is-valid-coord 49 49))))

(deftest test-dinosaur
  (add-dinosaur 0 0)
  (is (= true (is-dinosaur 0 0)))
  (is (= false (is-dinosaur 0 1)))
  )

(deftest test-moves-left-back
  (add-robot 0 0 "L")
  (update-robot 0 0 "move" "B")
  (is (= "←" (get-from-simulation 0 1)))
  (is (nil? (get-from-simulation 0 0))))

(deftest test-moves-down-back
  (add-robot 1 0 "D")
  (update-robot 1 0 "move" "B")
  (is (= "↓" (get-from-simulation 0 0)))
  (is (nil? (get-from-simulation 1 0))))

(deftest test-moves-right-back
  (add-robot 0 1 "R")
  (update-robot 0 1 "move" "B")
  (is (= "→" (get-from-simulation 0 0)))
  (is (nil? (get-from-simulation 0 1))))

(deftest test-moves-up-back
  (add-robot 0 0 "U")
  (update-robot 0 0 "move" "B")
  (is (= "↑" (get-from-simulation 1 0)))
  (is (nil? (get-from-simulation 0 0))))

(deftest test-moves-left-front
  (add-robot 0 1 "L")
  (update-robot 0 1 "move" "F")
  (is (= "←" (get-from-simulation 0 0)))
  (is (nil? (get-from-simulation 0 1))))

(deftest test-moves-up-front
  (add-robot 1 0 "U")
  (update-robot 1 0 "move" "F")
  (is (= "↑" (get-from-simulation 0 0)))
  (is (nil? (get-from-simulation 1 0))))

(deftest test-moves-down-front
  (add-robot 0 0 "D")
  (update-robot 0 0 "move" "F")
  (is (= "↓" (get-from-simulation 1 0)))
  (is (nil? (get-from-simulation 0 0))))

(deftest test-moves-right-front
  (add-robot 0 0 "R")
  (update-robot 0 0 "move" "F")
  (is (= "→" (get-from-simulation 0 1)))
  (is (nil? (get-from-simulation 0 0))))

(deftest test-turn-right
  (add-robot 0 0 "U")
  (update-robot 0 0 "turn" "R")
  (is (= "→" (get-from-simulation 0 0))))

(deftest test-turn-left
  (add-robot 0 0 "U")
  (update-robot 0 0 "turn" "L")
  (is (= "←" (get-from-simulation 0 0))))

(deftest test-turn-up
  (add-robot 0 0 "R")
  (update-robot 0 0 "turn" "U")
  (is (= "↑" (get-from-simulation 0 0))))

(deftest test-turn-down
  (add-robot 0 0 "U")
  (update-robot 0 0 "turn" "D")
  (is (= "↓" (get-from-simulation 0 0))))

(deftest test-atack
  (add-robot 1 1 "R")
  (add-dinosaur 0 1)
  (add-dinosaur 1 0)
  (add-dinosaur 1 2)
  (add-robot 2 1 "R")
  (is (= "3"  (update-robot 1 1 "atack"))))

(deftest test-error
  (add-dinosaur 0 0)
  (add-robot 0 1 "L")
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Posicao ocupada/invalida"
                        (add-dinosaur 0 0)))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Posicao ocupada/invalida"
                        (add-dinosaur -1 0)))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Movimento invalido não existe robo aqui"
                        (update-robot 0 0 "move" "L")))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Comando invalido só e possivel virar para L-Esquerda R-Direita D-Baixo U-Cima"
                        (add-robot 0 0 "B")))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Comando invalido só e possivel virar para L-Esquerda R-Direita D-Baixo U-Cima"
                        (update-robot 0 1 "turn" "B")))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Comando invalido só é possivel andar para frente ou para traz F/B"
                        (update-robot 0 1 "move" "L")))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Comando Invalido! Movimentos validos para o robo: turn, atack ou move"
                        (update-robot 0 1 "andar" "L")))
  )