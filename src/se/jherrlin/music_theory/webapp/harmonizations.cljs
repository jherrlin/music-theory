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


(defn harmonizations-view []
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
        scale              @(re-frame/subscribe [:scale])
        _                  (def scale scale)
        fn'                @(re-frame/subscribe [:fn])
        _                  (def fn' fn')]
    (let [{id          :id
           indexes     :scale/indexes
           intervals   :scale/intervals
           text        :scale/text
           scale-names :scale/scale
           :as         m}   (definitions/scale scale)
          _                 (def indexes indexes)
          _                 (def intervals intervals)
          _                 (def scale-names scale-names)
          _                 (def m m)
          index-tones       (utils/index-tones indexes key-of)
          _                 (def index-tones index-tones)
          interval-tones    (utils/interval-tones intervals key-of)
          _                 (def interval-tones interval-tones)
          instrument-tuning (get-in definitions/instrument-with-tuning [tuning :tuning])
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
                             scale
                             (condp = fn'
                               :seventh utils/seventh
                               utils/triad))
          _                 (def harmonization' harmonization')]
      [:<>
       [menus/menu]
       [:br]
       [menus/instrument-selection]
       [:br]
       [menus/key-selection]

       [:h3 "All " (if as-intervals "interval" "tone") " positions in the scale"]
       [instrument-types/instrument-component
        {:fretboard-matrix (if as-intervals
                             (utils/with-all-intervals
                               (mapv vector interval-tones intervals)
                               fretboard-matrix)
                             (utils/with-all-tones
                               interval-tones
                               fretboard-matrix))
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
                   family-str     :harmonization/family-str
                   :as            m}]
             (map-indexed vector harmonization')]
         ^{:key (str "harmonization-index-" id idx)}
         [:<>
          #_[common/debug-view m]
          [:div {:style {:display         :flex
                         :flex-direction  :column
                         :justify-content :center}}
           [:div {:style {:display :flex
                                        ;:justify-content :center
                          }}
            [:h3 {:style {:margin-right "2em"}} index]
            [:p {:style {:margin-right "2em"}}
             (str (some-> interval-tones first name str/capitalize) sufix)]
            [:p {:style {:margin-right "2em"}} mode-str]
            [:p {:style {:margin-right "2em"}} family-str]
            [:p {:style {:margin-right "2em"}}
             (->> intervals (str/join ", "))]
            [:p {:style {:margin-right "2em"}}
             (->> (utils/tones-by-key-and-intervals root intervals)
                  (map (comp str/capitalize name))
                  (str/join ", "))]]]
          [instrument-types/instrument-component
           {:fretboard-matrix (if as-intervals
                                (utils/with-all-intervals
                                  (mapv vector interval-tones intervals)
                                  fretboard-matrix)
                                (utils/with-all-tones
                                  interval-tones
                                  fretboard-matrix))
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
          ]

         )
       ])))

(def routes
  (let [route-name :harmonizations]
    ["/harmonizations/:instrument-type/:tuning/:key-of/:fn/:scale"
     {:name       route-name
      :view       [harmonizations-view]
      :coercion   reitit.coercion.malli/coercion
      :parameters {:path  [:map
                           [:instrument-type keyword?]
                           [:tuning          keyword?]
                           [:key-of          keyword?]
                           [:fn              keyword?]
                           [:scale           keyword?]]
                   :query [:map
                           [:nr-of-frets  {:optional true} int?]
                           [:nr-of-octavs {:optional true} int?]
                           [:as-intervals {:optional true} boolean?]
                           [:as-text      {:optional true} boolean?]]}
      :controllers
      [{:parameters {:path  [:instrument-type :tuning :key-of :fn :scale]
                     :query [:nr-of-frets :as-intervals :as-text :nr-of-octavs]}
        :start      (fn [{p :path q :query}]
                      (events/do-on-url-change route-name p q))}]}]))
