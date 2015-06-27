(ns app.core)

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

(defn get-feedback [guess combo]
  (loop [guess guess
         combo combo
         combo-freqs (frequencies combo)
         feedback []]
    (if (empty? guess)
      feedback
      (let [guess-cur (first guess)
            combo-cur (first combo)
            elt-feedback
              (if (> (get combo-freqs guess-cur 0) 0)
                (if (= guess-cur combo-cur) "black" "white"))]
        (recur
          (rest guess)
          (rest combo)
          (if (not (nil? elt-feedback))
            (update-in combo-freqs [guess-cur] dec)
            combo-freqs)
          (conj feedback elt-feedback))))))
