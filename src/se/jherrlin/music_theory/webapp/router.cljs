(ns se.jherrlin.music-theory.webapp.router
  (:require
   ["semantic-ui-react" :as semantic-ui]
   [reagent.dom :as rd]
   [re-frame.core :as re-frame]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.controllers :as rfc]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as str]
   [clojure.set :as set]
   [se.jherrlin.music-theory.webapp.focus :as focus]
   [v4.se.jherrlin.music-theory.webapp.strings.styled-fretboard :as styled-fretboard]))



(def routes
  [focus/routes
   styled-fretboard/routes
   ["/"
    [""
     {:name :home
      :view [:div [:h1 "Home"]]}]]])

(def router
  (rf/router
   routes
   {:data {:coercion rss/coercion}}))

(defn on-navigate [new-match]
  (when new-match
    (re-frame/dispatch [:navigated new-match])))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment true}))
