(ns se.jherrlin.music-theory.webapp.harmonizations
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.menus :as menus]))


(defn harmonizations-view []
  (let [path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])]
    [:<>
     [menus/menu]
     [menus/instrument-selection]
     [menus/key-selection]
     [menus/scale-selection]
     [:div
      [:h2 "harmonizations-view"]]]))

(def routes
  (let [route-name :harmonizations]
    ["/harmonizations/:instrument-type/:tuning/:key-of"
     {:name       route-name
      :view       [harmonizations-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path [:map
                          [:instrument-type keyword?]
                          [:tuning          keyword?]
                          [:key-of          keyword?]]}
      :controllers
      [{:parameters {:path [:instrument-type :tuning :key-of]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
