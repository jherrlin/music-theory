(ns se.jherrlin.music-theory.webapp.menus
  (:require
   [ se.jherrlin.music-theory.definitions :as definitions]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as str]
   [re-frame.core :as re-frame]))


(defn key-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])]
    [:div {:style {:display        "flow"
                   :flow-direction "column"
                   :overflow-x     "auto"
                   :white-space    "nowrap"}}
     (for [{:keys [title key]}
           (->> [:a :a# :b :c :c# :d :d# :e :f :f# :g :g#]
                (map (fn [x] {:key   x
                              :title (-> x name str/capitalize)})))]
       ^{:key title}
       [:a {:style {:margin-right "10px"}
            :href  (rfe/href current-route-name (assoc path-params :key-of key) query-params)}
        [:button
         {:disabled (= key-of key)}
         title]])]))

(def instruments
  [{:instrument-type :keyboard
    :tuning          :standard
    :title           "Piano"}
   {:instrument-type :fretboard
    :tuning          :guitar
    :title           "Guitar"}
   {:instrument-type :fretboard
    :tuning          :mandolin
    :title           "Mandolin"}
   {:instrument-type :fretboard
    :tuning          :mandola
    :title           "Mandola"}
   {:instrument-type :fretboard
    :tuning          :cittern
    :title           "Cittern"}
   {:instrument-type :fretboard
    :tuning          :mandolin-mandola
    :title           "Mandolin-mandola"}
   {:instrument-type :fretboard
    :tuning          :bass
    :title           "Bass"}
   {:instrument-type :fretboard
    :tuning          :guitar-dadgad
    :title           "Guitar (DADGAD)"}
   {:instrument-type :fretboard
    :tuning          :nordic-mandola
    :title           "Nordic mandola"}
   {:instrument-type :fretboard
    :tuning          :ukulele
    :title           "Ukulele"}
   {:instrument-type :fretboard
    :tuning          :banjo-irish
    :title           "Banjo (Irish)"}
   {:instrument-type :fretboard
    :tuning          :tres-cubano
    :title           "Tres cubano"}
   {:instrument-type :fretboard
    :tuning          :cuban-cuatro
    :title           "Cuban cuatro"}
   {:instrument-type :fretboard
    :tuning          :puerto-rican-cuatro
    :title           "Puerto Rican cuatro"}
   {:instrument-type :fretboard
    :tuning          :greek-bouzouki-3
    :title           "Greek bouzouki (3)"}
   {:instrument-type :fretboard
    :tuning          :greek-bouzouki-4
    :title           "Greek bouzouki (4)"}
   {:instrument-type :fretboard
    :tuning          :irish-bouzouki
    :title           "Irish bouzouki"}])

(defn instrument-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])]
    [:div {:style {:display        "flow"
                   :flow-direction "column"
                   :overflow-x     "auto"
                   :white-space    "nowrap"}}
     (for [{instrument-type' :instrument-type
            tuning'          :tuning
            :keys            [title] :as m}
           (->> instruments)]
       ^{:key title}
       [:a {:style {:margin-right "10px"}
            :href  (rfe/href
                   current-route-name
                   (assoc path-params :instrument-type instrument-type' :tuning tuning')
                   query-params)}
         [:button
          {:disabled (and (= instrument-type instrument-type')
                          (= tuning tuning'))}
          title]])]))

(defn scale-sort-order []
  (let [predifined-order [:major :minor
                          :ionian :dorian :phrygian :lydian :mixolydian :aeolian :locrian
                          :harmonic-minor :natural-minor :melodic-minor]]
    (->> (concat
          predifined-order
          (->> (se.jherrlin.music-theory.definitions/scales)
               keys
               (remove (set predifined-order))
               (map name)
               (sort-by :scale/order)
               (map keyword)))
         (map (fn [x]
                {:title (-> x name (str/replace "-" " ") str/capitalize)
                 :scale x
                 :m     (definitions/scale x)})))))

(defn scale-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])
        scale'             @(re-frame/subscribe [:scale])]
    [:div
     (for [{:keys [title scale] :as m} (scale-sort-order)]
       ^{:key title}
       [:div {:style {:margin-right "10px" :display "inline"}}
        [:a {:href (rfe/href
                    :scale
                    (assoc path-params :scale scale)
                    query-params)}
         [:button
          {:disabled (= scale' scale)}
          title]]])]))

(defn harmonization-scale-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])
        scale'             @(re-frame/subscribe [:harmonization-scale])]
    [:div
     (for [{:keys [title scale] :as m}
           ;; Only scales with 7 notes
           (->> (scale-sort-order)
                (remove (comp #{:lydian-minor} :scale)) ;; TODO check why this hangs
                (filter (comp #{7} count :scale/intervals definitions/scale :scale)))]
       ^{:key title}
       [:div {:style {:margin-right "10px" :display "inline"}}
        [:a {:href (rfe/href
                    :harmonizations
                    (assoc path-params :harmonization-scale scale)
                    query-params)}
         [:button
          {:disabled (= scale' scale)}
          title]]])]))

(defn harmonization-fn []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])
        scale'             @(re-frame/subscribe [:harmonization-scale])
        harmonization-fn   @(re-frame/subscribe [:harmonization-fn])]
    [:div {:style {:display "flex"}}
     [:div {:style {:margin-right "10px"}}
      [:a {:href (rfe/href
                  :harmonizations
                  (assoc path-params :harmonization-fn :triad)
                  query-params)}
       [:button
        {:disabled (= harmonization-fn :triad)}
        "Triads"]]]
     [:div {:style {:margin-right "10px"}}
      [:a {:href (rfe/href
                  :harmonizations
                  (assoc path-params :harmonization-fn :seventh)
                  query-params)}
       [:button
        {:disabled (= harmonization-fn :seventh)}
        "Sevens"]]]]))

(defn chord-sort-order []
  (let [chords (se.jherrlin.music-theory.definitions/chords)
        predifined-order [:major :minor
                          :sus2 :sus4
                          :dominant-seven :minor-seven :major-maj-seven :diminished-seventh :minor-maj-seven]]
    (->> (concat
          predifined-order
          (->> chords
               keys
               (remove (set predifined-order))
               (map name)
               (sort)
               (map keyword)))
         (map (fn [x]
                (get chords x)))
         (remove nil?))))

(defn chord-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])
        chord'             @(re-frame/subscribe [:chord])]
    [:div
     (for [{chord :chord/chord
            title :chord/display-text
            sufix :chord/sufix
            id :id
            :as m} (chord-sort-order)]
       ^{:key id}
       [:div {:style {:margin-right "10px" :display "inline"}}
        [:a {:href (rfe/href
                    :chord
                    (assoc path-params :chord chord)
                    query-params)}
         [:button
          {:disabled (= chord' chord)}
          (or title sufix)]]])]))

(defn menu []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])
        scale              @(re-frame/subscribe [:scale])
        chord              @(re-frame/subscribe [:chord])]
    [:div {:style {:display        "flow"
                   :flow-direction "column"
                   :overflow-x     "auto"
                   :white-space    "nowrap"}}
     [:a {:style {:margin-right "10px"} :href (rfe/href :home)}
       [:button
        {:disabled (= current-route-name :home)}
        "Home"]]
     [:a {:style {:margin-right "10px"}
          :href  (rfe/href :chord path-params query-params)}
       [:button
        {:disabled (= current-route-name :chord)}
        "Chords"]]

     [:a {:style {:margin-right "10px"}
          :href  (rfe/href :scale path-params query-params)}
       [:button
        {:disabled (= current-route-name :scale)}
        "Scales"]]

     [:a {:style {:margin-right "10px"}
          :href  (rfe/href :harmonizations path-params query-params)}
       [:button
        {:disabled (= current-route-name :harmonizations)}
        "Harmonizations"]]

     [:a {:style {:margin-right "10px"}
          :href  (rfe/href :table path-params query-params)}
       [:button
        {:disabled (= current-route-name :table)}
        "Table"]]

     [:a {:style {:margin-right "10px"}
          :href  (rfe/href :bookmarks path-params query-params)}
      [:button
       {:disabled (= current-route-name :bookmarks)}
       "Bookmarks"]]]))

(defn settings
  [{:keys [as-text? as-intervals? nr-of-frets? nr-of-octavs? trim-fretboard?]
    :or   {as-intervals? true}
    :as m}]
  (let [path-params        @(re-frame/subscribe [:path-params])
        _                  (def path-params path-params)
        query-params       @(re-frame/subscribe [:query-params])
        _                  (def query-params query-params)
        current-route-name @(re-frame/subscribe [:current-route-name])
        _                  (def current-route-name current-route-name)
        key-of             @(re-frame/subscribe [:key-of])
        _                  (def key-of key-of)
        instrument-type    @(re-frame/subscribe [:instrument-type])
        _                  (def instrument-type instrument-type)
        as-intervals       @(re-frame/subscribe [:as-intervals])
        _                  (def as-intervals as-intervals)
        nr-of-octavs       @(re-frame/subscribe [:nr-of-octavs])
        _                  (def nr-of-octavs nr-of-octavs)
        as-text            @(re-frame/subscribe [:as-text])
        _                  (def as-text as-text)
        nr-of-frets        @(re-frame/subscribe [:nr-of-frets])
        _                  (def nr-of-frets nr-of-frets)
        tuning             @(re-frame/subscribe [:tuning])
        _                  (def tuning tuning)
        chord              @(re-frame/subscribe [:chord])
        _                  (def chord chord)
        trim-fretboard     @(re-frame/subscribe [:trim-fretboard])
        _                  (def trim-fretboard trim-fretboard)]
    [:div {:style {:display "flex"}}
     (when as-intervals?
       [:div
        [:input {:on-click #(re-frame/dispatch [:href [current-route-name path-params (assoc query-params :as-intervals (not as-intervals))]])
                 :checked  as-intervals
                 :type     "checkbox" :id "as-intervals-checkbox" :name "as-intervals-checkbox"}]
        [:label {:for "as-intervals-checkbox"} "Show intervals"]])

     (when as-text?
       [:div {:style {:margin-left "1rem"}}
        [:input {:on-click #(re-frame/dispatch [:href [current-route-name path-params (assoc query-params :as-text (not as-text))]])
                 :checked  as-text
                 :type     "checkbox" :id "as-text-checkbox" :name "as-text-checkbox"}]
        [:label {:for "as-text-checkbox"} "Fretboard in text"]])

     (when trim-fretboard?
       [:div {:style {:margin-left "1rem"}}
        [:input {:on-click #(re-frame/dispatch [:href [current-route-name path-params (assoc query-params :trim-fretboard (not trim-fretboard))]])
                 :checked  trim-fretboard
                 :type     "checkbox" :id "trim-fretboard-checkbox" :name "trim-fretboard-checkbox"}]
        [:label {:for "trim-fretboard-checkbox"} "Trim fretboard"]])

     (when nr-of-frets?
       [:div {:style {:margin-left "1rem"}}
        [:label {:for "nr-of-frets-input"} "Nr of frets:"]
        [:input {:style     {:width "3rem"}
                 :on-change #(re-frame/dispatch [:href [current-route-name path-params (assoc query-params :nr-of-frets (-> % .-target .-value))]])
                 :value     nr-of-frets
                 :max       37
                 :min       2
                 :type      "number" :id "nr-of-frets-input" :name "nr-of-frets-input"}]])

     (when nr-of-octavs?
       [:div {:style {:margin-left "1rem"}}
        [:label {:for "nr-of-octavs-input"} "Nr of octavs:"]
        [:input {:style     {:width "3rem"}
                 :on-change #(re-frame/dispatch [:href [current-route-name path-params (assoc query-params :nr-of-octavs (-> % .-target .-value))]])
                 :value     nr-of-octavs
                 :max       4
                 :min       1
                 :type      "number" :id "nr-of-octavs-input" :name "nr-of-octavs-input"}]])]))
