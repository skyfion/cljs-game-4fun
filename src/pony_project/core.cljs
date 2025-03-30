(ns pony-project.core
  (:require [pony-project.kaboom :as kaboom]
            [pony-project.scenes.t-rex :as t-rex]
            [pony-project.scenes.levels :as levels]
            [pony-project.scenes.start :as start]))

(def first-level [:start])

(js/console.log "Initializing game with first level:" (pr-str first-level))

(kaboom/init
  {:params  {:global     true
             :fullscreen true
             :scale      3
             :debug      false
             :plugins    [js/asepritePlugin]
             :clearColor [0 0 0 1]}
   :sprites ["resource/"
             ["pony" "applejack.png" "applejack.json"]
             ;["bat" "bat-sheet.png" "bat.json"]
             ["barrel" "barrel.png"]
             ["ground1" "ground1.png"]
             ["tree" "tree1.png"]
             ["apple" "apple.png"]
             ["grass" "grass.png"]
             ["bg" "single_background.png"]]
   :scenes  {:start
             start/scene
             :t-rex
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
                                      (kaboom/k-go :start))))}
             :win
             {:objects [{:text   {:text "You win!" :size 34}
                         :pos    (fn [k] [(/ (.width k) 2) (/ (.height k) 2)])
                         :origin "center"}
                        {:text   {:text "press space to return to menu..." :size 8}
                         :origin "center"
                         :pos    (fn [k] [(/ (.width k) 2) (+ (/ (.height k) 2) 30)])}]
              :init-fn (fn [k _]
                         (.keyPress k #js ["space"]
                                    (fn []
                                      (reset! kaboom/state {})
                                      (kaboom/k-go :start))))}}})


(js/console.log "Starting game with scene:" (pr-str first-level))
;; first-level is [:start], so this becomes (kaboom/k-start :start)
(let [scene-id (first first-level)]
  (js/console.log "Using scene-id:" (pr-str scene-id))
  (kaboom/k-start scene-id))
(js/console.log "Game started!")

