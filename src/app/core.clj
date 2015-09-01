(ns app.core
  (:use app.gui [seesaw.core :only [select]])
  (:require [clojure.pprint :as pp] [clojure.string :as s :only [lower-case]]))

(def nturns 10)
(def ncolors 4)

(defn get-combo [colors ncolors]
  (repeatedly ncolors (fn [] (rand-nth (vec colors)))))

(defn get-color-map [colors]
  (let [first-letters (map (fn [color] (str (get color 0))) colors)]
    (zipmap first-letters colors))) 

; grabbed this from:
; http://blog.jayfields.com/2011/01/clojure-select-keys-select-values-and.html
(defn select-values [map ks]
         (reduce #(conj %1 (map %2)) [] ks))

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
        (let [freqs (frequencies feedback)
              black-pegs (or (freqs "black") 0)
              white-pegs (or (freqs "white") 0)]
          [black-pegs white-pegs (- ncolors (+ black-pegs white-pegs))])
        (let [cur (first remainder)
              elt-found (> (get combo-freqs cur 0) 0)]
          (recur
            (if elt-found (conj feedback "white") feedback)
            (rest remainder)
            (if elt-found (update-in combo-freqs [cur] dec) combo-freqs)))))))

(defn play-game [f]
  (let [combo (get-combo colors ncolors)]
    (loop [rows (select f [:.row])
           feedback-rows (select f [:.feedback-row])
           victory false]
      (if (or victory
            (empty? rows))
        victory
        (let [row (first rows)
              feedback-row (first feedback-rows)
              guess-prom (promise)
              row-deac-fn (activate-row row)
              submit-deac-fn (activate-submit f row guess-prom)
              feedback (get-feedback @guess-prom combo)]
          (doall (display-feedback feedback feedback-row))
          (row-deac-fn)
          (submit-deac-fn)
          (recur (rest rows) (rest feedback-rows) (= feedback [ncolors 0 0])))))))

(defn play []
  (let [f (mm-frame ncolors nturns)]
    (show-instructions nil)
    (loop [should-play true]
      (if (= :no should-play)
        (show-thx-for-playing)
        (let [victory (play-game f)
              should-play (end-game-dialog victory)]
            (if (= :success should-play)
              (do
                (clear-board f)
                (scroll-to-top f)))
            (recur should-play))))))
