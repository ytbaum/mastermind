(ns app.gui
  (:use [seesaw core graphics color])
  (:use app.core))

(def num-pegs 4)

(defn paint-guess-square [c g]
  (let [ht (.getHeight c)
        wd (.getWidth c)
        brdr 5]
    (draw g
      (rect brdr brdr (- ht (* 2 brdr)) (- wd (* 2 brdr)))
      (style :background "#FFFFFF"))))

(defn guess-square [w h]
  (canvas :background "#000000"
    :size [w :by h]
    :paint paint-guess-square
    :listen [:mouse-clicked (fn [e] (config! e :background (show! (color-chooser))))]))

(defn guess-row [num-pegs]
  (horizontal-panel
    :items (vec (repeatedly num-pegs #(guess-square 100 100)))
    :border 5))

(defn board []
  (frame
    :title "Yoni's frame"
    :height 500
    :width 500
    :content (scrollable (vertical-panel
                            :items (vec (repeatedly nturns #(guess-row num-pegs)))
                            :border 5))))

(defn chooser-square [col]
  (canvas :background col
    :size [50 :by 50]
    :listen [:mouse-clicked (fn [e] (return-from-dialog e col))]))

(defn color-chooser []
  (dialog
    :title "Color Chooser"
    :content "Choose a color:"
    :size [400 :by 200]
    :options (map chooser-square colors)))

(defn show-board []
  (-> (board) show!))
