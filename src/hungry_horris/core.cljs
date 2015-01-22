(ns hungry-horris.core
  (:require [goog.dom :as dom]
            [goog.style :as style]
            [goog.fx.dom :as fx-dom]
            [goog.math :as math]
            [goog.dom.classes :as classes]
            [goog.events :as events]))

(enable-console-print!)

; Define horris's state
(def state (atom {:eating {:now false :then false} :mouse [0 0]}))

; Create horris and add him to the page
(def horris (dom/createDom "div" (clj->js {:id "horris"}) " "))
(dom/append js/document.body horris)

(defn mouse-position
  "Extract client mouse position from mouse event object as a vector [x y]"
  [e]
  [(.-clientX e) (.-clientY e)])

(defn center-on-coord
  "Subtract half the width and height of the element from the coord"
  [elm coord]
  (let [bounds (style/getBounds elm)]
    [(- (first coord) (/ (.-width bounds) 2))
     (- (second coord) (/ (.-height bounds) 2))]))

(defn eating?
  "Determine if horris is eating the mouse"
  [horris coord]
  (.contains
    (style/getBounds horris)
    (math/Coordinate. (first coord) (second coord))))

(defn render
  "Render the view based on current state"
  [horris state]
  (let [mouse (:mouse @state)
        eating (:eating @state)
        sound (dom/$ "eating")]
    (.play (fx-dom/SlideFrom. horris (clj->js (center-on-coord horris mouse)) 400))
    (classes/enable horris "eating" (:now eating))
    (if (not= (:was eating) (:now eating)) (if (:now eating) (do (.load sound) (.play sound))))))

; Record mouse position
(events/listen
  js/document.body
  "mousemove"
  (fn [e] (let [mouse (mouse-position e)]
            ; (print (clj->js mouse))
            (swap! state update-in [:mouse] (fn [v] mouse)))))

; Determine if horris is eating and render the view
(.setInterval
  js/window
  (fn [id]
    (swap! state update-in [:eating] (fn [b] {:was (:now (:eating @state)) :now (eating? horris (:mouse @state))}))
    (render horris state))
  300)
