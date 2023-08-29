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


(def init-db
  {:current-route      nil
   :current-route-name :home
   :path-params        {:key-of          :c
                        :instrument-type :fretboard
                        :tuning          :guitar
                        :scale           :major
                        :chord           :major}
   :query-params       {:nr-of-frets 15
                        :as-text     false}})

(def events-
  [{:n :key-of
    :s (fn [db [k]] (get db k :c))}
   {:n :current-route}
   {:n :current-route-name
    :s (fn [db [k]] (get db k :home))}
   {:n :instrument-type
    :s (fn [db [k]] (get-in db [:path-params k]))
    :e (fn [db [k v]] (assoc-in db [:path-params k] v))}
   {:n :tuning
    :s (fn [db [k]] (get-in db [:path-params k]))
    :e (fn [db [k v]] (assoc-in db [:path-params k] v))}
   {:n :scale
    :s (fn [db [k]] (get-in db [:path-params k]))
    :e (fn [db [k v]] (assoc-in db [:path-params k] v))}
   {:n :chord
    :s (fn [db [k]] (get-in db [:path-params k]))
    :e (fn [db [k v]] (assoc-in db [:path-params k] v))}
   {:n :path-params
    :e merge'
    :s (fn [db [k]] (get db k))}
   {:n :query-params
    :e merge'
    :s (fn [db [k]] (get db k))}
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
   (if (get db :path-params)
     db
     init-db)))

(defn do-on-url-change
  [new-route-name
   {:keys [key-of instrument-type tuning scale chord]
    :as   path-params}
   query-params]
  (re-frame/dispatch [:current-route-name new-route-name])
  (re-frame/dispatch [:path-params path-params])
  (re-frame/dispatch [:query-params query-params])
  (when key-of
    (re-frame/dispatch [:key-of key-of]))
  (when instrument-type
    (re-frame/dispatch [:instrument-type instrument-type]))
  (when tuning
    (re-frame/dispatch [:tuning tuning]))
  (when scale
    (re-frame/dispatch [:scale scale]))
  (when chord
    (re-frame/dispatch [:chord chord])))
