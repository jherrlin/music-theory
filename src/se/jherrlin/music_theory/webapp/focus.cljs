(ns se.jherrlin.music-theory.webapp.focus
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


(defn focus-view []
  (let [{:keys [id] :as path-params} @(re-frame/subscribe [:path-params])
        _                            (def path-params path-params)
        _                            (def id id)
        query-params                 @(re-frame/subscribe [:query-params])
        _                            (def query-params query-params)
        current-route-name           @(re-frame/subscribe [:current-route-name])
        _                            (def current-route-name current-route-name)
        key-of                       @(re-frame/subscribe [:key-of])
        _                            (def key-of key-of)
        instrument-type              @(re-frame/subscribe [:instrument-type])
        _                            (def instrument-type instrument-type)
        as-intervals                 @(re-frame/subscribe [:as-intervals])
        _                            (def as-intervals as-intervals)
        nr-of-octavs                 @(re-frame/subscribe [:nr-of-octavs])
        _                            (def nr-of-octavs nr-of-octavs)
        as-text                      @(re-frame/subscribe [:as-text])
        _                            (def as-text as-text)
        nr-of-frets                  @(re-frame/subscribe [:nr-of-frets])
        _                            (def nr-of-frets nr-of-frets)
        tuning                       @(re-frame/subscribe [:tuning])
        _                            (def tuning tuning)
        chord                        @(re-frame/subscribe [:chord])
        _                            (def chord chord)]
    (let [instrument-tuning            (get-in definitions/instrument-with-tuning [tuning :tuning])
          _                            (def instrument-tuning instrument-tuning)
          {definition-type :type
           :as             definition} (definitions/by-id id)
          _                            (def definition definition)
          _                            (def definition-type definition-type)
          ]
      [:<>
       [menus/menu]
       [:br]
       [:div
        (when definition
            (let [{id          :id
                   indexes     (condp = definition-type
                                 :chord :chord/indexes
                                 :scale :scale/indexes)
                   intervals   (condp = definition-type
                                 :chord :chord/intervals
                                 :scale :scale/intervals)
                   :as         m}   (case definition-type
                                      :chord (definitions/chord (:chord/chord definition))
                                      :scale (definitions/scale (-> definition :scale/scale first)))
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

              [instrument-types/instrument-component
               {:fretboard-matrix (if as-intervals
                                    (utils/with-all-intervals
                                      (mapv vector interval-tones intervals)
                                      fretboard-matrix)
                                    (utils/with-all-tones
                                      interval-tones
                                      fretboard-matrix))
                :id id
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
                :nr-of-octavs     nr-of-octavs}]))]])))

(def routes
  (let [route-name :focus]
    ["/focus/:instrument-type/:tuning/:key-of/:id"
     {:name       route-name
      :view       [focus-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path  [:map
                           [:instrument-type keyword?]
                           [:tuning          keyword?]
                           [:key-of          keyword?]
                           [:id              uuid?]]
                   :query [:map
                           [:nr-of-frets  {:optional true} int?]
                           [:nr-of-octavs {:optional true} int?]
                           [:as-intervals {:optional true} boolean?]
                           [:as-text      {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path [:instrument-type :tuning :key-of :id]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))

;; http://localhost:8080/#/focus/fretboard/guitar/c/1cd72972-ca33-4962-871c-1551b7ea5244
