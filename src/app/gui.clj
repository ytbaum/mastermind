(ns app.gui
  (:use [seesaw core graphics color border])
  (:use app.core))

(def default-col "lightgray")

(defn col-name-pair [name-col-pair]
  (let [pair (vec name-col-pair)]
    [(take 3 (get-rgba (get pair 1))) (get pair 0)]))

(def colors-map
  (into {} (map col-name-pair @#'seesaw.color/color-names)))

(defn guess-square [w h]
  (canvas
    :background default-col
    :border (line-border :thickness 5 :color "darkGray")
    :size [w :by h]
    :paint nil))

(defn guess-row [num-pegs row-num]
  (horizontal-panel
    :items (vec (repeatedly num-pegs #(guess-square 100 100)))
    :id (keyword (str "row-" row-num))
    :border 5
    :class :row))

(defn board []
  (frame
    :title "Yoni's frame"
    :height 500
    :width 500
    :content (horizontal-panel
                :items [(scrollable (vertical-panel
                                      :items (vec (map #(guess-row ncolors %) (range nturns)))
                                      :border 5
                                      :id :rows))
                        (vertical-panel :items [(button :id :submit :text "Submit")])])))

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

(defn activate-row [row]
  (listen
    (select row [:.row :> :JPanel])
    :mouse-clicked (fn [e]
                    (if-let [background (show! (color-chooser))]
                      (config! e :background background)))))

(defn get-guess [row]
  (map #(colors-map (take 3 (get-rgba (config % :background)))) (select row [:.row :JPanel])))

(defn show-board []
  (-> (board) show!))
