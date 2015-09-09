(ns app.gui
  (:use [seesaw core graphics color border]))

(def default-col "lightgray")
(def default-brdr-col "darkgray")
(def active-brdr-col "darkblue")
(def black-pin-col "black")
(def white-pin-col "white")
(def no-pin-col "darkgray")

; Define some initial parameters

(defn col-name-pair [name-col-pair]
  (let [pair (vec name-col-pair)]
    [(take 3 (get-rgba (get pair 1))) (get pair 0)]))

(def colors #{"red" "green" "blue" "orange" "white" "yellow"})
(def colors-map
  (into {} (map col-name-pair @#'seesaw.color/color-names)))

(def frame-height 500)

; Helper functions
(defn get-guess-rows [f]
  (select f [:.row]))

(defn get-feedback-rows [f]
  (select f [:.feedback-row]))

(defn repaint-frame [f]
  (repaint! (select f [:*])))

; Functions for constructing the guess rows

(defn guess-square-border [col]
  (line-border :thickness 5 :color col))

(defn guess-square [w h]
  (canvas
    :background default-col
    :border (guess-square-border default-brdr-col)
    :size [w :by h]
    :paint nil
    :class :guess-square))

(defn guess-row [num-pegs row-num]
  (horizontal-panel
    :items (vec (repeatedly num-pegs #(guess-square 100 100)))
    :id (keyword (str "row-" row-num))
    :border 5
    :class :row))

(defn board [ncolors nturns]
  (scrollable
    (vertical-panel
      :items (vec (map #(guess-row ncolors %) (range nturns)))
      :border 5
      :id :rows)
    :vscroll :never
    :id :rows-scrl))

; Functions for constructing the feedback area

(defn feedback-square [col txt]
  (label
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
    :size [100 :by 110]
    :class :feedback-ctnr
    :north (make-widget [:fill-v 35])
    :south (make-widget [:fill-v 35])
    :east (make-widget [:fill-h 5])
    :west (make-widget [:fill-h 5])
    :center (feedback-row [0 0 0])))

(defn feedback-column [nturns]
  (scrollable
    (vertical-panel
      :items (vec (repeatedly nturns feedback-container))
      :border 5
      :id :feedback-col)
    :size [150 :by frame-height]
    :id :feedback-scrl))

; Function for constructing the right-most panel, where the user can control the board

(defn control-panel []
  (let [v-gap 20]
  (vertical-panel
    :items [[:fill-v v-gap]
            (horizontal-panel
              :size [200 :by 20]
              :items [:fill-h
                      (button :id :submit :text "Submit")
                      :fill-h])
            [:fill-v v-gap]
            (horizontal-panel
              :size [200 :by 20]
              :items [:fill-h
                      (button :id :help :text "Help")
                      :fill-h])]
    :size [200 :by 500])))

; Functions to construct dialog boxes

(defn show-instructions [_]
  (show!
    (dialog
      :title "Instructions"
      :content
        (str
          "Welcome to Mastermind!\n\n"
          "For rules, see https://en.wikipedia.org/wiki/Mastermind_(board_game)\n\n"
          "When the game is underway, the currently active row will appear\n"
          "highlighted in dark blue.\n"
          "Click on any square in the active row to choose a color\n"
          "for that square.\n"
        ` "When you have chosen a color for each square in the active row,\n"
          "click 'Submit' to submit your guess for evaluation.\n"
          "Feedback on your guess will appear in the form of three numbers\n"
          "next to the guess itself.\n"
          "The first number is the number of black key pegs in the feedback.\n"
          "The second number is the number of white key pegs in the feedback.\n"
          "The last number is the number of key peg holes that would be\n"
          "empty in the feedback.\n\n"
          "To see these instructions again at any point during the game,\n"
          "click on the 'Help' button.\n")
      :size [600 :by 400])))

(defn result-message [victory]
  (if victory "VICTORY!!!" "You. Lose."))

(defn end-game-dialog [victory]
  (show!
    (dialog
      :title "Play Again?"
      :content (str (result-message victory) "\n\n" "Would you like to play again?")
      :option-type :yes-no
      :size [400 :by 200])))

(defn show-thx-for-playing []
  (show!
    (dialog
      :content "Thanks for playing!"
      :size [200 :by 100])))

(defn add-listeners [f]
  (let [b-scrl (.getVerticalScrollBar (select f [:#rows-scrl]))
        f-scrl (.getVerticalScrollBar (select f [:#feedback-scrl]))
        adjust-f-scrl (fn [e] (.setValue f-scrl (.getValue b-scrl)))
        adjust-b-scrl (fn [e] (.setValue b-scrl (.getValue f-scrl)))]
    (listen b-scrl :mouse-dragged adjust-f-scrl)
    (listen
      (first (select b-scrl [:<javax.swing.JButton>]))
      :mouse-clicked adjust-f-scrl)
    (listen
      (second (select b-scrl [:<javax.swing.JButton>]))
      :mouse-clicked adjust-f-scrl)
    (listen
      (first (select f-scrl [:<javax.swing.JButton>]))
      :mouse-clicked adjust-b-scrl)
    (listen
      (second (select f-scrl [:<javax.swing.JButton>]))
      :mouse-clicked adjust-b-scrl)
    (listen f-scrl :mouse-dragged adjust-b-scrl)
    (listen (select f [:#help]) :mouse-clicked show-instructions)))

(defn mm-frame [ncolors nturns]
  (let [f (frame
            :title "Mastermind!"
            :height 500
            :width 800
            :content (horizontal-panel
                        :items [(board ncolors nturns)
                                (feedback-column nturns)
                                (control-panel)])
            :on-close :exit)]
    (add-listeners f)
    (show! f)))

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

(defn get-row-squares [row]
  (select row [:.row :> :JPanel]))

(defn chg-border-col [row col]
  (doall (map
          #(config! % :border (guess-square-border col))
          (get-row-squares row))))

(defn activate-row [row]
  (let [unlisten-fn (listen
                  (get-row-squares row)
                  :mouse-clicked (fn [e]
                                  (if-let [background (show! (color-chooser))]
                                    (config! e :background background))))
        unhighlight-border (fn []
                      (chg-border-col row default-brdr-col))]
    (chg-border-col row active-brdr-col)
    (fn []
      (unlisten-fn) (unhighlight-border))))

(defn get-submit-listener [row prom]
  (fn [e]
    (let [guess (map
                  #(colors-map (take 3 (get-rgba (config % :background))))
                  (get-row-squares row))]
      (if (not-any? #{default-col} guess)
        (deliver prom guess)))))

(defn activate-submit [b row prom]
  (listen (select b [:#submit]) :mouse-clicked (get-submit-listener row prom)))

(defn get-guess [row]
  (map #(colors-map (take 3 (get-rgba (config % :background)))) (get-row-squares row)))

(defn display-feedback [feedback feedback-row]
  (map #(text! %1 %2) (select feedback-row [:.feedback-square]) feedback))

(defn clear-board [f]
  (let [guess-squares (select f [:.guess-square])
        feedback-squares (select f [:.feedback-square])]
    (doall (map #(config! % :background default-col) guess-squares))
    (doall (map #(config! % :border (guess-square-border default-brdr-col)) guess-squares))
    (doall (map #(config! % :text 0) feedback-squares))))

(defn scroll-to-top [f]
  (do
    (scroll! (select f [:#rows]) :to :top)
    (scroll! (select f [:#feedback-col]) :to :top)))
