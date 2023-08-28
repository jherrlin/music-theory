(ns se.jherrlin.music-theory.webapp.focus
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]))


(defn focus-view []
  [:div
   [:h1 "focus view"]])

(def routes
  (let [route-name :focus]
    ["/focus/:instrument-type/:tuning/:key-of/:id"
     {:name       route-name
      :view       [focus-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path [:map
                          [:instrument-type keyword?]
                          [:tuning          keyword?]
                          [:key-of          keyword?]
                          [:id              uuid?]]}
      :controllers
      [{:parameters {:path [:instrument-type :tuning :key-of :id]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))


;; http://localhost:8080/#/focus/c/1559e2cf-5f29-4831-8a8f-dddd7ad89580