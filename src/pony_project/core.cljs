(ns pony-project.core
  (:require [pony-project.kaboom :as kaboom]
            [pony-project.const :as const]
            [pony-project.scenes.t-rex :as t-rex]
            [pony-project.scenes.levels :as levels]))

(kaboom/init
  {:params  {:global     true
             :fullscreen true
             :scale      3
             :debug      true
             :plugins    #js [js/asepritePlugin]
             :clearColor #js [0 0 0 1]}
   :sprites ["/resource/"
             ["pony" "applejack.png" "applejack.json"]
             ["barrel" "barrel.png"]
             ["ground1" "ground1.png"]
             ["tree" "tree1.png"]
             ["apple" "apple.png"]
             ["grass" "grass.png"]
             ["bg" "single_background.png"]]
   :scenes  {"t-rex"
             t-rex/scene
             "main"
             levels/level-1
             "game-over"
             {:objects [{:text   {:text "Game over" :size 34}
                         :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 2)])
                         :origin "center"}]}
             "win"
             {:objects [{:text   {:text "You win!" :size 34}
                         :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 2)])
                         :origin "center"}]}}})

(kaboom/k-start "main")


