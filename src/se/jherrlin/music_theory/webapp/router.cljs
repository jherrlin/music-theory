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
   [se.jherrlin.music-theory.webapp.harmonizations :as harmonizations]
   [se.jherrlin.music-theory.webapp.chords :as chords]
   [se.jherrlin.music-theory.webapp.scales :as scales]
   [v4.se.jherrlin.music-theory.webapp.strings.styled-fretboard :as styled-fretboard]
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.menus :as menus]
   [se.jherrlin.music-theory.webapp.home :as home]
   [se.jherrlin.music-theory.webapp.bookmarks :as bookmarks]))



(def routes
  [focus/routes
   chords/routes
   scales/routes
   harmonizations/routes
   styled-fretboard/routes
   bookmarks/routes
   home/routes])

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
