(ns se.jherrlin.music-theory.webapp.main
  (:require
   [reagent.dom :as rd]
   [re-frame.core :as re-frame]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.controllers :as rfc]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as str]
   [clojure.set :as set]
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.router :as router]))



(def debug? ^boolean goog.DEBUG)

(defn main []
  (let [current-route @(re-frame/subscribe [:current-route])
        path-params   @(re-frame/subscribe [:path-params])
        query-params  @(re-frame/subscribe [:query-params])]
    [:<>
     (when current-route
       (-> current-route :data :view))]))

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (rd/render
   [main]
   (.getElementById js/document "app")))

(defn ^:dev/after-load init []
  (println "starting...")
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (router/init-routes!)
  (mount-root))
