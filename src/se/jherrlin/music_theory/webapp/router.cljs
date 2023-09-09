(ns se.jherrlin.music-theory.webapp.router
  (:require
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
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.menus :as menus]
   [se.jherrlin.music-theory.webapp.home :as home]
   [se.jherrlin.music-theory.webapp.bookmarks :as bookmarks]
   [se.jherrlin.music-theory.webapp.instrument-types.fretboard :as fretboard]
   [se.jherrlin.music-theory.webapp.table :as table]))



(def routes
  [focus/routes
   chords/routes
   scales/routes
   harmonizations/routes
   fretboard/routes
   bookmarks/routes
   home/routes
   table/routes])

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
