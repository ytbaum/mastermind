(ns app.gui
  (:use [seesaw core graphics color]))

(defn yf-paint [c g]
  (let [w (.getWidth c)
        w2 (/ w 2)
        h (.getHeight c)
        w2 (/ h 2)]
    (draw g
      (rect 10 10 100 50)
      (style :background (color 128 128 128 128)))))

(defn paint-guess-square [c g]
  (let [ht (.getHeight c)
        wd (.getWidth c)
        brdr 5]
    (draw g
      (rect brdr brdr (- ht (* 2 brdr)) (- wd (* 2 brdr)))
      (style :background "#FFFFFF"))))

(defn guess-square [w h]
  (canvas :background "#000000" :size [w :by h] :paint paint-guess-square))

(defn guess-row []
  (horizontal-panel
    :items [(guess-square 100 100)
            (guess-square 100 100)
            (guess-square 100 100)
            (guess-square 100 100)]
    :border 5))

(defn yonis-frame []
  (frame
    :title "Yoni's frame"
    :height 500
    :width 500
    :content (guess-row)))

(defn show-yf []
  (-> (yonis-frame) show!))
