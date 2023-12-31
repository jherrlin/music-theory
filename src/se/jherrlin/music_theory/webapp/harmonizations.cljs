(ns se.jherrlin.music-theory.webapp.harmonizations
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [re-frame.core :as re-frame]
   [reitit.frontend.easy :as rfe]
   [reitit.coercion.malli]
   [se.jherrlin.music-theory.webapp.events :as events]
   [se.jherrlin.music-theory.webapp.menus :as menus]
   [se.jherrlin.music-theory.definitions :as definitions]
   [se.jherrlin.music-theory.utils :as utils]
   [se.jherrlin.music-theory.webapp.common :as common]
   [se.jherrlin.music-theory.webapp.instrument-types :as instrument-types]))

;; harmonization
[{:chord/root-tone          :c,
  :chord/intervals          ["1" "3" "5"],
  :chord/sufix              "",
  :harmonization/position   "I",
  :chord-name               "C",
  :harmonization/mode-str   "Ionian",
  :chord/interval-tones     [:c :e :g],
  :chord/types              #{:triad :major},
  :type                     :chord,
  :chord/chord              :major,
  :harmonization/family     :tonic,
  :chord/display-text       "major",
  :id                       #uuid "1cd72972-ca33-4962-871c-1551b7ea5244",
  :chord/name               "major",
  :harmonization/family-str "T",
  :harmonization/index      1,
  :chord/index-tones        [#{:c} #{:e} #{:g}],
  :chord/explanation        "major",
  :chord/indexes            [0 4 7],
  :key-of                   :c,
  :harmonization/mode       :ionian}
 {:chord/root-tone          :d,
  :chord/intervals          ["1" "b3" "5"],
  :chord/sufix              "m",
  :harmonization/position   "ii",
  :chord-name               "Dm",
  :harmonization/mode-str   "Dorian",
  :chord/interval-tones     [:d :f :a],
  :chord/types              #{:triad :minor},
  :type                     :chord,
  :chord/chord              :minor,
  :harmonization/family     :subdominant,
  :chord/display-text       "minor",
  :id                       #uuid "f9426eb8-5046-474a-b4c9-62383e5b0345",
  :chord/name               "minor",
  :harmonization/family-str "S",
  :harmonization/index      2,
  :chord/index-tones        [#{:d} #{:f} #{:a}],
  :chord/explanation        "minor",
  :chord/indexes            [0 3 7],
  :key-of                   :c,
  :harmonization/mode       :dorian}
 {:chord/root-tone          :e,
  :chord/intervals          ["1" "b3" "5"],
  :chord/sufix              "m",
  :harmonization/position   "iii",
  :chord-name               "Em",
  :harmonization/mode-str   "Phrygian",
  :chord/interval-tones     [:e :g :b],
  :chord/types              #{:triad :minor},
  :type                     :chord,
  :chord/chord              :minor,
  :harmonization/family     :tonic,
  :chord/display-text       "minor",
  :id                       #uuid "f9426eb8-5046-474a-b4c9-62383e5b0345",
  :chord/name               "minor",
  :harmonization/family-str "T",
  :harmonization/index      3,
  :chord/index-tones        [#{:e} #{:g} #{:b}],
  :chord/explanation        "minor",
  :chord/indexes            [0 3 7],
  :key-of                   :c,
  :harmonization/mode       :phrygian}
 {:chord/root-tone          :f,
  :chord/intervals          ["1" "3" "5"],
  :chord/sufix              "",
  :harmonization/position   "IV",
  :chord-name               "F",
  :harmonization/mode-str   "Lydian",
  :chord/interval-tones     [:f :a :c],
  :chord/types              #{:triad :major},
  :type                     :chord,
  :chord/chord              :major,
  :harmonization/family     :subdominant,
  :chord/display-text       "major",
  :id                       #uuid "1cd72972-ca33-4962-871c-1551b7ea5244",
  :chord/name               "major",
  :harmonization/family-str "S",
  :harmonization/index      4,
  :chord/index-tones        [#{:f} #{:a} #{:c}],
  :chord/explanation        "major",
  :chord/indexes            [0 4 7],
  :key-of                   :c,
  :harmonization/mode       :lydian}
 {:chord/root-tone          :g,
  :chord/intervals          ["1" "3" "5"],
  :chord/sufix              "",
  :harmonization/position   "V",
  :chord-name               "G",
  :harmonization/mode-str   "Mixolydian",
  :chord/interval-tones     [:g :b :d],
  :chord/types              #{:triad :major},
  :type                     :chord,
  :chord/chord              :major,
  :harmonization/family     :dominant,
  :chord/display-text       "major",
  :id                       #uuid "1cd72972-ca33-4962-871c-1551b7ea5244",
  :chord/name               "major",
  :harmonization/family-str "D",
  :harmonization/index      5,
  :chord/index-tones        [#{:g} #{:b} #{:d}],
  :chord/explanation        "major",
  :chord/indexes            [0 4 7],
  :key-of                   :c,
  :harmonization/mode       :mixolydian}
 {:chord/root-tone          :a,
  :chord/intervals          ["1" "b3" "5"],
  :chord/sufix              "m",
  :harmonization/position   "vi",
  :chord-name               "Am",
  :harmonization/mode-str   "Aeolian",
  :chord/interval-tones     [:a :c :e],
  :chord/types              #{:triad :minor},
  :type                     :chord,
  :chord/chord              :minor,
  :harmonization/family     :tonic,
  :chord/display-text       "minor",
  :id                       #uuid "f9426eb8-5046-474a-b4c9-62383e5b0345",
  :chord/name               "minor",
  :harmonization/family-str "T",
  :harmonization/index      6,
  :chord/index-tones        [#{:a} #{:c} #{:e}],
  :chord/explanation        "minor",
  :chord/indexes            [0 3 7],
  :key-of                   :c,
  :harmonization/mode       :aeolian}
 {:chord/root-tone          :b,
  :chord/intervals          ["1" "b3" "b5"],
  :chord/sufix              "dim",
  :harmonization/position   "vii",
  :chord-name               "Bdim",
  :harmonization/mode-str   "Locrian",
  :chord/interval-tones     [:b :d :f],
  :chord/types              #{:triad :diminished :minor},
  :type                     :chord,
  :chord/chord              :diminished-fifth,
  :harmonization/family     :dominant,
  :id                       #uuid "1559e2cf-5f29-4831-8a8f-dddd7ad89580",
  :chord/name               "diminished fifth",
  :harmonization/family-str "D",
  :harmonization/index      7,
  :chord/index-tones        [#{:b} #{:d} #{:f}],
  :chord/explanation        "diminished fifth",
  :chord/indexes            [0 3 6],
  :key-of                   :c,
  :harmonization/mode       :locrian}]

(defn harmonizations-table [ms]
  (let [path-params  @(re-frame/subscribe [:path-params])
        query-params @(re-frame/subscribe [:query-params])]
    [:<>
     [:p "T = Tonic (stable), S = Subdominant (leaving), D = Dominant (back home)"]
     [:table
      [:tr (map (fn [m] [:th (:harmonization/index m)]) ms)]
      [:tr (map (fn [m] [:th (:harmonization/position m)]) ms)]

      [:tr (map (fn [{chord-root-tone :chord/root-tone
                      mode            :harmonization/mode
                      :as             m}]
                  [:th
                   [:a {:href (rfe/href :scale
                                        (assoc path-params :scale mode :key-of chord-root-tone)
                                        query-params)}
                    (:harmonization/mode-str m)]]) ms)]
      [:tr (map (fn [m] [:th (:harmonization/family-str m)]) ms)]
      [:tr (map (fn [{:keys           []
                      chord-root-tone :chord/root-tone
                      chord           :chord/chord
                      :as             m}]
                  [:th
                   [:a
                    {:href (rfe/href
                            :chord
                            (assoc path-params :chord chord :key-of chord-root-tone)
                            query-params)}
                    (:chord-name m)]]) ms)]]]))

(defn harmonizations-view []
  (let [path-params         @(re-frame/subscribe [:path-params])
        _                   (def path-params path-params)
        query-params        @(re-frame/subscribe [:query-params])
        _                   (def query-params query-params)
        current-route-name  @(re-frame/subscribe [:current-route-name])
        _                   (def current-route-name current-route-name)
        key-of              @(re-frame/subscribe [:key-of])
        _                   (def key-of key-of)
        instrument-type     @(re-frame/subscribe [:instrument-type])
        _                   (def instrument-type instrument-type)
        as-intervals        @(re-frame/subscribe [:as-intervals])
        _                   (def as-intervals as-intervals)
        nr-of-octavs        @(re-frame/subscribe [:nr-of-octavs])
        _                   (def nr-of-octavs nr-of-octavs)
        as-text             @(re-frame/subscribe [:as-text])
        _                   (def as-text as-text)
        nr-of-frets         @(re-frame/subscribe [:nr-of-frets])
        _                   (def nr-of-frets nr-of-frets)
        tuning              @(re-frame/subscribe [:tuning])
        _                   (def tuning tuning)
        harmonization-scale @(re-frame/subscribe [:harmonization-scale])
        _                   (def scale harmonization-scale)
        harmonization-fn    @(re-frame/subscribe [:harmonization-fn])
        _                   (def harmonization-fn harmonization-fn)]
    (let [{id          :id
           indexes     :scale/indexes
           intervals   :scale/intervals
           text        :scale/text
           scale-names :scale/scale
           :as         m}   (definitions/scale harmonization-scale)
          _                 (def indexes indexes)
          _                 (def intervals intervals)
          _                 (def scale-names scale-names)
          _                 (def m m)
          index-tones       (utils/index-tones indexes key-of)
          _                 (def index-tones index-tones)
          interval-tones    (utils/interval-tones intervals key-of)
          _                 (def interval-tones interval-tones)
          instrument-tuning (definitions/instrument-tuning tuning)
          _                 (def instrument-tuning instrument-tuning)
          fretboard-matrix  (when (= instrument-type :fretboard)
                              (utils/fretboard-strings
                               instrument-tuning
                               nr-of-frets))
          _                 (def fretboard-matrix fretboard-matrix)
          harmonization'    (utils/gen-harmonization
                             (vals (definitions/scales))
                             (vals (definitions/chords))
                             key-of
                             harmonization-scale
                             (condp = harmonization-fn
                               :seventh utils/seventh
                               utils/triad))
          _                 (def harmonization' harmonization')]
      [:<>
       [menus/menu]
       [:br]
       [menus/instrument-selection]
       [:br]
       [common/instrument-description]
       [:br]
       [menus/key-selection]
       [:br]
       [menus/harmonization-scale-selection]
       [:br]
       [menus/harmonization-fn]
       [:br]
       [menus/settings
        {:as-text?      (= instrument-type :fretboard)
         :nr-of-frets?  (= instrument-type :fretboard)
         :nr-of-octavs? (= instrument-type :keyboard)}]

       [:br]
       [harmonizations-table harmonization']
       [:br]

       [:h2 "All " (if as-intervals "interval" "tone") " positions in the scale"]

       [instrument-types/instrument-component
        {:fretboard-matrix (if as-intervals
                             (utils/with-all-intervals
                               (mapv vector interval-tones intervals)
                               fretboard-matrix)
                             (utils/with-all-tones
                               interval-tones
                               fretboard-matrix))
         :id               id
         :as-text          as-text
         :instrument-type  instrument-type
         :key-of           key-of
         :tuning           tuning
         :nr-of-frets      nr-of-frets
         :as-intervals     as-intervals
         :index-tones      index-tones
         :interval-tones   interval-tones
         :intervals        intervals
         :nr-of-octavs     nr-of-octavs}]

       [:h2 "Chords in harmonization"]

       (for [[idx {id             :id
                   index          :harmonization/index
                   interval-tones :chord/interval-tones
                   index-tones    :chord/index-tones
                   intervals      :chord/intervals
                   mode-str       :harmonization/mode-str
                   sufix          :chord/sufix
                   root           :chord/root-tone
                   chord          :chord/chord
                   mode           :harmonization/mode
                   family-str     :harmonization/family-str
                   :as            m}]
             (map-indexed vector harmonization')]
         ^{:key (str "harmonization-index-" id idx)}
         [:<>
          #_[common/debug-view m]
          [:div {:style {:display     :flex
                         :align-items :center}}

           [:h2 {:style {:margin-right "2em"}} index]
           [:a
            {:href (rfe/href
                    :chord
                    (assoc path-params :chord chord :key-of root)
                    query-params)}
            [:p {:style {:margin-right "2em"}}
             (str (some-> interval-tones first name str/capitalize) sufix)]]
           [:a {:href (rfe/href :scale
                                (assoc path-params :scale mode :key-of root)
                                query-params)}
            [:p {:style {:margin-right "2em"}} mode-str]]
           [:p {:style {:margin-right "2em"}} family-str]
           [:p {:style {:margin-right "2em"}}
            (->> intervals (str/join ", "))]
           [:p {:style {:margin-right "2em"}}
            (->> (utils/tones-by-key-and-intervals root intervals)
                 (map (comp str/capitalize name))
                 (str/join ", "))]]
          [instrument-types/instrument-component
           {:fretboard-matrix (if as-intervals
                                (utils/with-all-intervals
                                  (mapv vector interval-tones intervals)
                                  fretboard-matrix)
                                (utils/with-all-tones
                                  interval-tones
                                  fretboard-matrix))
            :id               id
            :as-text          as-text
            :instrument-type  instrument-type
            :key-of           key-of
            :tuning           tuning
            :nr-of-frets      nr-of-frets
            :as-intervals     as-intervals
            :index-tones      index-tones
            :interval-tones   interval-tones
            :intervals        intervals
            :nr-of-octavs     nr-of-octavs}]])])))

(def routes
  (let [route-name :harmonizations]
    ["/harmonizations/:instrument-type/:tuning/:key-of/:harmonization-fn/:harmonization-scale"
     {:name       route-name
      :view       [harmonizations-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path  [:map
                           [:instrument-type     keyword?]
                           [:tuning              keyword?]
                           [:key-of              keyword?]
                           [:harmonization-fn    keyword?]
                           [:harmonization-scale keyword?]]
                   :query [:map
                           [:nr-of-frets  {:optional true} int?]
                           [:nr-of-octavs {:optional true} int?]
                           [:as-intervals {:optional true} boolean?]
                           [:as-text      {:optional true} boolean?]
                           [:debug        {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path  [:instrument-type :tuning :key-of :harmonization-fn :harmonization-scale]
                     :query [:nr-of-frets :as-intervals :as-text :nr-of-octavs :debug]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
