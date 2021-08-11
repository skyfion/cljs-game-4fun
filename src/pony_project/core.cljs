(ns pony-project.core
  (:require [pony-project.kaboom :as kaboom]
            [pony-project.scenes.t-rex :as t-rex]
            [pony-project.scenes.levels :as levels]))

(def first-level [:t-rex])

(kaboom/init
  {:params  {:global     true
             :fullscreen true
             :scale      3
             :debug      true
             :plugins    [js/asepritePlugin]
             :clearColor [0 0 0 1]}
   :sprites ["resource/"
             ["pony" "applejack.png" "applejack.json"]
             ["bat" "bat-sheet.png" "bat.json"]
             ["barrel" "barrel.png"]
             ["ground1" "ground1.png"]
             ["tree" "tree1.png"]
             ["apple" "apple.png"]
             ["grass" "grass.png"]
             ["bg" "single_background.png"]]
   :scenes  {:t-rex
             t-rex/scene
             :main
             levels/level-1
             :game-over
             {:objects [{:text   {:text "Game over" :size 34}
                         :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 2)])
                         :origin "center"}
                        {:text   {:text "press space to restart..." :size 8}
                         :origin "center"
                         :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 2) 30)])}]
              :init-fn (fn [k _]
                         (.keyPress k #js ["space"]
                                    (fn []
                                      (reset! kaboom/state {})
                                      (apply kaboom/k-go first-level))))}
             :win
             {:objects [{:text   {:text "You win!" :size 34}
                         :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 2)])
                         :origin "center"}]}}})


(apply kaboom/k-start first-level)



