(ns pony-project.scenes.t-rex
  (:require [pony-project.kaboom :as kaboom]
            [pony-project.const :as const]))

(def scene
  {:layers     [["bg" "game" "ui"] "game"]
   :cam-ignore ["ui" "bg"]
   :gravity    (- const/gravity 200)
   :objects    [{:sprite "bg"
                 :scale  (fn [ka] [(/ (.width ka) 512) (/ (.height ka) 303)])
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
                {:rect   (fn [ka] [(.width ka) 20])
                 :solid? true
                 :pos    (fn [ka] [0 (/ (* (.height ka) 80) 100)])}
                {:obj-id :player
                 :sprite "pony"
                 :layer  "game"
                 :pos    [100 100]
                 :area   [[-17 -39] [40 44]]
                 :body   {:jumpForce 330}
                 :origin "center"
                 :tag    "pony"
                 :scale  0.3
                 :params {:speed 100}}]
   :scene-init (fn []
                 (let [move-fn
                       (fn [k obj]
                         (when obj
                           (.move obj -100 0)
                           (when (< (.-x (.-pos obj)) (/ (* -1 (.width k)) 2))
                             (.destroy k obj))))]
                   (kaboom/k-action "barrel" move-fn)
                   (kaboom/k-action "tree" move-fn)
                   (kaboom/k-action "apple" move-fn))

                 (kaboom/k-loop
                   1
                   (fn []

                     (when (rand-nth [true false])
                       (kaboom/add-obj
                         {:pos    (fn [k] [(.width @kaboom/kaboom)
                                           (/ (* (.height k) 80) 100)])
                          :sprite "barrel"
                          :solid? true
                          :layer  "game"
                          :tag    "barrel"
                          :origin "bot"}))

                     (when (rand-nth [true false])
                       (kaboom/add-obj
                         {:pos    (fn [k] [(+ (.width @kaboom/kaboom) 100)
                                           (/ (* (.height k) 81) 100)])
                          :sprite "tree"
                          :layer  "bg"
                          :tag    "tree"
                          :origin "bot"}))

                     (when (rand-nth [true false])
                       (kaboom/add-obj
                         {:pos    (fn [k] [(+ (.width @kaboom/kaboom) 50)
                                           (/ (* (.height k) 70) 100)])
                          :sprite "apple"
                          :layer  "game"
                          :tag    "apple"
                          }))

                     )))

   :init-fn    (fn [k state]

                 (kaboom/k-action
                   #(.camPos % (kaboom/k-vec2 (/ (.width %) 2) (/ (.height %) 2))))



                 (when-let [player (:player state)]

                   (.play player "move")

                   (.collides
                     player
                     "apple"
                     #(when %
                        (when-let [score (:score state)]
                          (->>
                            (swap! kaboom/state update :score inc)
                            (:score)
                            (set! (.-text score))))
                        (kaboom/k-destroy %)))

                   (.collides
                     player
                     "barrel"
                     #(when % (kaboom/k-go "game-over")))


                   (.keyPress k #js ["up" "space"]
                              (fn []
                                (when ^js (.grounded player)
                                  ^js (.jump player (.-jumpForce player))
                                  (.play player "jump"))))

                   (.on player "grounded" #(.play player "move"))

                   (.action player #(.camPos k (.-pos player)))))})