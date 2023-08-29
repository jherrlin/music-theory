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
           as-text as-intervals nr-of-frets
           ] :as m}]
  (let [{id          :id
         indexes     :chord/indexes
         intervals   :chord/intervals
         explanation :chord/explanation
         sufix       :chord/sufix
         text        :chord/text
         chord-name  :chord/name
         :as         m}   (definitions/chord chord)
        instrument-tuning (get-in definitions/instrument-with-tuning [tuning :tuning])
        index-tones       (utils/index-tones indexes key-of)
        interval-tones    (utils/interval-tones intervals key-of)
        fretboard-matrix  (utils/with-all-tones
                            interval-tones #_[:e :b :g]
                            (utils/fretboard-strings
                             instrument-tuning
                             nr-of-frets
                             #_16))]
    [:div
     [fretboard/styled-view {:matrix fretboard-matrix}]]))
