(ns se.jherrlin.music-theory.webapp.common
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.menus :as menus]
   [se.jherrlin.music-theory.definitions :as definitions]
   [se.jherrlin.music-theory.utils :as utils]
   [se.jherrlin.music-theory.webapp.common :as common]
   [se.jherrlin.music-theory.webapp.instrument-types :as instrument-types]))

(defn debug-view
  ([]
   (debug-view @re-frame.db/app-db))
  ([x]
   [:pre
    (with-out-str (cljs.pprint/pprint x))]))

(defn chord-name
  [key-of
   {explanation :chord/explanation
    sufix       :chord/sufix}]
  [:div {:style {:margin-top      "1em"
                 :height          "100%"
                 :display         "inline-flex"
                 :justify-content :center
                 :align-items     :center}}
   [:h2 (str (-> key-of name str/capitalize) sufix)]
   (when explanation
     [:p {:style {:margin-left "2rem"}}
      (str "(" explanation ")")])])

(defn essentials-from-definition
  "Definitions have different keys.
  Find `id`, `type`, `indexes` and `intervals` from a definition.
  It's a bit hairy."
  [{definition-type :type                   ;;
    pattern-for     :fretboard-pattern/type ;; #{:scale :chord :triad}
    :as             definition}]
  (let [{id        :id
         type      :type
         indexes   (condp = definition-type
                     :chord :chord/indexes
                     :scale :scale/indexes
                     :pattern
                     (condp = pattern-for
                       :scale :scale/indexes
                       :chord :chord/indexes
                       :triad :chord/indexes))
         intervals (condp = definition-type
                     :chord :chord/intervals
                     :scale :scale/intervals
                     :pattern
                     (condp = pattern-for
                       :scale :scale/intervals
                       :chord :chord/intervals
                       :triad :chord/intervals))
         :as       m} (condp = definition-type
                        :chord (definitions/chord (:chord/chord definition))
                        :scale (-> definition :scale/scale first definitions/scale)
                        :pattern
                        (condp = pattern-for
                          :scale (-> definition :fretboard-pattern/belongs-to definitions/scale)
                          :chord (-> definition :fretboard-pattern/belongs-to definitions/chord)
                          :triad (-> definition :fretboard-pattern/belongs-to definitions/chord)))]
    {:indexes indexes :intervals intervals :id id :type type}))

(comment
  (essentials-from-definition
   {:id #uuid "39af7096-b5c6-45e9-b743-6791b217a3df",
    :type :scale,
    :scale/scale #{:ionian :major},
    :scale/intervals ["1" "2" "3" "4" "5" "6" "7"],
    :scale/indexes [0 2 4 5 7 9 11]})
  )

(defn highlight-tones [tones key-of]
  [:div {:style {:margin-top  "1em"
                 :display     "flex"
                 :align-items "center"
                 :overflow-x  "auto"
                 :overflow-y  "auto"}}

   (for [{:keys [tone match?]}
         (let [tones-set (set tones)]
           (->> (utils/all-tones)
                (utils/rotate-until #(% key-of))
                (map (fn [tone]
                       (cond-> {:tone tone}
                         (seq (set/intersection tones-set tone))
                         (assoc :match? true))))))]
     ^{:key (str tone "something")}
     [:div {:style {:width     "4.5em"
                    :font-size "0.9em"}}
      (for [t' (sort-by (fn [x]
                          (let [x (str x)]
                            (cond
                              (and (= (count x) 3) (str/includes? x "#"))
                              1
                              (= (count x) 3)
                              2
                              :else 0))) tone)]
        ^{:key (str tone t')}
        [:div {:style {:margin-top "0em"}}
         [:div
          {:style {:color       (if-not match? "grey")
                   :font-weight (if match? "bold")}}
          (-> t' name str/capitalize)]])])])

(defn intervals-to-tones [intervals tones]
  [:pre {:style {:overflow-x "auto"}}
   (->> (map
         (fn [interval index]
           (str (utils/fformat "%8s" interval) " -> " (-> index name str/capitalize)))
         intervals
         tones)
        (str/join "\n")
        (apply str)
        (str "Interval -> Tone\n"))])
