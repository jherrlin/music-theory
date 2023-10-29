(ns se.jherrlin.music-theory.webapp.instrument-types
  (:require
   [reitit.frontend.easy :as rfe]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [se.jherrlin.music-theory.webapp.instrument-types.fretboard :as fretboard]
   [se.jherrlin.music-theory.utils :as utils]
   [se.jherrlin.music-theory.definitions :as definitions]
   [se.jherrlin.music-theory.webapp.instrument-types.keyboard :as keyboard]
   [se.jherrlin.music-theory.models.entity :as entity]))



(defmulti instrument :instrument-type)

(defmethod instrument :keyboard [m]
  [keyboard/piano-unit m])

(defmethod instrument :fretboard
  [{:keys [on-click instrument-type key-of tuning chord
           as-text as-intervals nr-of-frets fretboard-matrix] :as m}]
  (if as-text
      [:pre {:style {:overflow-x "auto"
                     :margin     "0em"}}
       (utils/fretboard-str
        (fn [{:keys [out]}] (if (nil? out) "" out))
        fretboard-matrix)]
      [fretboard/styled-view {:matrix fretboard-matrix}]))

;; #{:scale :chord :triad :pattern}
(defmulti entity-info :type)

(defmethod entity-info :default
  [m]
  (let [debug @(re-frame/subscribe [:debug])]
    (when debug
      [:pre
       (with-out-str (cljs.pprint/pprint m))])))

(defmethod entity-info :scale
  [m]
  (let [debug @(re-frame/subscribe [:debug])
        style {:style {:margin-left "1em"}}]
    [:div {:style {:display "flex"}}
     [:p "Scale"]
     [:p style (->> m :key-of name str/capitalize)]
     [:p style (->> m :scale/intervals (str/join ", "))]
     [:p style (->> m :scale/scale (map (comp str/capitalize name)) (str/join ", "))]
     (when debug
       [:pre
        (with-out-str (cljs.pprint/pprint m))])]))

(defmethod entity-info :chord
  [{interval-tones :interval-tones
    intervals :intervals
    :as m}]
  (let [debug @(re-frame/subscribe [:debug])
        style {:style {:margin-left "1em"}}]
    [:div {:style {:display "flex"}}
     [:p "Chord"]
     [:p style (->> m :key-of name str/capitalize)]
     [:p style (->> interval-tones (map (comp str/capitalize name)) (str/join ", "))]
     [:p style (->> intervals (str/join ", "))]
     (when debug
       [:pre
        (with-out-str (cljs.pprint/pprint m))])]))

(defmethod entity-info :pattern
  [{pattern-type   :fretboard-pattern/type
    belongs-to     :fretboard-pattern/belongs-to
    intervals      :intervals
    interval-tones :interval-tones
    :as            m}]
  (let [debug @(re-frame/subscribe [:debug])
        style {:style {:margin-left "1em"}}]
    [:div {:style {:display "flex"}}
     [:p "Pattern"]
     [:p style (-> m :key-of name str/capitalize)]
     [:p style (-> pattern-type name str/capitalize)]
     [:p style (-> belongs-to name str/capitalize)]
     (when intervals
       [:p style (->> intervals (str/join ", "))])
     (when interval-tones
       [:p style (->> interval-tones (map (comp str/capitalize name)) (str/join ", "))])
     (when debug
       [:pre
        (with-out-str (cljs.pprint/pprint m))])]))

(defn instrument-component
  [{:keys [id
           as-intervals
           as-text
           bookmark-idx
           fretboard-matrix
           index-tones
           instrument-type
           interval-tones
           intervals
           key-of
           nr-of-frets
           nr-of-octavs
           tuning
           bookmark-button?
           focus-button?
           entity-info?]
    :or   {bookmark-button? true
           focus-button?    true
           entity-info?     false}
    :as   m}]
  {:pre [(entity/valid-entity? m)]}
  (let [bookmarks        @(re-frame/subscribe [:bookmarks])
        _                (entity/entities-to-string @(re-frame/subscribe [:bookmarks]))
        _                (def bookmarks bookmarks)
        bookmarks-set    @(re-frame/subscribe [:bookmarks-set])
        _                (def bookmarks-set bookmarks-set)
        bookmark-exists? (bookmarks-set m)
        _                (def bookmark-exists? bookmark-exists?)
        query-params     @(re-frame/subscribe [:query-params])
        _                (def query-params query-params)
        definition       (definitions/by-id id)
        _                (def definition definition)
        debug            @(re-frame/subscribe [:debug])
        _                (def debug debug)]
    [:<>
     (when entity-info?
       [entity-info (merge m definition)])
     [instrument m]
     [:div {:style {:margin-top "0.5rem"
                    :display    "flex"}}
      (when bookmark-button?
        [:button {:style    {:margin-right "1rem"}
                  :on-click (if bookmark-exists?
                              #(re-frame/dispatch [:remove-bookmark m])
                              #(re-frame/dispatch [:add-bookmark m]))}
         (if bookmark-exists?
           "Remove from bookmarks"
           "Add to bookmarks")])
      (when focus-button?
        [:a {:style {:margin-right "1rem"}
             :href  (rfe/href :focus (select-keys m [:instrument-type :tuning :key-of :id]) query-params)}
         [:button "Focus"]])]]))
