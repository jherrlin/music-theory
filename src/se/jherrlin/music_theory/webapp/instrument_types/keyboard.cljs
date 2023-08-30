(ns se.jherrlin.music-theory.webapp.instrument-types.keyboard
  (:require
   [clojure.string :as str]))


(defn piano
  "tone-or-interval: `:tone` `:interval`"
  [tone-or-interval input]
  (let [
        ;; input                         [{:interval-tone      :c,
        ;;                                 :interval  "1",}
        ;;                                {:interval-tone      :eb,
        ;;                                 :interval  "b3"}
        ;;                                {:interval-tone      :g,
        ;;                                 :interval  "5"}]
        sharp-keys-styling            {:margin-left      "-1rem"
                                       :height           "65%"
                                       :width            "2rem"
                                       :background-color "black"
                                       :border-radius    "0px 0px 5px 5px"}
        full-keys-styling             {:width         "3rem"
                                       :border        "2px solid black"
                                       :border-radius "0px 0px 10px 10px"}
        full-keys-styling-with-margin (merge full-keys-styling
                                             {:margin-left "-1rem"})
        column-reverse                {:flex-direction :column-reverse
                                       :height         "100%"
                                       :display        :flex
                                       :align-items    :center
                                       :margin         "-0.5rem"}
        orange                        "#ff7400"
        white                         "white"
        black                         "black"
        circle-style                  (fn [color] {:display          :flex
                                                   :height           "1.8rem"
                                                   :width            "1.8rem"
                                                   :background-color color #_ "#ff7400"
                                                   :border-radius    "50%"
                                                   :z-index          0
                                                   :align-items      :center
                                                   :justify-content  :center})
        the-comp                      (fn [tone-set]
                                        [:div {:style column-reverse}
                                         (when-let [{:keys [interval-tone interval]} (first (filter (comp tone-set :interval-tone) input))]
                                           [:div {:style (circle-style orange)}
                                            (condp = tone-or-interval
                                              :interval interval
                                              (-> interval-tone name str/capitalize))])])]
    [:div {:style {:display        :flex
                   :flex-direction :row}}

     ;; C
     [:div {:style full-keys-styling}
      [the-comp #{:c}]]

     ;; C#
     [:div {:style sharp-keys-styling}
      [the-comp #{:db :c#}]]

     ;; D
     [:div {:style full-keys-styling-with-margin}
      [the-comp #{:d}]]

     ;; D#
     [:div {:style sharp-keys-styling}
      [the-comp #{:d# :eb}]]

     ;; E
     [:div {:style full-keys-styling-with-margin}
      [the-comp #{:e}]]

     ;; F
     [:div {:style full-keys-styling}
      [the-comp #{:f}]]

     ;; F#
     [:div {:style sharp-keys-styling}
      [the-comp #{:gb :f#}]]

     ;; G
     [:div {:style full-keys-styling-with-margin}
      [the-comp #{:g}]]

     ;; G#
     [:div {:style sharp-keys-styling}
      [the-comp #{:g# :ab}]]

     ;; A
     [:div {:style full-keys-styling-with-margin}
      [the-comp #{:a}]]

     ;; A#
     [:div {:style sharp-keys-styling}
      [the-comp #{:bb :a#}]]

     ;; B
     [:div {:style full-keys-styling-with-margin}
      [the-comp #{:b}]]]))


(defn piano-unit
  [{:keys [as-intervals index-tones interval-tones intervals key-of nr-of-octavs] :as m}]
  [:div {:style {:overflow-x "auto"}}
   [:div {:style {:display    :flex
                  :height     "7rem"}}
    (for [index (range 0 nr-of-octavs)]
      ^{:key (str "piano-octav-index-" index)}
      [piano
       (if as-intervals
         :interval
         :tone)
       (mapv
        (fn [interval interval-tone]
          {:interval interval :interval-tone interval-tone})
        intervals
        interval-tones)])]])
