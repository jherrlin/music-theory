(ns se.jherrlin.music-theory.webapp.menus
  (:require
   [ se.jherrlin.music-theory.definitions :as definitions]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as str]
   [re-frame.core :as re-frame]))


(defn menu1 [component]
  (let [current-route @(re-frame/subscribe [:current-route])
        path-params   @(re-frame/subscribe [:path-params])
        query-params  @(re-frame/subscribe [:query-params])]
    [:<>
     (when component
       [component])]))


(defn key-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])]
    [:div
     (for [{:keys [title key]}
           (->> [:a :a# :b :c :c# :d :d# :e :f :f# :g :g#]
                (map (fn [x] {:key  x
                              :title (-> x name str/capitalize)})))]
       ^{:key title}
       [:div {:style {:margin-right "10px" :display "inline"}}
        [:a {:href (rfe/href current-route-name (assoc path-params :key-of key) query-params)}
         [:button
          {:disabled (= key-of key)}
          title]]])]))

(def instruments
  [{:instrument-type :keyboard
    :tuning          :standard
    :title           "Piano"}
   {:instrument-type :fretboard
    :tuning          :guitar
    :title           "Gitar"}
   {:instrument-type :fretboard
    :tuning          :mandolin
    :title           "Mandolin"}
   {:instrument-type :fretboard
    :tuning          :bass
    :title           "Bass"}
   {:instrument-type :fretboard
    :tuning          :guitar-dadgad
    :title           "Gitar (DADGAD)"}
   {:instrument-type :fretboard
    :tuning          :mandola
    :title           "Mandola"}
   {:instrument-type :fretboard
    :tuning          :octave-mandolin
    :title           "Octave mandolin"}
   {:instrument-type :fretboard
    :tuning          :ukulele
    :title           "Ukulele"}
   {:instrument-type :fretboard
    :tuning          :banjo-irish
    :title           "Banjo (irish)"}])

(defn instrument-selection []
  (let [current-route      @(re-frame/subscribe [:current-route])
        path-params        @(re-frame/subscribe [:path-params])
        query-params       @(re-frame/subscribe [:query-params])
        current-route-name @(re-frame/subscribe [:current-route-name])
        key-of             @(re-frame/subscribe [:key-of])
        instrument-type    @(re-frame/subscribe [:instrument-type])
        tuning             @(re-frame/subscribe [:tuning])]
    [:div
     (for [{instrument-type' :instrument-type
            tuning'          :tuning
            :keys            [title] :as m}
           (->> instruments)]
       ^{:key title}
       [:div {:style {:margin-right "10px" :display "inline"}}
        [:a {:href (rfe/href
                    current-route-name
                    (assoc path-params :instrument-type instrument-type' :tuning tuning')
                    query-params)}
         [:button
          {:disabled (and (= instrument-type instrument-type')
                          (= tuning tuning'))}
          title]]])]))

(defn scale-sort-order []
  (let [predifined-order [:major :minor
                          :ionian :dorian :phrygian :lydian :mixolydian :aeolian :locrian]]
    (->> (concat
          predifined-order
          (->> (se.jherrlin.music-theory.definitions/scales)
               keys
               (remove (set predifined-order))
               (map name)
               (sort)
               (map keyword)))
         (map (fn [x]
                {:title (-> x name (str/replace "-" " ") str/capitalize)
                 :scale x})))))

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

(defn chord-sort-order []
  (let [chords (se.jherrlin.music-theory.definitions/chords)
        predifined-order [:major :minor
                          :dominant-seven :minor-seven]]
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
    [:div
     [:div {:style {:margin-right "10px" :display "inline"}}
      [:a {:href (rfe/href :home)}
       [:button
        {:disabled (= current-route-name :home)}
        "Home"]]]

     [:div {:style {:margin-right "10px" :display "inline"}}
      [:a {:href (rfe/href :chord path-params query-params)}
       [:button
        {:disabled (= current-route-name :chord)}
        "Chords"]]]

     [:div {:style {:margin-right "10px" :display "inline"}}
      [:a {:href (rfe/href :scale path-params query-params)}
       [:button
        {:disabled (= current-route-name :scale)}
        "Scale"]]]

     [:div {:style {:margin-right "10px" :display "inline"}}
      [:a {:href (rfe/href :harmonizations path-params query-params)}
       [:button
        {:disabled (= current-route-name :harmonizations)}
        "Harmonizations"]]]

     [:div {:style {:margin-right "10px" :display "inline"}}
      [:a {:href (rfe/href :bookmarks path-params query-params)}
       [:button
        {:disabled (= current-route-name :bookmarks)}
        "Bookmarks"]]]]))
