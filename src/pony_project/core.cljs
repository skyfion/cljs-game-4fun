(ns pony-project.core
  (:require ["phaser" :as ph]))

(defonce state (atom {}))

(defn load-img [this name resource]
  (.image (.-load this) name resource))

(defn preload-fn []
  (this-as this
    (load-img this "bkg" "resource/single_background.png")
    (load-img this "ground" "resource/grass.png")
  ;  ^js (.animation (.-load this) "apple-jack-data" "resource/applejack.json")
  ;  ^js (.atlas (.-load this) "apple-jack" "resource/applejack.png" "resource/applejack.json")
    (load-img this "tiles" "resource/tileset_summer_1.png")
    ^js (.tilemapTiledJSON (.-load this) "map" "resource/level_1.json")
    ^js (.aseprite (.-load this) "apple-jack" "resource/applejack.png" "resource/applejack.json")
    ))

(defn create-fn []
  (this-as this
    (-> (.-add this)
        (.image 0 0 "bkg")
        (.setOrigin 0 0)
        (.setScale 2))

    ;(let [group (-> (.-physics this)
    ;                (.-add)
    ;                (.staticGroup)
    ;                (.create 400 500 "ground")
    ;                ; (.setScale 2)
    ;                (.refreshBody))]
    ;
    ;  )

    (let [player (-> (.-physics this)
                     (.-add)
                     (.sprite 100 100 "apple-jack")
                     (.setScale 0.8)
                    ; (.setBounce 0.5)
                     (.setCollideWorldBounds true))
          level (-> (.-make this)
                    (.tilemap #js {"key" "map"}))
          tileset (-> level
                      (.addTilesetImage "tileset_summer_1" "tiles"))
          layer (-> (.createLayer level "Tile Layer 1" tileset 0 0)
                    (.setCollisionByExclusion -1 true))
          cursors (-> (.-input this)
                      (.-keyboard)
                      (.createCursorKeys))
          tags (-> (.createFromAseprite (.-anims this) "apple-jack"))]
      (-> (.-physics this)
          (.-add)
          (.collider player layer))


      (reset! state {:cursors cursors
                     :player  player})
      )

    ))

(defn update-fn []
  (this-as this
    (let [{:keys [cursors player]} @state]
      (cond
        (.-isDown (.-left cursors))
        (do
          (.setVelocityX player -200)
          (.setFlipX player true)
          (when (.onFloor (.-body player))
            (.play player "move" true)))

        (.-isDown (.-right cursors))
        (do
          (.setVelocityX player 200)
          (.setFlipX player false)
          (when (.onFloor (.-body player))
            (.play player "move" true)))

        :else
        (do
          (when (.onFloor (.-body player))
            (.play player "idle"))
          (.setVelocityX player 0))
        )
      (when
        (and (.-isDown (.-space cursors)) (.onFloor (.-body player)))

        (.setVelocityY player -350)
        (.play player "jump" true))
      )
    ))

(def config {:type    (.-AUTO ph)
             :physics {:default "arcade"
                       :arcade  {:gravity {:y 800}
                                 :debug   true}}
             ;:width 800
             ;:height 800
             :scene   {:preload preload-fn
                       :create  create-fn
                       :update  update-fn}})

(defonce game (ph/Game. (clj->js config)))







