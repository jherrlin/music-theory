(ns se.jherrlin.music-theory.webapp.common
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [se.jherrlin.music-theory.utils :as utils]))

(defn debug-view
  ([]
   (debug-view @re-frame.db/app-db))
  ([x]
   [:pre
    (with-out-str (cljs.pprint/pprint x))]))

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
