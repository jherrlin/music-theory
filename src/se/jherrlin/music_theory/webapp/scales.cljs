(ns se.jherrlin.music-theory.webapp.scales
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


(defn scale-view []
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
        scale              @(re-frame/subscribe [:scale])
        _                  (def scale scale)
        debug              @(re-frame/subscribe [:debug])
        _                  (def debug debug)
        trim-fretboard     @(re-frame/subscribe [:trim-fretboard])
        _                  (def trim-fretboard trim-fretboard)]
    (when (and scale key-of)
      (let [{id          :id
             indexes     :scale/indexes
             intervals   :scale/intervals
             text        :scale/text
             scale-names :scale/scale
             :as         m}   (definitions/scale scale)
            _                 (def indexes indexes)
            _                 (def intervals intervals)
            _                 (def scale-names scale-names)
            _                 (def m m)
            index-tones       (utils/index-tones indexes key-of)
            _                 (def index-tones index-tones)
            interval-tones    (utils/interval-tones intervals key-of)
            _                 (def interval-tones interval-tones)
            instrument-tuning (definitions/instrument-tuning tuning)
            _                 (def instrument-tuning instrument-tuning)
            fretboard-matrix  (when (= instrument-type :fretboard)
                                (utils/fretboard-strings
                                 instrument-tuning
                                 nr-of-frets))
            _                 (def fretboard-matrix fretboard-matrix)
            fretboard-matrix' (if as-intervals
                                (utils/with-all-intervals
                                  (mapv vector interval-tones intervals)
                                  fretboard-matrix)
                                (utils/with-all-tones
                                  interval-tones
                                  fretboard-matrix))
            _                 (def fretboard-matrix' fretboard-matrix')]
        [:<>
         [menus/menu]
         [:br]
         [menus/instrument-selection]
         [:br]
         [common/instrument-description]
         [:br]
         [menus/key-selection]
         [:br]
         [menus/settings
          {:as-text?        (= instrument-type :fretboard)
           :nr-of-frets?    (= instrument-type :fretboard)
           :trim-fretboard? (= instrument-type :fretboard)
           :nr-of-octavs?   (= instrument-type :keyboard)}]
         [:br]
         [menus/scale-selection]
         [common/highlight-tones interval-tones key-of]

         [common/intervals-to-tones intervals interval-tones]
         [:h3 "All " (if as-intervals "interval" "tone") " positions in the scale"]
         [instrument-types/instrument-component
          {:fretboard-matrix (cond->> fretboard-matrix'
                               trim-fretboard (utils/trim-matrix #(every? nil? (map :out %))))
           :id               id
           :as-text          as-text
           :instrument-type  instrument-type
           :key-of           key-of
           :tuning           tuning
           :nr-of-frets      nr-of-frets
           :as-intervals     as-intervals
           :index-tones      index-tones
           :interval-tones   interval-tones
           :intervals        intervals
           :nr-of-octavs     nr-of-octavs}]

         (let [scale-patterns (definitions/scale-patterns-by-belonging-and-tuning scale-names instrument-tuning)]
           (when (seq scale-patterns)
             [:<>
              [:h3 "Scale patterns"]
              (for [{id      :id
                     pattern :fretboard-pattern/pattern
                     text    :fretboard-pattern/text
                     :as     scale-pattern}
                    scale-patterns]
                ^{:key id}
                [:div {:style {:margin-bottom "2rem"}}
                 (when debug
                   [common/debug-view scale-pattern])
                 (when text
                   [:p text])
                 [instrument-types/instrument-component
                  {:id               id
                   :as-text          as-text
                   :instrument-type  instrument-type
                   :key-of           key-of
                   :tuning           tuning
                   :nr-of-frets      nr-of-frets
                   :as-intervals     as-intervals
                   :index-tones      index-tones
                   :interval-tones   interval-tones
                   :intervals        intervals
                   :nr-of-octavs     nr-of-octavs
                   :fretboard-matrix (cond->> ((if as-intervals
                                                 utils/pattern-with-intervals
                                                 utils/pattern-with-tones)
                                               key-of
                                               pattern
                                               fretboard-matrix)
                                       trim-fretboard (utils/trim-matrix #(every? nil? (map :out %))))}]])]))

         (let [chords-to-scale (utils/chords-to-scale (definitions/chords) indexes)]
           (when (seq chords-to-scale)
             [:<>
              [:h3 "Chords to scale"]
              (for [{chord :chord/chord
                     name  :chord/name
                     id    :id}
                    chords-to-scale]
                ^{:key id}
                [:div {:style {:margin-right "10px" :display "inline"}}
                 [:a {:href
                      (rfe/href :chord (assoc path-params :chord chord) query-params)}
                  [:button name]]])]))]))))

(def routes
  (let [route-name :scale]
    ["/scale/:instrument-type/:tuning/:key-of/:scale"
     {:name       route-name
      :view       [scale-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path  [:map
                           [:instrument-type keyword?]
                           [:tuning          keyword?]
                           [:key-of          keyword?]
                           [:scale           keyword?]]
                   :query [:map
                           [:nr-of-frets    {:optional true} int?]
                           [:nr-of-octavs   {:optional true} int?]
                           [:as-intervals   {:optional true} boolean?]
                           [:as-text        {:optional true} boolean?]
                           [:debug          {:optional true} boolean?]
                           [:trim-fretboard {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path  [:instrument-type :tuning :key-of :scale]
                     :query [:nr-of-frets :as-intervals :as-text :nr-of-octavs :debug :trim-fretboard]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
