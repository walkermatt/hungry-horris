(ns hungry-horris.core
  (:require [cljs.core.async :refer [chan put! dropping-buffer sliding-buffer]]
            [goog.dom :as dom]
            [goog.style :as style]
            [goog.fx.dom :as fx-dom]
            [goog.math :as math]
            [goog.dom.classes :as classes]
            [goog.events :as events])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn listen [ch el type]
  (events/listen
    el type (fn [e] (put! ch e)))
  ch)

(defn set-interval [ch n]
  (.setInterval js/window (fn [id] (put! ch :tick)) n) ch)

(def state (atom {:eating false :mouse [0 0]}))

(def horris (dom/createDom "div" (clj->js {:id "horris"}) " "))
(dom/append js/document.body horris)

(let [ch (listen (chan (sliding-buffer 1)) js/document.body "mousemove")
      bounds (style/getBounds horris)]
  (go (while true (let [e (<! ch)
                        x (- (.-clientX e) (/ (.-width bounds) 2))
                        y (- (.-clientY e) (/ (.-height bounds) 2))]
                    (swap! state update-in [:mouse] (fn [v] [x y]))))))

(let [tick-ch (set-interval (chan (dropping-buffer 1)) 200)
      bounds (style/getBounds horris)]
  (go (while true
        (let [_ (<! tick-ch)]
          (.play (fx-dom/SlideFrom. horris (clj->js (:mouse @state)) 200))
          (let [eating (.contains (style/getBounds horris) (math/Coordinate. (first (:mouse @state)) (second (:mouse @state))))
                sound (dom/$ "eating")]
            (classes/enable horris "eating" eating)
            (if (not= (:eating @state) eating) (if eating (do (.load sound) (.play sound))))
            (swap! state update-in [:eating] (fn [b] eating)))))))

; (defn request-ani-frame [ch]
;   (.requestAnimationFrame js/window (fn [id]
;                                       ; (.log js/console id)
;                                       (put! ch id)
;                                       (recur ch))))

; (let [ch (request-ani-frame (chan))]
;   (go (while true
;         (let [id (<! ch)]
;           (.log js/console id)))))
