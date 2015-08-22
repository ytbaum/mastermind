(ns app.gui
  (:use [seesaw core graphics color border]))

(def default-col "lightgray")
(def default-brdr-col "darkgray")
(def black-pin-col "black")
(def white-pin-col "white")
(def no-pin-col "darkgray")


(defn col-name-pair [name-col-pair]
  (let [pair (vec name-col-pair)]
    [(take 3 (get-rgba (get pair 1))) (get pair 0)]))

(def colors #{"red" "green" "blue" "orange" "white" "yellow"})
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

(defn board [ncolors nturns]
  (scrollable (vertical-panel
    :items (vec (map #(guess-row ncolors %) (range nturns)))
    :border 5
    :id :rows)))

(defn feedback-square [col txt]
  (text
    :text txt
    :background default-col
    :foreground col
    :font "ARIAL-BOLD-14"
    :size [30 :by 30]
    :halign :center
    :border (line-border :thickness 2 :color default-brdr-col)
    :class :feedback-square))

(defn feedback-row [feedback]
  (horizontal-panel
    :items (vec (map feedback-square [black-pin-col white-pin-col no-pin-col] feedback))
    :class :feedback-row))

(defn feedback-container []
  (border-panel
    :size [100 :by 100]
    :class :feedback-ctnr
    :border (line-border :thickness 5 :color default-brdr-col)
    :north (make-widget [:fill-v 35])
    :south (make-widget [:fill-v 35])
    :east (make-widget [:fill-h 5])
    :west (make-widget [:fill-h 5])
    :center (feedback-row [0 0 0])))

(defn feedback-column [nturns]
  (scrollable (vertical-panel
    :items (vec (repeatedly nturns feedback-container))
    :border 5
    :id :feedback-col)))

(defn control-panel []
  (let [v-gap 20]
  (vertical-panel
    :items [[:fill-v v-gap]
            (horizontal-panel
              :size [200 :by 20]
              :items [:fill-h
                      (button :id :submit :text "Submit")
                      :fill-h])]
    :size [200 :by 500])))

(defn mm-frame [ncolors nturns]
  (let [v-gap 20]
    (show! (frame
      :title "Mastermind!"
      :height 500
      :width 800
      :content (horizontal-panel
                  :items [(board ncolors nturns)
                          (feedback-column nturns)
                          (control-panel)])))))

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

(defn get-submit-listener [row prom]
  (fn [e]
    (let [guess (map
                  #(colors-map (take 3 (get-rgba (config % :background))))
                  (select row [:.row :JPanel]))]
      (if (not-any? #{default-col} guess)
        (deliver prom guess)))))

(defn activate-submit [b row prom]
  (listen (select b [:#submit]) :mouse-clicked (get-submit-listener row prom)))

(defn get-guess [row]
  (map #(colors-map (take 3 (get-rgba (config % :background)))) (select row [:.row :JPanel])))

(defn display-feedback [feedback feedback-row]
  (map #(text! %1 %2) (select feedback-row [:.feedback-square]) feedback))
