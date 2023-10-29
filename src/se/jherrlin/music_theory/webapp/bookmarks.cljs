(ns se.jherrlin.music-theory.webapp.bookmarks
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


(defn bookmark-view []
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
        bookmarks          @(re-frame/subscribe [:bookmarks])
        _                  (def bookmarks bookmarks)]
    [:<>
     [menus/menu]
     [:br]
     [:div
      (if-not (seq bookmarks)
        [:p "No bookmarks yet"]
        (for [[idx bookmark] (map-indexed vector bookmarks)]
          ^{:key (str "bookmark-" idx)}
          [:<>
           [instrument-types/instrument-component (assoc bookmark :entity-info? true)]
           [:br]]))]]))

(def routes
  (let [route-name :bookmarks]
    ["/bookmarks"
     {:name       route-name
      :view       [bookmark-view]
      :coercion   reitit.coercion.malli/coercion
      :controllers
      [{:parameters {:path []}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
