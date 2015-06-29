(ns app.core
  (:require [clojure.pprint :as pp]))

(def colors #{"red" "green" "blue" "orange" "white" "yellow"})
(def nturns 10)
(def ncolors 4)
(def should-play true)

(defn get-combo [colors ncolors]
  (repeatedly ncolors (fn [] (rand-nth (vec colors)))))

(defn get-color-map [colors]
  (let [first-letters (map (fn [color] (str (get color 0))) colors)]
    (zipmap first-letters colors))) 

; grabbed this from:
; http://blog.jayfields.com/2011/01/clojure-select-keys-select-values-and.html
(defn select-values [map ks]
         (reduce #(conj %1 (map %2)) [] ks))

(defn get-user-guess [colors]
  (println "Enter your guess:")
  (let [guess (read-line)
        color-map (get-color-map colors) ]
    (select-values color-map (map str (vec guess)))))  

; find which elements of the user guess vector, if any, match the hidden combo in both color and location
(defn get-loc-matches [guess combo]
  (loop [guess guess
          combo combo
          feedback []
          remainder []
          combo-freqs (frequencies combo)]
    (if (empty? guess)
      [(shuffle (remove nil? feedback)) remainder combo-freqs]
      (let [guess-cur (first guess)
            combo-cur (first combo)
            elt-feedback
              (if (> (get combo-freqs guess-cur 0) 0)
                (if (= guess-cur combo-cur) "black" "white"))]
        (recur
          (rest guess)
          (rest combo)
          (if (= elt-feedback "black")
            (conj feedback "black")
            feedback)
          (if (= elt-feedback "black")
            remainder
            (conj remainder guess-cur))
          (if (= elt-feedback "black")
            (update-in combo-freqs [guess-cur] dec)
            combo-freqs))))))

; Do arguments fall within expected nature of arguments? How to handle if they don't?
; Test over all possible combinations of groupings four-color arguments? Also test to make sure that different orderings of the same combo/guess don't make a difference
(defn get-feedback [guess combo]
  (let [[feedback remainder combo-freqs] (get-loc-matches guess combo)]
    (loop [feedback feedback
            remainder remainder
            combo-freqs combo-freqs]
      (if (empty? remainder)
        (shuffle feedback)
        (let [cur (first remainder)
              elt-found (> (get combo-freqs cur 0) 0)]
          (recur
            (if elt-found (conj feedback "white") feedback)
            (rest remainder)
            (if elt-found (update-in combo-freqs [cur] dec) combo-freqs)))))))

(defn print-result [victory]
  (if victory (println "VICTORY!!!") (println "Bow to me...")))

(defn play-game []
  (let [combo (get-combo colors ncolors)]
    (loop [victory false
          turns-left nturns]
      (if (or victory (= turns-left 0))
        (do
          (print-result victory)
          [victory combo])
        (let [guess (get-user-guess colors)
              feedback (get-feedback guess combo)]
          (pp/pprint feedback)
          (recur (= guess combo) (dec turns-left)))))))
