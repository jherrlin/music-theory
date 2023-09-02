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
      "The main thing you can find here is chords, scales and harmonizations "
      "for different instruments.")]
    ;; [:p "Lets walk through some of the features."]
    ;; [:h2 "Chords"]
    ;; [:p "This is pattern that shows how you can play a Major C chord on guitar."]
    ;; [:p "It displays the positions and what tone is in the position on the fretboard."]

    ;; (let [nr-of-frets       4
    ;;       instrument-tuning [:e :a :d :g :b :e]
    ;;       fretboard-matrix  (utils/fretboard-strings
    ;;                          instrument-tuning
    ;;                          nr-of-frets)
    ;;       as-intervals      false
    ;;       interval-tones    [:c :e :g]
    ;;       intervals         ["1" "3" "5"]
    ;;       key-of            :c
    ;;       index-tones       [#{:c} #{:e} #{:g}]
    ;;       nr-of-octavs      1
    ;;       tuning            :guitar
    ;;       as-text           false
    ;;       instrument-type   :fretboard
    ;;       chord-pattern     [["3" nil nil nil]
    ;;                          [nil "1" nil nil]
    ;;                          ["5" nil nil nil]
    ;;                          [nil nil "3" nil]
    ;;                          [nil nil nil "1"]
    ;;                          [nil nil nil nil]]]
    ;;   [instrument-types/instrument-component
    ;;    {:fretboard-matrix #_ (if as-intervals
    ;;                            (utils/with-all-intervals
    ;;                              (mapv vector interval-tones intervals)
    ;;                              fretboard-matrix)
    ;;                            (utils/with-all-tones
    ;;                              interval-tones
    ;;                              fretboard-matrix))
    ;;     ((if as-intervals
    ;;        utils/pattern-with-intervals
    ;;        utils/pattern-with-tones)
    ;;      key-of
    ;;      chord-pattern
    ;;      fretboard-matrix)
    ;;     :as-text          as-text
    ;;     :instrument-type  instrument-type
    ;;     :key-of           key-of
    ;;     :tuning           tuning
    ;;     :nr-of-frets      nr-of-frets
    ;;     :as-intervals     as-intervals
    ;;     :index-tones      index-tones
    ;;     :interval-tones   interval-tones
    ;;     :intervals        intervals
    ;;     :nr-of-octavs     nr-of-octavs}])

    ;; [:p "It's possible to change the styling of the fretboard to be text."]

    ;; (let [nr-of-frets       4
    ;;       instrument-tuning [:e :a :d :g :b :e]
    ;;       fretboard-matrix  (utils/fretboard-strings
    ;;                          instrument-tuning
    ;;                          nr-of-frets)
    ;;       as-intervals      false
    ;;       interval-tones    [:c :e :g]
    ;;       intervals         ["1" "3" "5"]
    ;;       key-of            :c
    ;;       index-tones       [#{:c} #{:e} #{:g}]
    ;;       nr-of-octavs      1
    ;;       tuning            :guitar
    ;;       as-text           true
    ;;       instrument-type   :fretboard
    ;;       chord-pattern     [["3" nil nil nil]
    ;;                          [nil "1" nil nil]
    ;;                          ["5" nil nil nil]
    ;;                          [nil nil "3" nil]
    ;;                          [nil nil nil "1"]
    ;;                          [nil nil nil nil]]]
    ;;   [instrument-types/instrument-component
    ;;    {:fretboard-matrix #_ (if as-intervals
    ;;                            (utils/with-all-intervals
    ;;                              (mapv vector interval-tones intervals)
    ;;                              fretboard-matrix)
    ;;                            (utils/with-all-tones
    ;;                              interval-tones
    ;;                              fretboard-matrix))
    ;;     ((if as-intervals
    ;;        utils/pattern-with-intervals
    ;;        utils/pattern-with-tones)
    ;;      key-of
    ;;      chord-pattern
    ;;      fretboard-matrix)
    ;;     :as-text          as-text
    ;;     :instrument-type  instrument-type
    ;;     :key-of           key-of
    ;;     :tuning           tuning
    ;;     :nr-of-frets      nr-of-frets
    ;;     :as-intervals     as-intervals
    ;;     :index-tones      index-tones
    ;;     :interval-tones   interval-tones
    ;;     :intervals        intervals
    ;;     :nr-of-octavs     nr-of-octavs}])

    ;; [:p "Or as a piano"]

    ;; (let [
    ;;       nr-of-frets       4
    ;;       instrument-tuning [:e :a :d :g :b :e]
    ;;       fretboard-matrix  (utils/fretboard-strings
    ;;                          instrument-tuning
    ;;                          nr-of-frets)
    ;;       as-intervals      false
    ;;       interval-tones    [:c :e :g]
    ;;       intervals         ["1" "3" "5"]
    ;;       key-of            :c
    ;;       index-tones       [#{:c} #{:e} #{:g}]
    ;;       nr-of-octavs      1
    ;;       tuning            :guitar
    ;;       as-text           false
    ;;       instrument-type   :keyboard
    ;;       chord-pattern     [["3" nil nil nil]
    ;;                          [nil "1" nil nil]
    ;;                          ["5" nil nil nil]
    ;;                          [nil nil "3" nil]
    ;;                          [nil nil nil "1"]
    ;;                          [nil nil nil nil]]]
    ;;   [instrument-types/instrument-component
    ;;    {:fretboard-matrix #_ (if as-intervals
    ;;                            (utils/with-all-intervals
    ;;                              (mapv vector interval-tones intervals)
    ;;                              fretboard-matrix)
    ;;                            (utils/with-all-tones
    ;;                              interval-tones
    ;;                              fretboard-matrix))
    ;;     ((if as-intervals
    ;;        utils/pattern-with-intervals
    ;;        utils/pattern-with-tones)
    ;;      key-of
    ;;      chord-pattern
    ;;      fretboard-matrix)
    ;;     :as-text          as-text
    ;;     :instrument-type  instrument-type
    ;;     :key-of           key-of
    ;;     :tuning           tuning
    ;;     :nr-of-frets      nr-of-frets
    ;;     :as-intervals     as-intervals
    ;;     :index-tones      index-tones
    ;;     :interval-tones   interval-tones
    ;;     :intervals        intervals
    ;;     :nr-of-octavs     nr-of-octavs}])

    ;; [:p "It's possible to look at this chord in another perspective."]
    ;; [:p "This image shows the intervals that makes up chord."]

    ;; (let [nr-of-frets       4
    ;;       instrument-tuning [:e :a :d :g :b :e]
    ;;       fretboard-matrix  (utils/fretboard-strings
    ;;                          instrument-tuning
    ;;                          nr-of-frets)
    ;;       as-intervals      true
    ;;       interval-tones    [:c :e :g]
    ;;       intervals         ["1" "3" "5"]
    ;;       key-of            :c
    ;;       index-tones       [#{:c} #{:e} #{:g}]
    ;;       nr-of-octavs      1
    ;;       tuning            :guitar
    ;;       as-text           false
    ;;       instrument-type   :fretboard
    ;;       chord-pattern     [["3" nil nil nil]
    ;;                          [nil "1" nil nil]
    ;;                          ["5" nil nil nil]
    ;;                          [nil nil "3" nil]
    ;;                          [nil nil nil "1"]
    ;;                          [nil nil nil nil]]]
    ;;   [instrument-types/instrument-component
    ;;    {:fretboard-matrix #_ (if as-intervals
    ;;                            (utils/with-all-intervals
    ;;                              (mapv vector interval-tones intervals)
    ;;                              fretboard-matrix)
    ;;                            (utils/with-all-tones
    ;;                              interval-tones
    ;;                              fretboard-matrix))
    ;;     ((if as-intervals
    ;;        utils/pattern-with-intervals
    ;;        utils/pattern-with-tones)
    ;;      key-of
    ;;      chord-pattern
    ;;      fretboard-matrix)
    ;;     :as-text          as-text
    ;;     :instrument-type  instrument-type
    ;;     :key-of           key-of
    ;;     :tuning           tuning
    ;;     :nr-of-frets      nr-of-frets
    ;;     :as-intervals     as-intervals
    ;;     :index-tones      index-tones
    ;;     :interval-tones   interval-tones
    ;;     :intervals        intervals
    ;;     :nr-of-octavs     nr-of-octavs}])

    #_(let [interval-tones [:c :e :g]
            key-of         :c]
      [common/highlight-tones interval-tones key-of])

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
