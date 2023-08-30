(ns se.jherrlin.music-theory.webapp.instrument-types
  (:require
   [se.jherrlin.music-theory.webapp.instrument-types.fretboard :as fretboard]
   [se.jherrlin.music-theory.utils :as utils]
   [se.jherrlin.music-theory.definitions :as definitions]))




(defmulti instrument :instrument-type)

(defmethod instrument :keyboard [_]
  [:div
   [:h2 "keyboard"]])

(defmethod instrument :fretboard
  [{:keys [on-click instrument-type key-of tuning chord
           as-text as-intervals nr-of-frets fretboard-matrix] :as m}]
  (if as-text
      [:pre {:style {:overflow-x "auto"
                     :margin     "0em"}}
       (utils/fretboard-str
        (fn [{:keys [out]}] (if (nil? out) "" out))
        fretboard-matrix)]
      [fretboard/styled-view {:matrix fretboard-matrix}]))
