(ns app.gui
  (:use [seesaw core graphics color border])
  (:use app.core))

(def default-col "lightGray")

(defn guess-square [w h]
  (canvas
    :background default-col
    :border (line-border :thickness 5 :color "#000000")
    :size [w :by h]
    :paint nil))

(defn guess-row [num-pegs row-num]
  (horizontal-panel
    :items (vec (repeatedly num-pegs #(guess-square 100 100)))
    :id (keyword (str "row-" row-num))
    :border 5))

(defn board []
  (frame
    :title "Yoni's frame"
    :height 500
    :width 500
    :content (scrollable (vertical-panel
                            :items (vec (map #(guess-row ncolors %) (range nturns)))
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

(defn activate-row [board row-num]
  (listen
    (select (to-root board) [(keyword (str "#row-" row-num)) (keyword "JPanel")])
    :mouse-clicked (fn [e]
                    (if-let [background (show! (color-chooser))]
                      (config! e :background background)))))

(defn show-board []
  (-> (board) show!))
