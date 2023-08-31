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
        _                  (def path-params path-params)
        query-params       @(re-frame/subscribe [:query-params])
        _                  (def query-params query-params)
        current-route-name @(re-frame/subscribe [:current-route-name])
        _                  (def current-route-name current-route-name)
        key-of             @(re-frame/subscribe [:key-of])
        _                  (def key-of key-of)
        instrument-type    @(re-frame/subscribe [:instrument-type])
        _                  (def instrument-type instrument-type)
        as-intervals       @(re-frame/subscribe [:as-intervals])
        _                  (def as-intervals as-intervals)
        nr-of-octavs       @(re-frame/subscribe [:nr-of-octavs])
        _                  (def nr-of-octavs nr-of-octavs)
        as-text            @(re-frame/subscribe [:as-text])
        _                  (def as-text as-text)
        nr-of-frets        @(re-frame/subscribe [:nr-of-frets])
        _                  (def nr-of-frets nr-of-frets)
        tuning             @(re-frame/subscribe [:tuning])
        _                  (def tuning tuning)
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
            _                 (def indexes indexes)
            _                 (def m m)
            _                 (def intervals intervals)
            index-tones       (utils/index-tones indexes key-of)
            _                 (def index-tones index-tones)
            interval-tones    (utils/interval-tones intervals key-of)
            _                 (def interval-tones interval-tones)
            instrument-tuning (get-in definitions/instrument-with-tuning [tuning :tuning])
            _                 (def instrument-tuning instrument-tuning)
            fretboard-matrix  (when (= instrument-type :fretboard)
                                (utils/fretboard-strings
                                 instrument-tuning
                                 nr-of-frets))
            _                 (def fretboard-matrix fretboard-matrix)]
        [:<>
         [menus/menu]
         [:br]
         [menus/instrument-selection]
         [:br]
         [menus/key-selection]
         [:br]
         [menus/chord-selection]
         [:br]
         [common/chord-name key-of m]
         [:br]
         [common/highlight-tones interval-tones key-of]
         [:br]
         [common/intervals-to-tones intervals interval-tones]

         [:h3 "All " (if as-intervals "interval" "tone") " positions in the chord"]
         [instrument-types/instrument-component
          {:fretboard-matrix (if as-intervals
                               (utils/with-all-intervals
                                 (mapv vector interval-tones intervals)
                                 fretboard-matrix)
                               (utils/with-all-tones
                                 interval-tones
                                 fretboard-matrix))
           :id               id
           :as-text          as-text
           :instrument-type  instrument-type
           :key-of           key-of
           :tuning           tuning
           :chord            chord
           :nr-of-frets      nr-of-frets
           :as-intervals     as-intervals
           :index-tones      index-tones
           :interval-tones   interval-tones
           :intervals        intervals
           :nr-of-octavs     nr-of-octavs}]

         (let [chord-patterns (definitions/chord-patterns-by-belonging-and-tuning chord instrument-tuning)]
           (when (seq chord-patterns)
             [:<>
              [:h3 "Chord patterns"]
              (for [{id      :id
                     pattern :fretboard-pattern/pattern
                     :as     chord-pattern}
                    chord-patterns]
                ^{:key id}
                [:div {:style {:margin-bottom "2rem"}}
                 #_[common/debug-view chord-pattern]
                 [instrument-types/instrument-component
                  {:id               id
                   :instrument-type  instrument-type
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
                [:div {:style {:margin-bottom "2rem"}}
                 [instrument-types/instrument-component
                  {:id               id
                   :instrument-type  instrument-type
                   :as-text          as-text
                   :fretboard-matrix ((if as-intervals
                                        utils/pattern-with-intervals
                                        utils/pattern-with-tones)
                                      key-of
                                      pattern
                                      fretboard-matrix)}]])]))

         (let [scales-to-chord (utils/scales-to-chord (definitions/scales) indexes)]
           (when (seq scales-to-chord)
             [:<>
              [:h3 "Scales to chord"]
              (for [{scale :scale/scale
                     id    :id}
                    scales-to-chord]
                ^{:key id}
                [:div {:style {:margin-right "10px" :display "inline"}}
                 [:a {:href
                      (rfe/href :scale (assoc path-params :scale (-> scale first)) query-params)}
                  [:button
                   (->> scale
                        (map (comp str/capitalize #(str/replace % "-" "") name))
                        (str/join " / "))]]])]))]))))

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
                           [:nr-of-frets  {:optional true} int?]
                           [:nr-of-octavs {:optional true} int?]
                           [:as-intervals {:optional true} boolean?]
                           [:as-text      {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path  [:instrument-type :tuning :key-of :chord]
                     :query [:nr-of-frets :as-intervals :as-text :nr-of-octavs]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
