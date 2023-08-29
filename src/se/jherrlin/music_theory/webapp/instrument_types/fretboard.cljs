(ns se.jherrlin.music-theory.webapp.instrument-types.fretboard
  (:require
   [re-frame.core :as re-frame]))


(def matrix
  [[{:x 0, :tone #{:e}, :y 0, :out "E"}
  {:x 1, :tone #{:f}, :y 0}
  {:x 2, :tone #{:gb :f#}, :y 0}
  {:x 3, :tone #{:g}, :y 0, :out "G"}
  {:x 4, :tone #{:g# :ab}, :y 0}
  {:x 5, :tone #{:a}, :y 0}
  {:x 6, :tone #{:bb :a#}, :y 0}
  {:x 7, :tone #{:b}, :y 0, :out "B"}
  {:x 8, :tone #{:c}, :y 0}
  {:x 9, :tone #{:db :c#}, :y 0}
  {:x 10, :tone #{:d}, :y 0}
  {:x 11, :tone #{:d# :eb}, :y 0}
  {:x 12, :tone #{:e}, :y 0, :out "E"}
  {:x 13, :tone #{:f}, :y 0}
  {:x 14, :tone #{:gb :f#}, :y 0}
  {:x 15, :tone #{:g}, :y 0, :out "G"}]
 [{:x 0, :tone #{:b}, :y 1, :out "B"}
  {:x 1, :tone #{:c}, :y 1}
  {:x 2, :tone #{:db :c#}, :y 1}
  {:x 3, :tone #{:d}, :y 1}
  {:x 4, :tone #{:d# :eb}, :y 1}
  {:x 5, :tone #{:e}, :y 1, :out "E"}
  {:x 6, :tone #{:f}, :y 1}
  {:x 7, :tone #{:gb :f#}, :y 1}
  {:x 8, :tone #{:g}, :y 1, :out "G"}
  {:x 9, :tone #{:g# :ab}, :y 1}
  {:x 10, :tone #{:a}, :y 1}
  {:x 11, :tone #{:bb :a#}, :y 1}
  {:x 12, :tone #{:b}, :y 1, :out "B"}
  {:x 13, :tone #{:c}, :y 1}
  {:x 14, :tone #{:db :c#}, :y 1}
  {:x 15, :tone #{:d}, :y 1}]
 [{:x 0, :tone #{:g}, :y 2, :out "G"}
  {:x 1, :tone #{:g# :ab}, :y 2}
  {:x 2, :tone #{:a}, :y 2}
  {:x 3, :tone #{:bb :a#}, :y 2}
  {:x 4, :tone #{:b}, :y 2, :out "B"}
  {:x 5, :tone #{:c}, :y 2}
  {:x 6, :tone #{:db :c#}, :y 2}
  {:x 7, :tone #{:d}, :y 2}
  {:x 8, :tone #{:d# :eb}, :y 2}
  {:x 9, :tone #{:e}, :y 2, :out "E"}
  {:x 10, :tone #{:f}, :y 2}
  {:x 11, :tone #{:gb :f#}, :y 2}
  {:x 12, :tone #{:g}, :y 2, :out "G"}
  {:x 13, :tone #{:g# :ab}, :y 2}
  {:x 14, :tone #{:a}, :y 2}
  {:x 15, :tone #{:bb :a#}, :y 2}]
 [{:x 0, :tone #{:d}, :y 3}
  {:x 1, :tone #{:d# :eb}, :y 3}
  {:x 2, :tone #{:e}, :y 3, :out "E"}
  {:x 3, :tone #{:f}, :y 3}
  {:x 4, :tone #{:gb :f#}, :y 3}
  {:x 5, :tone #{:g}, :y 3, :out "G"}
  {:x 6, :tone #{:g# :ab}, :y 3}
  {:x 7, :tone #{:a}, :y 3}
  {:x 8, :tone #{:bb :a#}, :y 3}
  {:x 9, :tone #{:b}, :y 3, :out "B"}
  {:x 10, :tone #{:c}, :y 3}
  {:x 11, :tone #{:db :c#}, :y 3}
  {:x 12, :tone #{:d}, :y 3}
  {:x 13, :tone #{:d# :eb}, :y 3}
  {:x 14, :tone #{:e}, :y 3, :out "E"}
  {:x 15, :tone #{:f}, :y 3}]
 [{:x 0, :tone #{:a}, :y 4}
  {:x 1, :tone #{:bb :a#}, :y 4}
  {:x 2, :tone #{:b}, :y 4, :out "B"}
  {:x 3, :tone #{:c}, :y 4}
  {:x 4, :tone #{:db :c#}, :y 4}
  {:x 5, :tone #{:d}, :y 4}
  {:x 6, :tone #{:d# :eb}, :y 4}
  {:x 7, :tone #{:e}, :y 4, :out "E"}
  {:x 8, :tone #{:f}, :y 4}
  {:x 9, :tone #{:gb :f#}, :y 4}
  {:x 10, :tone #{:g}, :y 4, :out "G"}
  {:x 11, :tone #{:g# :ab}, :y 4}
  {:x 12, :tone #{:a}, :y 4}
  {:x 13, :tone #{:bb :a#}, :y 4}
  {:x 14, :tone #{:b}, :y 4, :out "B"}
  {:x 15, :tone #{:c}, :y 4}]
 [{:x 0, :tone #{:e}, :y 5, :out "E"}
  {:x 1, :tone #{:f}, :y 5}
  {:x 2, :tone #{:gb :f#}, :y 5}
  {:x 3, :tone #{:g}, :y 5, :out "G"}
  {:x 4, :tone #{:g# :ab}, :y 5}
  {:x 5, :tone #{:a}, :y 5}
  {:x 6, :tone #{:bb :a#}, :y 5}
  {:x 7, :tone #{:b}, :y 5, :out "B"}
  {:x 8, :tone #{:c}, :y 5}
  {:x 9, :tone #{:db :c#}, :y 5}
  {:x 10, :tone #{:d}, :y 5}
  {:x 11, :tone #{:d# :eb}, :y 5}
  {:x 12, :tone #{:e}, :y 5, :out "E"}
  {:x 13, :tone #{:f}, :y 5}
  {:x 14, :tone #{:gb :f#}, :y 5}
  {:x 15, :tone #{:g}, :y 5, :out "G"}]])

(defn fret-number [n]
  [:div {:style {:width           "5rem"
                 :min-width       "5rem"
                 :height          "2rem"
                 :display         "flex"
                 :justify-content :center}}
   n])

(defn fret
  [{:keys [on-click circle-text background-color fret-color y]
    :or   {y                0
           background-color "#000000d6"
           fret-color       "linear-gradient(to right, #FFFFFF , #706e68)"}}]
  (let [string-color  "linear-gradient(#737270 , #b9bab3, #737270)"
        string-height (str (+ 0.2 y) "rem")]
    [:div {:on-click on-click
           :style    {:width          "5rem"
                      :height         "3rem"
                      :display        "flex"
                      :flex-direction "row"}}
     [:div {:style {:background-color background-color
                    :width            "4.5rem"
                    :height           "100%"
                    :justify-content  :center
                    :display          "flex"
                    :flex-direction   "column"}}
      [:div {:style {:display          "flex"
                     :align-items      :center
                     :justify-content  :center
                     :background-image string-color
                     :height           string-height
                     :width            "5rem"
                     :z-index          100}}
       (when circle-text
         [:div {:style {:display          "flex"
                        :align-items      :center
                        :justify-content  :center
                        :height           "2rem"
                        :width            "2rem"
                        :background-color "orange"
                        :border-radius    "50%"
                        :z-index          0}}
          circle-text])]]

     [:div {:style {:background-image fret-color
                    :z-index          50
                    :width            "0.5rem"
                    :height           "100%"}}]]))

(defn styled-view [matrix]
  (let [min-x (->> matrix first (map :x) (apply min))
        max-x (->> matrix first (map :x) (apply max))]
    [:div
     [:div {:style {:overflow-x "auto"}}
      [:div {:style {:display :flex}}
       (for [idx (range 16)]
         ^{:key (str "tone-" idx)}
         [fret-number idx])]

      [:div {:style {:display :flex}}
       (for [idx (range 16)]
         ^{:key (str "tone-" idx)}
         [fret
          {:on-click identity}])]]


     [:div {:style {:overflow-x "auto"}}
      [:div {:style {:display "flex"}}
       (for [{:keys [x]} (-> matrix first)]
         ^{:key (str "fretboard-fret-" x)}
         [fret-number x])]
      (for [[idx fretboard-string] (map-indexed vector matrix)]
        ^{:key (str "fretboard-string-" idx)}
        [:div {:style {:display "flex"}}
         (for [{:keys [x y tone out] :as m} fretboard-string]
           ^{:key (str "fretboard-string-" x "-" y)}
           [fret
            {:y                (/ y 10)
             :on-click         #(js/console.log m)
             :circle-text      (when out out)
             :background-color (if (= x 0)
                                 "white"
                                 "#000000d6")
             :fret-color       (cond
                                 (= x min-x) "linear-gradient(black, black, black)"
                                 (= x max-x) "linear-gradient(#000000d6, #000000d6, #000000d6)"
                                 :else
                                 "linear-gradient(to right, #FFFFFF , #706e68)")}])])]]))

(def routes
  (let [path-name :a/a]
    ["/debug/styled-fretboard"
     {:name path-name
      :view [styled-view matrix]}]))
