(ns se.jherrlin.music-theory.webapp.table
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
        trim-fretboard     @(re-frame/subscribe [:trim-fretboard])
        _                  (def trim-fretboard trim-fretboard)]
    (when key-of
      [:<>
       [menus/menu]
       [:br]
       [menus/key-selection]
       [:br]
       [:<>
        [:h3 "Chords"]
        [:table
         [:thead
          [:tr
           [:th "Sufix"]
           [:th "Intervals"]
           [:th "Tones"]]]
         [:tbody
          (for [{id           :id
                 chord-id     :chord/chord
                 intervals    :chord/intervals
                 indexes      :chord/indexes
                 text         :chord/text
                 sufix        :chord/sufix
                 explanation  :chord/explanation
                 types        :chord/types
                 chord        :chord/chord
                 display-text :chord/display-text}
                (->> (definitions/chords)
                     (vals)
                     (sort-by :chord/order))]
            ^{:key (str "chord-list-" chord-id)}
            (let [_ (def intervals intervals)]
              [:tr
               [:td
                [:a
                 {:href
                  (rfe/href
                   :chord
                   (assoc path-params :chord chord)
                   query-params)}
                 (or display-text sufix)]]
               [:td
                (->> intervals
                     (str/join ", "))]
               [:td
                (->> (utils/tones-by-key-and-intervals key-of intervals)
                     (map (comp str/capitalize name))
                     (str/join ", "))]]))]]]

       [:<>
        [:h3 "Scales"]
        [:table
         [:thead
          [:tr
           [:th "Name"]
           [:th "Intervals"]
           [:th "Tones"]]]
         [:tbody
          (for [{title :title
                 scale :scale
                 {id          :id
                  intervals   :scale/intervals
                  name'       :scale/name
                  indexes     :scale/indexes
                  sufix       :scale/sufix
                  explanation :scale/explanation}
                 :m}
                (menus/scale-sort-order)]
            ^{:key (str "scale-list-" id)}
            (let [_ (def m m)]
              [:tr
               [:td
                [:a
                 {:href
                  (rfe/href
                   :scale
                   (assoc path-params :scale scale)
                   query-params)}
                 title]]
               [:td (str/join ", " intervals)]
               [:td
                (->> (utils/tones-by-key-and-intervals key-of intervals)
                     (map (comp str/capitalize name))
                     (str/join ", "))]]))]]]])))

(def routes
  (let [route-name :table]
    ["/table/:key-of"
     {:name       route-name
      :view       [chord-component]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path  [:map
                           [:key-of          keyword?]]
                   :query [:map
                           [:nr-of-frets    {:optional true} int?]
                           [:nr-of-octavs   {:optional true} int?]
                           [:as-intervals   {:optional true} boolean?]
                           [:as-text        {:optional true} boolean?]
                           [:trim-fretboard {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path  [:key-of]
                     :query [:nr-of-frets :as-intervals :as-text :nr-of-octavs :trim-fretboard]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
