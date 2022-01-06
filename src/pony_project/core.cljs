(ns pony-project.core
  (:require ["phaser" :as ph]))


(defn preload []
  )

(def config {:type (.-AUTO ph)
             :width 800
             :height 800
             :scene {:preload preload}})

(def game (ph/Game. (clj->js config)))





