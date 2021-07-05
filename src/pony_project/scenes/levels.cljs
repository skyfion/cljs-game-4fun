(ns pony-project.scenes.levels
  (:require [pony-project.kaboom :as kaboom]
            [pony-project.const :as const]))

(def level-1
  {:layers     [["bg" "game" "ui"] "game"]
   :cam-ignore ["ui" "bg"]
   :gravity    const/gravity
   :objects    [{:sprite "bg"
                 :scale  (fn [ka] #js [(/ (.width ka) 512)
                                       (/ (.height ka) 303)])
                 :layer  "bg"
                 :origin "topleft"}
                {:layer  "ui"
                 :pos    [8 6]
                 :sprite "apple"
                 :scale  0.8}
                {:obj-id :score
                 :text   {:text "0"
                          :size 12}
                 :layer  "ui"
                 :pos    [25 9]}
                {:obj-id :player
                 :sprite "pony"
                 :layer  "game"
                 :pos    [100 0]
                 :area   [[-17 -39] [40 44]]
                 :body   {:jumpForce 350}
                 :origin "center"
                 ;  :solid? true
                 :scale  (fn [] 0.3)
                 :params {:speed 100}}]
   :scene-init (fn [_]
                 (kaboom/add-level
                   [
                    " B                                             a  t              "
                    "0                                              =              "
                    "                                                              "
                    "                    a    a      a             a                   "
                    "                                                                                       a          aaaaaaa    aaaa      aa  a a a a aa  "
                    "              t         B    a    t   a      B               a                    t      t               t   t                 t         t "
                    "=     =    =    =    =     =      =     =        =     =    =         =          =      =     =      =        =       =     =     =    =   ="
                    ]
                   {:width  11
                    :height 11
                    :pos    (kaboom/k-vec2 0 0)

                    "t"     {:layer  "game"
                             :origin [0 0.75]
                             :sprite "tree"}
                    "="     {:layer  "game"
                             :sprite "ground1"
                             :origin "topleft"
                             :solid? true}

                    "0"     {:layer  "game"
                             :sprite "ground1"
                             :origin "topleft"
                             :solid? true}

                    ;"B" #js [(.layer kaboom "game")
                    ;         (.sprite kaboom "barrel")
                    ;         (.origin kaboom "center")
                    ;         (.solid kaboom)]
                    ;"a" #js [(.layer kaboom "game")
                    ;         (.sprite kaboom "apple")
                    ;         "apple"
                    ;         (.scale kaboom 0.6)
                    ;         (.origin kaboom "center")]

                    }))
   :init-fn    (fn [_ state]
                 (when-let [player (:player state)]

                   (.collides
                     player
                     "apple"
                     #(when %
                        (when-let [score (get state :score 0)]
                          (->>
                            (swap! kaboom/state update :score inc)
                            (:score)
                            (set! (.-text score)))
                          ; todo
                          (when (= (:score @state) 28)
                            (kaboom/k-go "win")))
                        (kaboom/k-destroy %)))

                   (.action player
                            #(when
                               (>= (.-y (.-pos player)) (.height @kaboom/kaboom))
                               (.go @kaboom/kaboom "game-over")))

                   (kaboom/k-key-down ["left" "right"]
                                      #(when (and ^js (.grounded player)
                                                  (not= (.curAnim player) "move"))

                                         (.play player "move")))

                   (kaboom/k-key-down "left"
                                      (fn []
                                        (.flipX player -1)
                                        (.move player (* -1 (.-speed player)) 0)))

                   (kaboom/k-key-down "right"
                                      (fn []
                                        (.flipX player 1)
                                        (.move player (.-speed player) 0)))

                   (.keyRelease @kaboom/kaboom #js ["left" "right"]
                                #(if (and (not (.keyIsDown @kaboom/kaboom "right"))
                                          (not (.keyIsDown @kaboom/kaboom "left"))
                                          ^js (.grounded player))
                                   (.play player "idle")))

                   (.keyPress @kaboom/kaboom #js ["up" "space"]
                              (fn []
                                (when ^js (.grounded player)
                                  ^js (.jump player (.-jumpForce player))
                                  (.play player "jump"))))

                   (.on player "grounded" #(.play player "idle"))


                   ;  ^js (.areaHeight player 20)
                   (.action player #(kaboom/k-cam-pos (.-pos player)))

                   ))})