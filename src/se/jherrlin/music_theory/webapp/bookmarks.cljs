(ns se.jherrlin.music-theory.webapp.bookmarks
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]))


(defn bookmark-view []
  (let [path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])]
    [:div
     [:h2 "bookmark-view"]]))

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
