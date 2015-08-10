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
  (canvas :background "#000000" :size [w :by h] :paint paint-guess-square))

(defn guess-row [num-pegs]
  (horizontal-panel
    :items (vec (repeatedly num-pegs #(guess-square 100 100)))
    :border 5))

(defn yonis-frame []
  (frame
    :title "Yoni's frame"
    :height 500
    :width 500
    :content (guess-row num-pegs)))

(defn chooser-square [col]
  (canvas :background col
    :size [50 :by 50]
    :listen [:mouse-clicked (fn [e] (alert (str col " was Clicked!")))]))

(defn color-chooser []
  (frame
    :title "Color Chooser"
    :height 100
    :width 400
    :content (horizontal-panel :items (vec (map chooser-square colors)))))

(defn show-yf []
  (-> (yonis-frame) show!))
