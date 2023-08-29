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
        chord              @(re-frame/subscribe [:chord])]
    (when (and chord key-of)
      (let [{id          :id
             indexes     :chord/indexes
             intervals   :chord/intervals
             explanation :chord/explanation
             sufix       :chord/sufix
             text        :chord/text
             chord-name  :chord/name
             :as         m} (definitions/chord chord)
            _               (def m m)
            index-tones     (utils/index-tones indexes key-of)
            interval-tones  (utils/interval-tones intervals key-of)]
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
          {:instrument-type instrument-type
           :key-of          key-of
           :tuning          tuning
           :chord           chord
           :nr-of-frets     nr-of-frets}]

         #_(definitions/chord-triad-patterns-by-belonging :major)]))))

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
