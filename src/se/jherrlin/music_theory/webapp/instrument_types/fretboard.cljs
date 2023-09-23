(ns se.jherrlin.music-theory.webapp.instrument-types.fretboard
  (:require
   [re-frame.core :as re-frame]))


(def matrix
  [[{:x 0, :tone #{:e}, :y 0, :out "1", :root? true}
    {:x 1, :tone #{:f}, :y 0}
    {:x 2, :tone #{:gb :f#}, :y 0}
    {:x 3, :tone #{:g}, :y 0, :out "5"}
    {:x 4, :tone #{:g# :ab}, :y 0}
    {:x 5, :tone #{:a}, :y 0}
    {:x 6, :tone #{:bb :a#}, :y 0}
    {:x 7, :tone #{:b}, :y 0, :out "b3"}
    {:x 8, :tone #{:c}, :y 0}
    {:x 9, :tone #{:db :c#}, :y 0}
    {:x 10, :tone #{:d}, :y 0}]
   [{:x 0, :tone #{:a}, :y 1}
    {:x 1, :tone #{:bb :a#}, :y 1}
    {:x 2, :tone #{:b}, :y 1, :out "b3"}
    {:x 3, :tone #{:c}, :y 1}
    {:x 4, :tone #{:db :c#}, :y 1}
    {:x 5, :tone #{:d}, :y 1}
    {:x 6, :tone #{:d# :eb}, :y 1}
    {:x 7, :tone #{:e}, :y 1, :out "1", :root? true}
    {:x 8, :tone #{:f}, :y 1}
    {:x 9, :tone #{:gb :f#}, :y 1}
    {:x 10, :tone #{:g}, :y 1, :out "5"}]
   [{:x 0, :tone #{:d}, :y 2}
    {:x 1, :tone #{:d# :eb}, :y 2}
    {:x 2, :tone #{:e}, :y 2, :out "1", :root? true}
    {:x 3, :tone #{:f}, :y 2}
    {:x 4, :tone #{:gb :f#}, :y 2}
    {:x 5, :tone #{:g}, :y 2, :out "5"}
    {:x 6, :tone #{:g# :ab}, :y 2}
    {:x 7, :tone #{:a}, :y 2}
    {:x 8, :tone #{:bb :a#}, :y 2}
    {:x 9, :tone #{:b}, :y 2, :out "b3"}
    {:x 10, :tone #{:c}, :y 2}]
   [{:x 0, :tone #{:g}, :y 3, :out "5"}
    {:x 1, :tone #{:g# :ab}, :y 3}
    {:x 2, :tone #{:a}, :y 3}
    {:x 3, :tone #{:bb :a#}, :y 3}
    {:x 4, :tone #{:b}, :y 3, :out "b3"}
    {:x 5, :tone #{:c}, :y 3}
    {:x 6, :tone #{:db :c#}, :y 3}
    {:x 7, :tone #{:d}, :y 3}
    {:x 8, :tone #{:d# :eb}, :y 3}
    {:x 9, :tone #{:e}, :y 3, :out "1", :root? true}
    {:x 10, :tone #{:f}, :y 3}]
   [{:x 0, :tone #{:b}, :y 4, :out "b3"}
    {:x 1, :tone #{:c}, :y 4}
    {:x 2, :tone #{:db :c#}, :y 4}
    {:x 3, :tone #{:d}, :y 4}
    {:x 4, :tone #{:d# :eb}, :y 4}
    {:x 5, :tone #{:e}, :y 4, :out "1", :root? true}
    {:x 6, :tone #{:f}, :y 4}
    {:x 7, :tone #{:gb :f#}, :y 4}
    {:x 8, :tone #{:g}, :y 4, :out "5"}
    {:x 9, :tone #{:g# :ab}, :y 4}
    {:x 10, :tone #{:a}, :y 4}]
   [{:x 0, :tone #{:e}, :y 5, :out "1", :root? true}
    {:x 1, :tone #{:f}, :y 5}
    {:x 2, :tone #{:gb :f#}, :y 5}
    {:x 3, :tone #{:g}, :y 5, :out "5"}
    {:x 4, :tone #{:g# :ab}, :y 5}
    {:x 5, :tone #{:a}, :y 5}
    {:x 6, :tone #{:bb :a#}, :y 5}
    {:x 7, :tone #{:b}, :y 5, :out "b3"}
    {:x 8, :tone #{:c}, :y 5}
    {:x 9, :tone #{:db :c#}, :y 5}
    {:x 10, :tone #{:d}, :y 5}]])

(defn fret-number [n]
  [:div {:style {:width           "3.9rem"
                 :min-width       "3.9rem"
                 :height          "2rem"
                 :display         "flex"
                 :justify-content :center}}
   (if (= n 0)
     ""
     n)])

(defn fret
  [{:keys [on-click circle-text background-color fret-color y circle-color]
    :or   {y                0
           background-color "#000000d6"
           fret-color       "linear-gradient(to right, #FFFFFF , #706e68)"}}]
  (let [string-color  "linear-gradient(#737270 , #b9bab3, #737270)"
        circle-color  (or circle-color "orange")
        string-height (str (+ 0.2 y) "rem")
        fret-width    3.9
        fret-height   2.7]
    [:div {:on-click on-click
           :style    {:width          (str fret-width "rem")
                      :height         (str fret-height "rem")
                      :display        "flex"
                      :flex-direction "row"}}
     [:div {:style {:background-color background-color
                    :width            (str (- fret-width 0.3) "rem")
                    :height           "100%"
                    :justify-content  :center
                    :display          "flex"
                    :flex-direction   "column"}}
      [:div {:style {:display          "flex"
                     :align-items      :center
                     :justify-content  :center
                     :background-image string-color
                     :height           string-height
                     :width            (str fret-width "rem")
                     :z-index          100}}
       (when circle-text
         [:div {:style {:display          "flex"
                        :align-items      :center
                        :justify-content  :center
                        :height           "2rem"
                        :width            "2rem"
                        :background-color circle-color
                        :border-radius    "50%"
                        :z-index          0}}
          circle-text])]]

     [:div {:style {:background-image fret-color
                    :z-index          50
                    :width            "0.5rem"
                    :height           "100%"}}]]))

(defn styled-view [{:keys [on-click matrix]
                    :or   {on-click (fn [{:keys [x y tone out root?] :as m'}]
                                      (def m' m')
                                      (js/console.log m'))}
                    :as   m}]
  (let [min-x (->> matrix first (map :x) (apply min))
        max-x (->> matrix first (map :x) (apply max))]
    [:div {:style {:overflow-x "auto"}}
     [:div {:style {:display "flex"}}
      (for [{:keys [x]} (-> matrix first)]
        ^{:key (str "fretboard-fret-" x)}
         [fret-number x])]
     (for [[idx fretboard-string] (map-indexed vector matrix)]
       ^{:key (str "fretboard-string-" idx)}
       [:div {:style {:display "flex"}}
        (for [{:keys [x y tone out root?] :as m} fretboard-string]
          ^{:key (str "fretboard-string-" x "-" y)}
           [fret
            {:y                (/ y 10)
             :circle-color     (when root? "#ff7600")
             :on-click         (fn [_] (on-click m))
             :circle-text      (when out out)
             :background-color (if (= x 0)
                                 "white"
                                 "#000000d6")
             :fret-color       (cond
                                 (= x 0)     "linear-gradient(black, black, black)"
                                 (= x max-x) "linear-gradient(#000000d6, #000000d6, #000000d6)"
                                 :else
                                 "linear-gradient(to right, #FFFFFF , #706e68)")}])])]))

(def routes
  (let [path-name :a/a]
    ["/debug/fretboard"
     {:name path-name
      :view [styled-view {:matrix matrix}]}]))
