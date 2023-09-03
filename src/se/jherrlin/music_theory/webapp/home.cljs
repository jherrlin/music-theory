(ns se.jherrlin.music-theory.webapp.home
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.menus :as menus]
   [se.jherrlin.music-theory.definitions :as definitions]
   [se.jherrlin.music-theory.utils :as utils]
   [se.jherrlin.music-theory.webapp.common :as common]
   [se.jherrlin.music-theory.webapp.instrument-types :as instrument-types]))


(defn home-component []
  [:<>
   [menus/menu]
   [:div
    [:h1 "Welcome to this app about music theory!"]
    [:p
     (str
      "The main thing you can find here is diagrams of chords, scales and harmonizations "
      "for different instruments.")]
    [:p "Lets look at some features"]

    [:h2 "C major chord shape with tones"]
    (let [nr-of-frets       4
          instrument-tuning [:e :a :d :g :b :e]
          fretboard-matrix  (utils/fretboard-strings
                             instrument-tuning
                             nr-of-frets)
          as-intervals      false
          interval-tones    [:c :e :g]
          intervals         ["1" "3" "5"]
          key-of            :c
          index-tones       [#{:c} #{:e} #{:g}]
          nr-of-octavs      1
          tuning            :guitar
          as-text           false
          instrument-type   :fretboard
          chord-pattern     [["3" nil nil nil]
                             [nil "1" nil nil]
                             ["5" nil nil nil]
                             [nil nil "3" nil]
                             [nil nil nil "1"]
                             [nil nil nil nil]]]
      [instrument-types/instrument-component
       {:fretboard-matrix #_ (if as-intervals
                               (utils/with-all-intervals
                                 (mapv vector interval-tones intervals)
                                 fretboard-matrix)
                               (utils/with-all-tones
                                 interval-tones
                                 fretboard-matrix))
        ((if as-intervals
           utils/pattern-with-intervals
           utils/pattern-with-tones)
         key-of
         chord-pattern
         fretboard-matrix)
        :id               #uuid "2e3cdabd-c310-4327-8470-1f3ecd0aefa6" ;; not connected to any definition
        :bookmark-button? false
        :focus-button?    false
        :as-text          as-text
        :instrument-type  instrument-type
        :key-of           key-of
        :tuning           tuning
        :nr-of-frets      nr-of-frets
        :as-intervals     as-intervals
        :index-tones      index-tones
        :interval-tones   interval-tones
        :intervals        intervals
        :nr-of-octavs     nr-of-octavs}])

    [:h2 "C major chord shape with intervals"]
    (let [nr-of-frets       4
          instrument-tuning [:e :a :d :g :b :e]
          fretboard-matrix  (utils/fretboard-strings
                             instrument-tuning
                             nr-of-frets)
          as-intervals      true
          interval-tones    [:c :e :g]
          intervals         ["1" "3" "5"]
          key-of            :c
          index-tones       [#{:c} #{:e} #{:g}]
          nr-of-octavs      1
          tuning            :guitar
          as-text           false
          instrument-type   :fretboard
          chord-pattern     [["3" nil nil nil]
                             [nil "1" nil nil]
                             ["5" nil nil nil]
                             [nil nil "3" nil]
                             [nil nil nil "1"]
                             [nil nil nil nil]]]
      [instrument-types/instrument-component
       {:fretboard-matrix
        ((if as-intervals
           utils/pattern-with-intervals
           utils/pattern-with-tones)
         key-of
         chord-pattern
         fretboard-matrix)
        :id               #uuid "2e3cdabd-c310-4327-8470-1f3ecd0aefa6" ;; not connected to any definition
        :bookmark-button? false
        :focus-button?    false
        :as-text          as-text
        :instrument-type  instrument-type
        :key-of           key-of
        :tuning           tuning
        :nr-of-frets      nr-of-frets
        :as-intervals     as-intervals
        :index-tones      index-tones
        :interval-tones   interval-tones
        :intervals        intervals
        :nr-of-octavs     nr-of-octavs}])


    [:h2 "Highligh what tones are used"]
    (let [interval-tones [:c :e :g]
          key-of         :c]
      [common/highlight-tones interval-tones key-of])

    [:h2 "What interval goes to what tone"]
    (let [intervals      ["1" "3" "5"]
          interval-tones [:c :e :g]]
      [common/intervals-to-tones intervals interval-tones])


    [:h2 "C major chord shape with fretboard in text"]

    (let [nr-of-frets       4
          instrument-tuning [:e :a :d :g :b :e]
          fretboard-matrix  (utils/fretboard-strings
                             instrument-tuning
                             nr-of-frets)
          as-intervals      false
          interval-tones    [:c :e :g]
          intervals         ["1" "3" "5"]
          key-of            :c
          index-tones       [#{:c} #{:e} #{:g}]
          nr-of-octavs      1
          tuning            :guitar
          as-text           true
          instrument-type   :fretboard
          chord-pattern     [["3" nil nil nil]
                             [nil "1" nil nil]
                             ["5" nil nil nil]
                             [nil nil "3" nil]
                             [nil nil nil "1"]
                             [nil nil nil nil]]]
      [instrument-types/instrument-component
       {:fretboard-matrix #_ (if as-intervals
                               (utils/with-all-intervals
                                 (mapv vector interval-tones intervals)
                                 fretboard-matrix)
                               (utils/with-all-tones
                                 interval-tones
                                 fretboard-matrix))
        ((if as-intervals
           utils/pattern-with-intervals
           utils/pattern-with-tones)
         key-of
         chord-pattern
         fretboard-matrix)
        :id               #uuid "2e3cdabd-c310-4327-8470-1f3ecd0aefa6" ;; not connected to any definition
        :bookmark-button? false
        :focus-button?    false
        :as-text          as-text
        :instrument-type  instrument-type
        :key-of           key-of
        :tuning           tuning
        :nr-of-frets      nr-of-frets
        :as-intervals     as-intervals
        :index-tones      index-tones
        :interval-tones   interval-tones
        :intervals        intervals
        :nr-of-octavs     nr-of-octavs}])

    [:h2 "Chord in piano"]

    (let [nr-of-frets       4
          instrument-tuning [:e :a :d :g :b :e]
          fretboard-matrix  (utils/fretboard-strings
                             instrument-tuning
                             nr-of-frets)
          as-intervals      false
          interval-tones    [:c :e :g]
          intervals         ["1" "3" "5"]
          key-of            :c
          index-tones       [#{:c} #{:e} #{:g}]
          nr-of-octavs      1
          tuning            :guitar
          as-text           false
          instrument-type   :keyboard
          chord-pattern     [["3" nil nil nil]
                             [nil "1" nil nil]
                             ["5" nil nil nil]
                             [nil nil "3" nil]
                             [nil nil nil "1"]
                             [nil nil nil nil]]]
      [instrument-types/instrument-component
       {:fretboard-matrix
        ((if as-intervals
           utils/pattern-with-intervals
           utils/pattern-with-tones)
         key-of
         chord-pattern
         fretboard-matrix)
        :id               #uuid "2e3cdabd-c310-4327-8470-1f3ecd0aefa6" ;; not connected to any definition
        :bookmark-button? false
        :focus-button?    false
        :as-text          as-text
        :instrument-type  instrument-type
        :key-of           key-of
        :tuning           tuning
        :nr-of-frets      nr-of-frets
        :as-intervals     as-intervals
        :index-tones      index-tones
        :interval-tones   interval-tones
        :intervals        intervals
        :nr-of-octavs     nr-of-octavs}])

        [:h2 "A major scale on mandolin"]
    (let [nr-of-frets       8
          instrument-tuning [:g :d :a :e]
          fretboard-matrix  (utils/fretboard-strings
                             instrument-tuning
                             nr-of-frets)
          as-intervals      false
          interval-tones    [:a :b :c# :d :e :f# :g#]
          intervals         ["1" "2" "3" "4" "5" "6" "7"]
          key-of            :a
          index-tones       [#{:a} #{:b} #{:db :c#} #{:d} #{:e} #{:gb :f#} #{:g# :ab}]
          nr-of-octavs      1
          tuning            :mandolin
          as-text           false
          instrument-type   :fretboard
          pattern'          [[nil nil nil nil nil nil]
                             [nil nil nil nil nil nil]
                             ["5" nil "6" nil "7" "1"]
                             ["1" nil "2" nil "3" "4"]]]
      [instrument-types/instrument-component
       {:fretboard-matrix
        ((if as-intervals
           utils/pattern-with-intervals
           utils/pattern-with-tones)
         key-of
         pattern'
         fretboard-matrix)
        :id               #uuid "2e3cdabd-c310-4327-8470-1f3ecd0aefa6" ;; not connected to any definition
        :bookmark-button? false
        :focus-button?    false
        :as-text          as-text
        :instrument-type  instrument-type
        :key-of           key-of
        :tuning           tuning
        :nr-of-frets      nr-of-frets
        :as-intervals     as-intervals
        :index-tones      index-tones
        :interval-tones   interval-tones
        :intervals        intervals
        :nr-of-octavs     nr-of-octavs}])





    #_(let [intervals      ["1" "3" "5"]
            interval-tones [:c :e :g]]
      [common/intervals-to-tones intervals interval-tones])

    #_(let [intervals      ["1" "b3" "5"]
            interval-tones [:c :eb :g]]
      [common/intervals-to-tones intervals interval-tones])


    [:a {:href "https://github.com/jherrlin/music-theory"}
     "Source code"]]])

(def routes
  (let [route-name :home]
    ["/"
     {:name     route-name
      :view     [home-component]
      :coercion reitit.coercion.malli/coercion
      :controllers
      [{:start (fn [_]
                 (re-frame/dispatch [:current-route-name route-name]))}]}]))
