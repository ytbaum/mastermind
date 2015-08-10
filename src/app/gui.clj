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


(defn yonis-frame []
  (frame
    :title "Yoni's frame"
    :height 500
    :width 500
    :content (vertical-panel 
                :items [(canvas :id :canvas1 :background "#BBBBDD" :paint nil
                          :listen [:mouse-entered (fn [e] (alert "Mouse entered!"))])
                        (canvas :id :canvas2 :background "#AAAACC" :paint nil)]
                )))

(defn show-yf []
  (-> (yonis-frame) show!))
