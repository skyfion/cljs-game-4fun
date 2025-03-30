(ns pony-project.scenes.start
  (:require [pony-project.kaboom :as kaboom]
            [pony-project.const :as const]))

(defn start-game []
  (js/console.log "Starting game from button or key press")
  (reset! kaboom/state {})
  (kaboom/k-go :main))

(def scene
  (let []
    {:layers     [["bg" "ui"] "ui"]
     :cam-ignore ["ui" "bg"]
     :gravity    0
     :objects    [{:sprite "bg"
                   :scale  (fn [ka] [(/ (.width ka) 512) (/ (.height ka) 303)])
                   :layer  "bg"
                   :origin "topleft"
                   :color  [100 0 150 1]}

                  {:rect   [800 200]
                   :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 3)])
                   :origin "center"
                   :color  [50 0 80 1]}

                  {:text   {:text "PONY ADVENTURE" :size 15}
                   :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 3)])
                   :origin "center"
                   :color  [255 255 255 1]}

                  {:sprite "pony"
                   :layer  "ui"
                   :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 3) 70)])
                   :origin "center"
                   :scale  0.6}

                  {:sprite "apple"
                   :layer  "ui"
                   :pos    (fn [k] [(- (/ (.width k) 2) 120) (+ (/ (.height k) 3) 70)])
                   :origin "center"
                   :scale  1}

                  {:sprite "apple"
                   :layer  "ui"
                   :pos    (fn [k] [(+ (/ (.width k) 2) 120) (+ (/ (.height k) 3) 70)])
                   :origin "center"
                   :scale  1}

                  ;; Button background with hover effect - clickable area
                  {:obj-id :start-button
                   :rect   [300 60]
                   :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 3) 100)])
                   :origin "center"
                   :color  [100 100 0 1]
                   :area   [[150 30] [150 30]]              ;; Make it clickable with proper dimensions
                   :params {:normal-color [100 100 0 1]
                            :hover-color  [150 150 0 1]}}

                  ;; Button border/outline (decorative)
                  {:rect   [310 70]
                   :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 3) 100)])
                   :origin "center"
                   :color  [180 180 0 0.5]}

                  ;; Button text
                  {:obj-id :start-text
                   :text   {:text "START GAME" :size 24}
                   :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 3) 100)])
                   :origin "center"
                   :color  [255 255 255 1]}

                  ;; Space key prompt
                  {:text   {:text "or press SPACE key" :size 14}
                   :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 3) 140)])
                   :origin "center"
                   :color  [200 200 200 1]}

                  {:text   {:text "Controls: Arrow keys to move, SPACE to jump" :size 14}
                   :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 3) 120)])
                   :origin "center"
                   :color  [200 200 200 1]}]

     :scene-init (fn []
                   (js/console.log "Start scene initialized!"))

     :init-fn    (fn [k state]
                   (js/console.log "Start scene loaded!")

                   ;; Space key to start
                   (.keyPress k #js ["space"] start-game)

                   ;; Start button hover and click handling
                   (when-let [button (:start-button state)]
                     ;; Define a variable to track hover state
                     (let [hover-state (atom false)]
                       (js/console.log (type button) button)
                       ;; Handle hover effects
                       #_(.onHover button
                                   (fn []
                                     (reset! hover-state true)
                                     ;; Change button color
                                     (set! (.-color button) (clj->js (.-hover-color (.-params button))))
                                     ;; Slightly scale up the button when hovering
                                     (set! (.-scale button) (clj->js {:x 1.05 :y 1.05}))
                                     ;; Change text color if available
                                     (when-let [text (:start-text state)]
                                       (set! (.-color text) (clj->js [255 255 100 1]))))

                                   (fn []
                                     (reset! hover-state false)
                                     ;; Restore button color
                                     (set! (.-color button) (clj->js (.-normal-color (.-params button))))
                                     ;; Restore original scale
                                     (set! (.-scale button) (clj->js {:x 1 :y 1}))
                                     ;; Restore text color if available
                                     (when-let [text (:start-text state)]
                                       (set! (.-color text) (clj->js [255 255 255 1])))))

                       ;; Handle click with visual feedback
                       #_(.onClick button
                                   (fn []
                                     ;; Visual feedback - briefly scale down when clicked
                                     (set! (.-scale button) (clj->js {:x 0.95 :y 0.95}))
                                     ;; Add a small delay before starting the game
                                     (.wait k 0.1 start-game)))

                       ;; Add a subtle pulse animation to the button when not hovered
                       #_(.action k
                                  (fn []
                                    (when-not @hover-state
                                      (let [t (.time k)
                                            s (+ 1.0 (* 0.02 (.sin k (* t 4))))]
                                        (set! (.-scale button) (clj->js {:x s :y s})))))))))}))