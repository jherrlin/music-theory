(ns se.jherrlin.music-theory.webapp.chords
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


(defn chord-component []
  (let [path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        as-intervals       @(re-frame/subscribe [:as-intervals])
        as-text            @(re-frame/subscribe [:as-text])
        nr-of-frets        @(re-frame/subscribe [:nr-of-frets])
        tuning             @(re-frame/subscribe [:tuning])
        chord              @(re-frame/subscribe [:chord])
        _                  (def chord chord)]
    (when (and chord key-of)
      (let [{id          :id
             indexes     :chord/indexes
             intervals   :chord/intervals
             explanation :chord/explanation
             sufix       :chord/sufix
             text        :chord/text
             chord-name  :chord/name
             :as         m}   (definitions/chord chord)
            _                 (def m m)
            index-tones       (utils/index-tones indexes key-of)
            _                 (def index-tones index-tones)
            interval-tones    (utils/interval-tones intervals key-of)
            _                 (def interval-tones interval-tones)
            instrument-tuning (get-in definitions/instrument-with-tuning [tuning :tuning])
            _                 (def instrument-tuning instrument-tuning)
            fretboard-matrix  (utils/fretboard-strings
                               instrument-tuning
                               nr-of-frets)
            _                 (def fretboard-matrix fretboard-matrix)]
        [:<>
         [menus/menu]
         [:br]
         [menus/instrument-selection]
         [:br]
         [menus/key-selection]
         [:br]
         [menus/chord-selection]
         [common/highlight-tones interval-tones key-of]

         [common/intervals-to-tones intervals interval-tones]
         [:h3 "All " (if as-intervals "interval" "tone") " positions in the chord"]
         [instrument-types/instrument
          {:fretboard-matrix (if as-intervals
                               (utils/with-all-intervals
                                 (mapv vector interval-tones intervals)
                                 fretboard-matrix)
                               (utils/with-all-tones
                                 interval-tones
                                 fretboard-matrix))
           :as-text          as-text
           :instrument-type  instrument-type
           :key-of           key-of
           :tuning           tuning
           :chord            chord
           :nr-of-frets      nr-of-frets}]

         (let [chord-patterns (definitions/chord-patterns-by-belonging-and-tuning chord instrument-tuning)]
           (when (seq chord-patterns)
             [:<>
              [:h3 "Chord patterns"]
              (for [{id      :id
                     pattern :fretboard-pattern/pattern
                     :as     chord-pattern}
                    chord-patterns]
                ^{:key id}
                [:div
                 #_[common/debug-view chord-pattern]
                 [instrument-types/instrument
                  {:instrument-type  instrument-type
                   :as-text          as-text
                   :fretboard-matrix ((if as-intervals
                                        utils/pattern-with-intervals
                                        utils/pattern-with-tones)
                                      key-of
                                      pattern
                                      fretboard-matrix)}]])]))

         (let [triad-patterns (definitions/chord-triad-patterns-by-belonging-and-tuning chord instrument-tuning)]
           (when (seq triad-patterns)
             [:<>
              [:h3 "Triad patterns"]
              (for [{id      :id
                     pattern :fretboard-pattern/pattern
                     :as     chord-pattern}
                    triad-patterns]
                ^{:key id}
                [:div
                 [instrument-types/instrument
                  {:instrument-type  instrument-type
                   :as-text          as-text
                   :fretboard-matrix ((if as-intervals
                                        utils/pattern-with-intervals
                                        utils/pattern-with-tones)
                                      key-of
                                      pattern
                                      fretboard-matrix)}]])]))

         #_(let [scales-to-chord (utils/scales-to-chord @definitions/scales indexes)]
             (when (seq scales-to-chord)
               [:<>
                [:h3 "Scales to chord"]
                (for [{scale-title :scale/name
                       scale-id    :scale/id}
                      scales-to-chord]
                  ^{:key scale-title}
                  [:div {:style {:margin-right "10px" :display "inline"}}
                   [:a {:href
                        (rfe/href :v4.strings/scale (assoc path-params :scale scale-id) query-params)}
                    [:button scale-title]]])]))]))))

(def routes
  (let [route-name :chord]
    ["/chord/:instrument-type/:tuning/:key-of/:chord"
     {:name       route-name
      :view       [chord-component]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path  [:map
                           [:instrument-type keyword?]
                           [:tuning          keyword?]
                           [:key-of          keyword?]
                           [:chord           keyword?]]
                   :query [:map
                           [:nr-of-frets {:optional true} int?]
                           [:as-intervals {:optional true} boolean?]
                           [:as-text {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path  [:instrument-type :tuning :key-of :chord]
                     :query [:nr-of-frets :as-intervals :as-text]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
