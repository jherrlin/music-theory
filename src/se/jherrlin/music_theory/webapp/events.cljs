(ns se.jherrlin.music-theory.webapp.events
  "Namespace contains global events."
  (:require
   ["semantic-ui-react" :as semantic-ui]
   [reagent.dom :as rd]
   [re-frame.core :as re-frame]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.controllers :as rfc]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as str]
   [clojure.set :as set]))


(defn merge' [db [k m]]
  (assoc db k (merge (get db k) m)))

(def events-
  [{:n :key-of}
   {:n :current-route}
   {:n :path-params
    :e merge'}
   {:n :query-params
    :e merge'}
   {:n :nr-of-frets}
   {:n :nr-of-octavs}])

(doseq [{:keys [n s e]} events-]
  (re-frame/reg-sub n (or s (fn [db [n']] (get db n'))))
  (re-frame/reg-event-db n (or e (fn [db [_ e]] (assoc db n e)))))

(re-frame/reg-event-db
 :navigated
 (fn [db [_ new-match]]
   (let [old-match   (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     (assoc db :current-route (assoc new-match :controllers controllers)))))

(re-frame/reg-fx
 :push-state
 (fn [route]
   (apply rfe/push-state route)))

(re-frame/reg-event-db
 :initialize-db
 (fn [db _]
   (if db
     db
     {:current-route nil})))
