(ns robots.simulation)

(def simulation {})

;Format map key with coords
(defn format-key [x y]
  (str (format "%02d" x) (format "%02d" y))
  )

;return value of coord x y
(defn get-from-simulation [x y]
  (get simulation (keyword (format-key x y)) )
  )

;return full simulation 50x50
(defn get-simulation []
  (for [x (range 0 50) y (range 50)]
    (assoc {} (keyword (format-key x y)) (get-from-simulation x y))))

;create a new simulation
(defn add-simulation []
  (def simulation {})
  (get-simulation))

;verify if the coord is valid
(defn is-valid-coord [x y]
  (if (and (and (and (< x 50) (< y 50))
                (>= x 0)) (>= y 0))
    true
    false)
  )

;verify if exist a dinosaur in the coord
(defn is-dinosaur [x y]
  (if (and (is-valid-coord x  y) (= "D" (get-from-simulation x y)))
    true
    false
    )
  )

;verify if the coord is free
(defn is-free [x y]
  (if (nil? (get-from-simulation x y))
    true
    false)
  )

;put carac in coord x y
(defn put-map [x y carac]
  (dosync
    (def simulation
      (assoc simulation (keyword (format-key x y)) carac))))

;remove from coord x y
(defn rem-map [x y]
  (dosync
    (def simulation
      (dissoc simulation (keyword (format-key x y))))))

;put in simulation if coord is valid and free
(defn put-in-simulation [x y carac]
  (dosync
    (if (and (is-valid-coord x y) (is-free x y))
      (put-map x y carac)
      (throw
        (ex-info "Posicao ocupada/invalida" {})))))

;add robot in simulation
(defn add-robot [x y direction]
  (case direction
    "L" (put-in-simulation x y "←")
    "R" (put-in-simulation x y "→")
    "U" (put-in-simulation x y "↑")
    "D" (put-in-simulation x y "↓")
    (throw
      (ex-info "Comando invalido só e possivel virar para L-Esquerda R-Direita D-Baixo U-Cima" {})))
  (get-simulation))

;destroys dinosaurs around coord x y and return amount of destroyed
(defn atack [x y]
  (if (is-dinosaur (dec x) y)
    (do (rem-map (dec x) y)
        (print " entrou " (dec x) " " y)
        (+ 1 (atack x y)))
    (if (is-dinosaur (inc x) y)
      (do (rem-map (inc x) y)
          (print " entrou " (inc x) " " y)
          (+ 1 (atack x y)))
      (if (is-dinosaur x (inc y))
        (do (rem-map x (inc y))
            (print " entrou " x " " (inc y))
            (+ 1 (atack x y)))
        (if (is-dinosaur x (dec y))
          (do (rem-map x (dec y))
              (print " entrou " x " " (dec y))
              (+ 1 (atack x (dec y))))
          0)))))

(defn go-left [x y]
  (put-in-simulation x (dec y) (get-from-simulation x y))
  (rem-map x y))

(defn go-right [x y]
  (put-in-simulation x (inc y) (get-from-simulation x y))
  (rem-map x y))

(defn go-up [x y]
  (put-in-simulation (dec x) y (get-from-simulation x y))
  (rem-map x y))

(defn go-down [x y]
  (put-in-simulation (inc x) y (get-from-simulation x y))
  (rem-map x y))

;add dinosaur to coord x y
(defn add-dinosaur [x y]
  (put-in-simulation x y "D")
  (get-simulation))

;turn robot in coord x y to side
(defn turn [x y side]
  (case side
    "R" (put-map x y "→")
    "L" (put-map x y "←")
    "U" (put-map x y "↑")
    "D" (put-map x y "↓")
    (throw
      (ex-info "Comando invalido só e possivel virar para L-Esquerda R-Direita D-Baixo U-Cima" {})))
  (get-simulation))

;move robot to front in coord x y
(defn move-front [x y]
  (case (get-from-simulation x y)
    "→" (go-right x y )
    "←" (go-left x y )
    "↑" (go-up x y )
    (go-down x y )))

;move robot to back in coord x y
(defn move-back [x y]
  (case (get-from-simulation x y)
    "→" (go-left x y )
    "←" (go-right x y )
    "↑" (go-down x y )
    (go-up x y )
    ))

;move robot in cord x y to back(B) or front(F)
(defn move [x y side]
  (case side
    "F" (move-front x y)
    "B" (move-back x y)
    (throw
      (ex-info "Comando invalido só é possivel andar para frente ou para traz F/B" {}))
    )
  (get-simulation))

;command robot to turn, move or attack
(defn update-robot [x y command & [side]]
  (dosync
    (if (or (is-free x y) (is-dinosaur x y) )
      (throw
        (ex-info "Movimento invalido não existe robo aqui" {})))

    (case command
      "turn" (turn x y side)
      "move" (move x y side)
      "atack" (str (atack x y))
      (throw
        (ex-info "Comando Invalido! Movimentos validos para o robo: turn, atack ou move" {})))

    )
  )