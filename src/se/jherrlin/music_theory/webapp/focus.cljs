(ns se.jherrlin.music-theory.webapp.focus
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]))


(defn focus-view []
  [:div
   [:h1 "focus view"]])

(def routes
  ["/focus/:key-of/:id"
   {:name       :focus
    :view       [focus-view]
    :coercion   reitit.coercion.malli/coercion
    :parameters {:path [:map
                        [:key-of keyword?]
                        [:id     uuid?]]}
    :controllers
    [{:parameters {:path [:key-of :id]}
      :start      (fn [{p :path q :query}]
                    (re-frame/dispatch [:path-params p])
                    (re-frame/dispatch [:query-params q]))}]}])


;; http://localhost:8080/#/focus/c/1559e2cf-5f29-4831-8a8f-dddd7ad89580
