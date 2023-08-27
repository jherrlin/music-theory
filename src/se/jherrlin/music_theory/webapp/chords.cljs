(ns se.jherrlin.music-theory.webapp.chords
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]))


(defn chord-view []
  (let [path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])]
    [:div
     [:h2 "chord-view"]]))

(def routes
  (let [route-name :chord]
    ["/chord/:instrument-type/:tuning/:key-of/:chord"
     {:name       route-name
      :view       [chord-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path [:map
                          [:instrument-type keyword?]
                          [:tuning          keyword?]
                          [:key-of          keyword?]
                          [:chord           keyword?]]}
      :controllers
      [{:parameters {:path [:instrument-type :tuning :key-of :chord]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
