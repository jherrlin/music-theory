(ns se.jherrlin.music-theory.webapp.instrument-types
  (:require
   [reitit.frontend.easy :as rfe]
   [re-frame.core :as re-frame]
   [se.jherrlin.music-theory.webapp.instrument-types.fretboard :as fretboard]
   [se.jherrlin.music-theory.utils :as utils]
   [se.jherrlin.music-theory.definitions :as definitions]
   [se.jherrlin.music-theory.webapp.instrument-types.keyboard :as keyboard]))




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
           tuning]
    :as   m}]
  {:pre [(uuid? id)]}
  (let [bookmarks        @(re-frame/subscribe [:bookmarks])
        _                (def bookmarks bookmarks)
        bookmarks-set    @(re-frame/subscribe [:bookmarks-set])
        _                (def bookmarks-set bookmarks-set)
        bookmark-exists? (bookmarks-set m)
        _                (def bookmark-exists? bookmark-exists?)
        query-params     @(re-frame/subscribe [:query-params])
        _                (def query-params query-params)]

    [:<>
     [instrument m]
     [:div {:style {:margin-top "0.5rem"
                    :display    "flex"}}
      [:button {:style    {:margin-right "1rem"}
                :on-click (if bookmark-exists?
                            #(re-frame/dispatch [:remove-bookmark m])
                            #(re-frame/dispatch [:add-bookmark m]))}
       (if bookmark-exists?
         "Remove from bookmarks"
         "Add to bookmarks")]
      [:a {:style {:margin-right "1rem"}
           :href  (rfe/href :focus (select-keys m [:instrument-type :tuning :key-of :id]) query-params)}
       [:button "Focus"]]]]))
