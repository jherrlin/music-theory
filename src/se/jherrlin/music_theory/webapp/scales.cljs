(ns se.jherrlin.music-theory.webapp.scales
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]))


(defn scale-view []
  (let [path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])]
    [:div
     [:h2 "scale-view"]]))

(def routes
  (let [route-name :scale]
    ["/scale/:instrument-type/:tuning/:key-of/:scale"
     {:name       route-name
      :view       [scale-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path [:map
                          [:instrument-type keyword?]
                          [:tuning          keyword?]
                          [:key-of          keyword?]
                          [:scale           keyword?]]}
      :controllers
      [{:parameters {:path [:instrument-type :tuning :key-of :scale]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
