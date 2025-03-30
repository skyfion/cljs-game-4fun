(ns pony-project.kaboom
  (:require ["kaboom"]
            [clojure.core.match :refer [match]]))

(defonce kaboom (atom nil))
(defonce state (atom {}))

(defn k-start
  ([id] 
   (js/console.log "k-start called with id:" (name id))
   (.start @kaboom (name id)))
  ([id n] 
   (js/console.log "k-start called with id and n:" (name id) n)
   (.start @kaboom (name id) n))
  ([] 
   (js/console.log "k-start called with no args")
   (.start @kaboom)))

(defn k-load-root [root] (when root (.loadRoot @kaboom root)))

(defn k-load-aseprite [id res json] ^js (.loadAseprite @kaboom id res json))

(defn k-load-sprite [id res] (.loadSprite @kaboom id res))

(defn k-layers [layers default]
  (.layers @kaboom (clj->js layers) default))

(defn k-cam-ignore [p]
  (.camIgnore @kaboom (clj->js p)))

(defn k-scale [s]
  (cond
    (vector? s) (let [[a b] s] (.scale @kaboom a b))
    (fn? s) (k-scale (s @kaboom))
    :default (.scale @kaboom s)))

(defn k-body [body]
  (.body @kaboom (clj->js body)))

(defn k-pos [p]
  (if (fn? p)
    (k-pos (p @kaboom))
    (let [[a b] p]
      (.pos @kaboom a b))))

(defn k-rect [p]
  (if (fn? p)
    (k-rect (p @kaboom))
    (let [[w h] p]
      (.rect @kaboom w h))))

(defn k-text [{:keys [text size]}]
  (.text @kaboom text size))

(defn k-vec2 [x y] (.vec2 @kaboom x y))

(defn k-key-down [e f]
  (.keyDown @kaboom (if (vector? e) (clj->js e) e) f))

(defn k-sprite [sprite]
  (when sprite
    (cond
      (map? sprite)
      (.sprite @kaboom (:id sprite) (clj->js (dissoc sprite :id)))
      :default
      (.sprite @kaboom (name sprite)))))

(defn k-level [level params]
  (.addLevel @kaboom (clj->js params)))

(defn k-destroy [obj] (.destroy @kaboom obj))

(defn k-cam-pos [pos]
  (.camPos @kaboom pos))

(defn k-action
  ([f] (k-action nil f))
  ([tag f]
   (if tag
     (.action @kaboom tag (partial f @kaboom))
     (.action @kaboom (partial f @kaboom)))))

(defn k-loop [n f]
  (.loop @kaboom n f))

(defn k-rand [a b]
  (.rand @kaboom a b))

(defn k-origin [origin]
  (when origin
    (.origin
      @kaboom
      (cond
        (vector? origin) (k-vec2 (get origin 0) (get origin 1))
        :default (name origin)))))

(defn k-go
  ([id a] (.go @kaboom (name id) a))
  ([id] (.go @kaboom (name id))))

(defn k-color [c]
  (when c
    (let [f
          (.color
            @kaboom
            (match
              c
              [r g b a]
              (.rgba @kaboom r g b a)
              [r g b]
              (.rgb @kaboom r g b)
              :else (clj->js c)))]
      (js/console.log f)
      f)))

(defn map->obj [{:keys [sprite scale layer origin body pos params
                        solid? rect text area color tag play]}]
  (clj->js
    (vec
      (filter
        identity
        [(k-sprite sprite)
         (when text (k-text text))
         (when scale (k-scale scale))
         (when layer (.layer @kaboom (name layer)))
         (k-origin origin)
         (when pos (k-pos pos))
         (when body (k-body body))
         (when rect (k-rect rect))
         (when-let [[x y] area]
           (.area @kaboom (k-vec2 (first x) (second x))
                  (k-vec2 (first y) (second y))))
         (when solid? (.solid @kaboom))
         (k-color color)
         (when params (clj->js params))
         (when play (.play @kaboom (name play)))
         tag]))))

(defn add-obj [obj]
  (.add @kaboom (map->obj obj)))

(defn add-level [level params]
  (.addLevel
    @kaboom
    (clj->js level)
    (clj->js
      (into
        {}
        (map
          (fn [[k v]]
            (if (and (string? k) (map? v))
              [k (map->obj v)]
              [k v])) params)))))

(defn map->level
  [id {:keys [layers cam-ignore objects gravity init-fn scene-init]}]
  (when-let [[all d] layers]
    (k-layers all d))
  (when cam-ignore (k-cam-ignore cam-ignore))
  (when gravity (.gravity @kaboom gravity))

  (when scene-init
    (scene-init @kaboom))

  ; add objects in state
  (doseq [{:keys [obj-id] :as param} objects]
    (let [obj (add-obj param)]
      (when id
        (swap! state assoc-in [id obj-id] obj))))

  (when init-fn

    (init-fn @kaboom (get @state id))))

(defn init [{:keys [params sprites scenes]}]
  (reset! kaboom (js/kaboom (clj->js params)))
  (doseq [s sprites]
    (match s
           [id res json] (k-load-aseprite id res json)
           [id res] (k-load-sprite id res)
           :else (k-load-root s)))

  (doseq [[id level] scenes]
    (.scene
      @kaboom (name id)
      (cond
        (map? level) #(map->level id level)
        (fn? level) #(map->level id (level %))
        :else (throw (js/Error. "something went wrong...")))))
  @kaboom)
