(ns se.jherrlin.music-theory.utils
  "

  Index tones are sets:        #{:db :c#}
  Interval tones are keywords: :c#
"
  (:require
   #?(:cljs [goog.string.format])
   #?(:cljs [goog.string :as gstring])
   [clojure.set :as set]
   [clojure.string :as str]
   [malli.core :as m]
   [se.jherrlin.music-theory.intervals :as intervals]
   [se.jherrlin.music-theory.models.chord :as models.chord]
   [se.jherrlin.music-theory.models.fretboard-pattern :as models.fretboard-pattern]
   [se.jherrlin.music-theory.models.scale :as models.scale]
   [se.jherrlin.music-theory.models.tone :as models.tone]))



(defn vec-remove
  "remove elem in coll"
  [idx coll]
  (into (subvec coll 0 idx) (subvec coll (inc idx))))

(defn list-insert
  "Insert `element` on `index` on `lst`.

 (list-insert
  0
  3
  [1 2 3 4])"
  [element index lst]
  (let [[l r] (split-at index lst)]
    (concat l [element] r)))

(list-insert
 0
 3
 [1 2 3 4])

#?(:cljs
   (defn fformat
     "Formats a string using goog.string.format.
   e.g: (format \"Cost: %.2f\" 10.0234)"
     [fmt & args]
     (apply gstring/format fmt args))
   :clj (def fformat format))

(fformat "%5d" 3)
(fformat "Hello there, %s" "bob")

(defn rotate-until
  "Rotate collection `xs` util `pred`.

  (rotate-until
   #(% :f#)
   [#{:c} #{:db :c#} #{:d} #{:d# :eb} #{:e} #{:f} #{:gb :f#} #{:g}])"
  [pred xs]
  {:pre [(fn? pred) (coll? xs)]}
  (let [xs-count (count xs)]
    (->> (cycle xs)
         (drop-while #(not (pred %)))
         (take xs-count)
         (vec))))

(rotate-until
 #(% :f#)
 [#{:c} #{:db :c#} #{:d} #{:d# :eb} #{:e} #{:f} #{:gb :f#} #{:g} #{:g# :ab} #{:a} #{:bb :a#} #{:b}])

(defn take-indexes
  "Take indexes from a collection.

  (take-indexes
   [#{:c} #{:db :c#} #{:d} #{:d# :eb} #{:e} #{:f}]
   [0 3 5])"
  [coll indexes]
  (mapv
   (fn [index]
     (nth coll index))
   indexes))

(take-indexes
 [#{:c} #{:db :c#} #{:d} #{:d# :eb} #{:e} #{:f}]
 [0 3 5])

(defn rotate-matrix
  "Rotate matrix.
  In:  `[[1 2 3] [3 4 5] [6 7 8]]`
  Out: `[[1 3 6] [2 4 7] [3 5 8]]`

  (rotate-matrix
   [[\"3\" nil nil]
    [nil \"1\" nil]
    [\"5\" nil nil]
    [nil nil nil]
    [nil nil nil]
    [nil nil nil]])"
  [matrix]
  (if-not (seq matrix)
    matrix
    (->> matrix
         (apply mapv vector)
         (mapv identity))))

(rotate-matrix
 [["3" nil nil]
  [nil "1" nil]
  ["5" nil nil]
  [nil nil nil]
  [nil nil nil]
  [nil nil nil]])

(defn trim-matrix
  "Trim a matrix left and right by `pred`.

  Will remove the columns and rows that have `nil` values.

 (trim-matrix
  [[\"3\" nil nil]
   [nil \"1\" nil]
   [\"5\" nil nil]
   [nil nil nil]
   [nil nil nil]
   [nil nil nil]])"
  ([fretboard-matrix]
   (trim-matrix (partial every? nil?) fretboard-matrix))
  ([pred fretboard-matrix]
   (if (empty? fretboard-matrix)
     fretboard-matrix
     (let [rotated (rotate-matrix fretboard-matrix)
           first?  (->> rotated first pred)
           last?   (->> rotated last pred)]
       (cond
         (and first? last?)
         (->> rotated
              (drop 1)
              (drop-last 1)
              (rotate-matrix)
              (trim-matrix pred))
         (and first? (not last?))
         (->> rotated
              (drop 1)
              (rotate-matrix)
              (trim-matrix pred))
         (and (not first?) last?)
         (->> rotated
              (drop-last 1)
              (rotate-matrix)
              (trim-matrix pred))
         :else fretboard-matrix)))))

(trim-matrix
 [["3" nil nil]
  [nil "1" nil]
  ["5" nil nil]
  [nil nil nil]
  [nil nil nil]
  [nil nil nil]])

(trim-matrix
 [[nil "1" nil nil]
  [nil "5" nil nil]
  [nil "b3" nil nil]
  [nil nil nil "1"]
  [nil nil nil "5"]
  [nil "1" nil nil]])

(defn map-matrix
  "Flatten matrix into one dim list and run `f` on each item. The convert it back
  into a matrix.

  (map-matrix
   inc
   [[1]
    [2]
    [3]])"
  [f matrix]
  (->> (apply concat matrix)
       (map f)
       (partition (-> matrix first count))
       (mapv #(mapv identity %))))

(map-matrix
 inc
 [[1]
  [2]
  [3]])





(defn all-tones
  "All tones as index.

  =>
  [#{:c} #{:db :c#} #{:d} #{:d# :eb} #{:e} #{:f} #{:gb :f#} #{:g} #{:g# :ab} #{:a} #{:bb :a#} #{:b}]"
  []
  {:post [(models.tone/valid-index-tones? %)]
   :malli/schema [:=> [] models.tone/IndexTones]}
  [#{:c} #{:db :c#} #{:d} #{:d# :eb} #{:e} #{:f} #{:gb :f#} #{:g} #{:g# :ab} #{:a} #{:bb :a#} #{:b}])

(all-tones)

(defn tones-starting-at
  "`x` - can be both a index tone and a interval tone.

  Example:
  `x` = `:d#`
  =>
  [#{:d# :eb} #{:e} #{:f} #{:gb :f#} #{:g} #{:g# :ab} #{:a} #{:bb :a#} #{:b} #{:c} #{:db :c#} #{:d}]"
  ([x]
   (tones-starting-at (all-tones) x))
  ([all-tones x]
   {:pre [(models.tone/valid-index-tones? all-tones)]}
   (rotate-until
    #(if (models.tone/valid-index-tone? x)
       (= % x)
       (% x))
    all-tones)))

(tones-starting-at (all-tones) :c)
(tones-starting-at :c)
(tones-starting-at (all-tones) #{:c})
(tones-starting-at (all-tones) :d#)

(defn fretboard-string
  "Generate a freatboard string.

  `all-tones`        - A collection of all the tones
  `tuning`           - Tuning on string
  `number-of-frets`  - Width of the freatboard"
  ([string-tune number-of-frets]
   (fretboard-string (all-tones) string-tune number-of-frets))
  ([all-tones string-tune number-of-frets]
   {:pre [(models.tone/valid-index-tones? all-tones)
          (models.tone/valid-interval-tone? string-tune)]}
   (->> (mapv
         (fn [x t]
           {:x    x
            :tone t})
         (iterate inc 0)
         (->> (tones-starting-at all-tones string-tune)
              (cycle)
              (take number-of-frets))))))

(fretboard-string
 (all-tones)
 :e
 12)

(defn fretboard-strings
  "Generate freatboard matrix.

  `all-tones`        - A collection of all the tones
  `tunings`          - Tuning on strings
  `number-of-frets`  - Width of the freatboard"
  ([string-tunes number-of-frets]
   (fretboard-strings (all-tones) string-tunes number-of-frets))
  ([all-tones string-tunes number-of-frets]
   {:pre [(models.tone/valid-index-tones? all-tones)
          (models.tone/valid-interval-tones? string-tunes)]}
   (let [number-of-frets (inc number-of-frets)]
     (->> string-tunes
          (reverse)
          (mapv
           (fn [y string-tune]
             (mapv
              #(assoc % :y y)
              (fretboard-string all-tones string-tune number-of-frets)))
           (iterate inc 0))))))

(fretboard-strings
 (all-tones)
 [:e :b :g :d :a :e]
 2)

(defn gen-fretboard-matrix
  "Generate a matrix that represents the fretboard.

  `all-tones`        - A collection of all the tones
  `tunings`          - Tunings on each string
  `number-of-frets`  - Width of the freatboard"
  ([string-tunings number-of-frets]
   (gen-fretboard-matrix (all-tones) string-tunings number-of-frets))
  ([all-tones string-tunings number-of-frets]
   {:pre [(models.tone/valid-index-tones? all-tones)
          (models.tone/valid-interval-tones? string-tunings)]}
   (->> string-tunings
        (mapv
         (fn [y string-tune]
           (mapv
            #(assoc % :y y)
            (fretboard-string all-tones string-tune number-of-frets)))
         (iterate inc 0)))))

(gen-fretboard-matrix
 (all-tones)
 [:e :b :g :a :b]
 3)

(defn sharp-or-flat
  "Select tone from interval.
  Tone is a set: #{:db :c#}
  Interval is a string: \"3#\"

  =>
  :c#"
  [index-tone interval]
  {:pre [(models.tone/valid-index-tone? index-tone)]}
  (-> (cond
        (= 1 (count index-tone))     index-tone
        (str/includes? interval "b") (filter (comp #(str/includes? % "b") name) index-tone)
        :else                        (filter (comp #(str/includes? % "#") name) index-tone))
      (first)))

(sharp-or-flat
 #{:db :c#}
 "#3")

(sharp-or-flat
 #{:db :c#}
 "b3")

(defn tones-data-from-indexes-and-intervals
  [all-tones indexes intervals]
  (mapv
   (fn [index interval]
     (merge
      (get intervals/intervals-map-by-function interval)
      (let [index-tone (nth all-tones index)]
        {:index-tone    index-tone
         :interval-tone (sharp-or-flat (nth all-tones index) interval)
         :interval      interval
         :index         index})))
   indexes
   intervals))

(tones-data-from-indexes-and-intervals
 (all-tones)
 [0 3 7]
 ["1" "b3" "5"])

(defn tones-on-indexes-with-intervals
  ([indexes intervals]
   (tones-on-indexes-with-intervals (all-tones) indexes intervals))
  ([tones indexes intervals]
   (mapv
    (fn [index interval]
      (sharp-or-flat (nth tones index) interval))
    indexes
    intervals)))

(tones-on-indexes-with-intervals
 (all-tones)
 [0 3 7]
 ["1" "b3" "5"])

(defn tones-by-indexes
  "Tones by indexes"
  ([indexes]
   (tones-by-indexes (all-tones) indexes))
  ([all-tones indexes]
   {:pre  [(models.tone/valid-index-tones? all-tones)
           (m/validate models.tone/Indexes indexes)]
    :post [(models.tone/valid-index-tones? %)]}
   (take-indexes all-tones indexes)))

(tones-by-indexes
 (all-tones)
 [0 4 7])


(defn tones-by-key-and-indexes
  "Index tones by `key-of` and `indexes`

  =>
  [#{:c} #{:d} #{:e}]"
  ([key-of indexes]
   (tones-by-key-and-indexes (all-tones) key-of indexes))
  ([all-tones key-of indexes]
   {:pre  [(models.tone/valid-index-tones? all-tones)
           (m/validate models.tone/Indexes indexes)]
    :post [(models.tone/valid-index-tones? %)]}
   (let [all-tones' (tones-starting-at all-tones key-of)]
     (tones-by-indexes all-tones' indexes))))

(tones-by-key-and-indexes
 :d
 [0 2 4 5 7 9 11])

(tones-by-key-and-indexes
 :d
 [0 2 4 5 7 9 11])

(tones-by-key-and-indexes
 #{:c}
 [0 4 7])

(defn tones-by-intervals
  "Get tones from intervals

  `intervals`   -   [\"1\" \"3\" \"5\"]
  =>
  [:c :e :g]"
  ([intervals]
   (tones-by-intervals (all-tones) intervals))
  ([all-tones intervals]
   {:pre  [(m/validate models.tone/Intervals intervals)]
    :post [(m/validate models.tone/IntervalTones %)]}
   (->> intervals
        (mapv (fn [interval-function]
                (let [interval-index (get-in intervals/intervals-map-by-function [interval-function :semitones])]
                  (sharp-or-flat (nth all-tones interval-index) interval-function)))))))

(tones-by-intervals
 (all-tones)
 ["1" "3" "5"])

(tones-by-intervals
 ["1" "3" "5"])

(defn tones-by-key-and-intervals
  "

  `key-of`    -  :c
  `intervals` -  [\"1\" \"3\" \"5\"]
  =>
  [:c :e :g]"
  ([key-of intervals]
   (tones-by-key-and-intervals (all-tones) key-of intervals))
  ([all-tones key-of intervals]
   {:pre  [(m/validate models.tone/IntervalTone key-of)
           (m/validate models.tone/Intervals intervals)]
    :post [(m/validate models.tone/IntervalTones %)]}
   (tones-by-intervals
    (tones-starting-at all-tones key-of)
    intervals)))

(tones-by-key-and-intervals
 :c
 #_["1" "b3" "5"]
 ["1" "2" "3" "4" "5" "6" "7"])

(defn intervals-to-indexes
  "Indexes from intervals

  `intervals` -  [\"1\" \"3\" \"5\"]
  =>
  `[0 3 7]`"
  [intervals]
  {:pre  [(m/validate models.tone/Intervals intervals)]
   :post [(m/validate models.tone/Indexes %)]}
  (->> intervals
       (mapv (fn [interval]
               (get-in intervals/intervals-map-by-function [interval :semitones])))))

(intervals-to-indexes ["1" "b3" "5"])

(defn tones-data-from-key-of-and-intervals
  "

  `key-of`    -  :c
  `intervals` -  [\"1\" \"3\" \"5\"]
  =>
  [:c :e :g]"
  ([key-of intervals]
   (tones-data-from-key-of-and-intervals (all-tones) key-of intervals))
  ([all-tones key-of intervals]
   {:pre  [(m/validate models.tone/IndexTones all-tones)
           (m/validate models.tone/IntervalTone key-of)]
    :post [(models.tone/valid-tones-data? %)]}
   (let [indexes (intervals-to-indexes intervals)]
     (tones-data-from-indexes-and-intervals
      (tones-starting-at all-tones key-of)
      indexes
      intervals))))

(tones-data-from-key-of-and-intervals
 (all-tones)
 :c
 ["1" "b3" "5"]
 #_["1" "3" "5"])

(defn intevals-string->intervals-matrix
  [interval]
  (->> interval
       (str/trim)
       (str/split-lines)
       (mapv (fn [line]
               (->> line
                    str/trim
                    (re-seq #"(b{0,2}#{0,2}\d{1,2})|-")
                    (mapv second))))))

(intevals-string->intervals-matrix
 "   3   -   -
   -   bb1   -
   5   -   -
   -   -   -
   -   -   -
   -   -   -")

(defn find-fretboard-pattern
  "Find patterns on the fretboard"
  [all-tones key-of interval-matrix fretboard-matrix]
  (let [;; interval-matrix       (trim-matrix interval-matrix)
        interval-matrix-width (-> interval-matrix first count)
        fretboard-count       (-> fretboard-matrix first count)]
    (loop [counter           0
           found-pattern-xys #{}]
      (let [combinations-p
            (->>  fretboard-matrix
                  (mapv (comp vec (partial take interval-matrix-width) (partial drop counter))))
            combinations
            (->> combinations-p
                 (apply concat)
                 (mapv vector (apply concat interval-matrix)))
            box-match? (->> combinations
                            (remove (comp nil? first))
                            (every? (fn [[interval' {:keys [tone] :as tone'}]]
                                      (let [interval-semitones (get-in intervals/intervals-map-by-function [interval' :semitones])
                                            fretboard-tone     (nth
                                                                (tones-starting-at all-tones key-of)
                                                                interval-semitones)]
                                        (and
                                         (= (-> combinations-p first count)
                                            interval-matrix-width)
                                         (= tone fretboard-tone))))))
            pattern-check
            (->> combinations
                 (remove (comp nil? first))
                 (map (fn [[interval' {:keys [tone] :as tone'}]]
                        (let [interval-semitones (get-in intervals/intervals-map-by-function [interval' :semitones])
                              fretboard-tone     (nth
                                                  (tones-starting-at all-tones key-of)
                                                  interval-semitones)]
                          (assoc tone'
                                 :match? (and box-match? (= tone fretboard-tone))
                                 :interval interval')))))
            found-pattern-xys'
            (if-not box-match?
              found-pattern-xys
              (->> pattern-check
                   (filter :match?)
                   (map #(select-keys % [:x :y :interval]))
                   (set)
                   (into found-pattern-xys)))]
        (if (< counter fretboard-count)
          (recur
           (inc counter)
           found-pattern-xys')
          (->> fretboard-matrix
               (apply concat)
               (map (fn [{:keys [x y] :as m}]
                      (if-not (contains? (->> found-pattern-xys'
                                              (map (juxt :x :y))
                                              (set))
                                         [x y])
                        m
                        (assoc m
                               :pattern-match? true
                               :interval (->> found-pattern-xys'
                                              (filter (fn [{x' :x y' :y}]
                                                        (= [x y] [x' y'])))
                                              (first)
                                              :interval)))))
               (partition fretboard-count)
               (mapv #(mapv identity %))))))))

(find-fretboard-pattern
 (all-tones)
 :e
 [["1" nil nil]
  ["5" nil nil]
  ["b3" nil nil]
  [nil nil "1"]
  [nil nil "5"]
  ["1" nil nil]]
 (fretboard-strings
  (all-tones)
  [:e :a :d :g :b :e]
  5))

(defn match-chord-with-scales
  "Find what scales that works with a chord, by the chord indexes.

  `scales-map`     - Map with scales
  `chord-indexes`  - Seq with chord indexes, example: `[0 4 7]`"
  [scales-maps chord-indexes]
  {:pre [(seq scales-maps)]}
  (->> scales-maps
       (filter (fn [scale]
                 (let [scale-indexes (get scale :scale/indexes)]
                   (set/subset? (set chord-indexes) (set scale-indexes)))))))

(match-chord-with-scales
 [#:scale{:id        :ionian,
          :intervals ["1" "2" "3" "4" "5" "6" "7"],
          :indexes   [0 2 4 5 7 9 11],
          :title     "ionian",
          :order     4}]
 [0 4 7])

(defn find-chords
  [chord-maps all-tones chord-tones]
  {:pre [(seq chord-maps)]}
  (let [[root-tone & _] chord-tones
        tones           (tones-starting-at all-tones root-tone)]
    (->> chord-maps
         (filter (fn [{:chord/keys [indexes]}]
                   (let [chord-to-serch (tones-by-indexes tones indexes)
                         chord-to-match chord-tones]
                     (and (= (count chord-to-serch) (count chord-to-match))
                          (->> (map (fn [a b]
                                      (if (set? b)
                                        (= a b)
                                        (boolean (a b))))
                                    chord-to-serch
                                    chord-to-match)
                               (every? true?)))))))))

(find-chords
 [#:chord{:id           :major,
          :intervals    ["1" "3" "5"],
          :indexes      [0 4 7],
          :title        "major",
          :order        1,
          :sufix        "",
          :explanation  "major",
          :display-text "major"}
  #:chord{:id           :minor,
          :intervals    ["1" "b3" "5"],
          :indexes      [0 3 7],
          :title        "minor",
          :order        2,
          :sufix        "m",
          :explanation  "minor",
          :display-text "minor"}]
 (all-tones)
 [#{:c} #{:e} #{:g}]
 #_[#{:c} #{:d# :eb} #{:g}]
 #_[:c :e :g])

(defn find-chord
  [chord-maps all-tones chord-tones]
  {:pre [(coll? chord-maps)]}
  (->> (find-chords chord-maps all-tones chord-tones)
       (first)))



(find-chord
 [#:chord{:id           :major,
          :intervals    ["1" "3" "5"],
          :indexes      [0 4 7],
          :title        "major",
          :order        1,
          :sufix        "",
          :explanation  "major",
          :display-text "major"}]
 (all-tones)
 [#{:c} #{:e} #{:g}])

(defn chord-name
  [chord-maps chord-tones]
  {:pre [(coll? chord-maps)]}
  (let [root-tone             (first chord-tones)
        {:chord/keys [sufix]} (find-chord chord-maps (all-tones) chord-tones)]
    (str (-> root-tone name str/lower-case str/capitalize) sufix)))

(chord-name
 [#:chord{:id           :major,
          :intervals    ["1" "3" "5"],
          :indexes      [0 4 7],
          :title        "major",
          :order        1,
          :sufix        "",
          :explanation  "major",
          :display-text "major"}
  #:chord{:id           :minor,
          :intervals    ["1" "b3" "5"],
          :indexes      [0 3 7],
          :title        "minor",
          :order        2,
          :sufix        "m",
          :explanation  "minor",
          :display-text "minor"}]
 #_[:c :e :g]
 [:c :eb :g])

(defn merge-matrix [width f xs]
  (->> xs
       (map f)
       (map (partial apply concat))
       (apply map merge)
       (partition-all width)
       (mapv #(mapv identity %))))

(defn scales-to-chord [scales chord-indexes]
  (->> scales
       (vals)
       (filter
        (fn [{:scale/keys [indexes]}]
          (set/subset? (set chord-indexes) (set indexes))))))

(comment
  (scales-to-chord
   @v4.se.jherrlin.music-theory.definitions/scales
   [0 4 7]))

(defn chords-to-scale [chords scale-indexes]
  (->> chords
       (vals)
       (sort-by :chord/order)
       (filter (fn [{:chord/keys [indexes]}]
                 (set/subset? (set indexes) (set scale-indexes))))))

(comment
  (chords-to-scale
   @v4.se.jherrlin.music-theory.definitions/chords
   [0 2 4 5 7 9 11]))

(defn index-tones
  "Index tones from indexes and key-of"
  ([indexes key-of]
   (index-tones (all-tones) indexes key-of))
  ([all-tones indexes key-of]
   {:pre  [(m/validate models.tone/Indexes indexes)
           (m/validate models.tone/IntervalTone key-of)]
    :post [(m/validate models.tone/IndexTones %)]}
   (let [tones (tones-starting-at all-tones key-of)]
     (tones-by-indexes tones indexes))))

(index-tones [0 1 2] :c)

(defn interval-tones
  "Interval tones from intervals and key-of"
  ([intervals key-of]
   (interval-tones (all-tones) intervals key-of))
  ([all-tones intervals key-of]
   {:pre  [(m/validate models.tone/Intervals intervals)
           (m/validate models.tone/IntervalTone key-of)]
    :post [(m/validate models.tone/IntervalTones %)]}
   (tones-by-intervals
    (tones-starting-at all-tones key-of)
    intervals)))

(interval-tones ["1" "b3" "5"] :c)

(defn add-layer [f fretboard-matrix]
  (map-matrix f fretboard-matrix))

(defn add-pattern
  [{:keys [x y tone pattern-match? interval out] :as m}]
  (if (seq interval)
    (assoc m :out (-> (sharp-or-flat tone interval) name str/capitalize))
    m))

(defn add-sharps
  [{:keys [x y tone pattern-match? interval out] :as m}]
  (assoc m :out (-> tone (sharp-or-flat "#") name str/capitalize)))

(defn add-flats
  [{:keys [x y tone pattern-match? interval out] :as m}]
  (assoc m :out (-> tone (sharp-or-flat "b") name str/capitalize)))

(defn add-chord-tones
  [chord-tones {:keys [x y tone pattern-match? interval out] :as m}]
  (if-let [tone' (first (set/intersection (set chord-tones) tone))]
    (assoc m :out (-> tone' name str/capitalize))
    m))

(defn add-root
  [root-tone {:keys [x y tone pattern-match? interval out] :as m}]
  {:pre [(set? tone)]}
  (if (or (= out "1")
          (tone root-tone))
    (assoc m :root? true)
    m))

(defn add-intervals
  "Show as intervals.

  Example:
  chord-tones-and-intervals: `[[:c \"1\"] [:d \"2\"] [:e \"3\"] [:f \"4\"] [:g \"5\"] [:a \"6\"] [:b \"7\"]]`"
  [chord-tones-and-intervals {:keys [x y tone pattern-match? interval out] :as m}]
  {:pre [(set? tone)]}
  (if-let [i (->> chord-tones-and-intervals
                  (filter (fn [[tone' interval']]
                            (tone tone')))
                  (first)
                  (second))]
    (assoc m :out i)
    m))

(defn add-pattern-with-intervals
  [{:keys [x y tone pattern-match? interval out] :as m}]
  (if (and pattern-match? interval)
    (assoc m :out interval)
    m))

(->> (find-fretboard-pattern
      (all-tones)
      :e
      [["1" nil nil]
       ["5" nil nil]
       ["b3" nil nil]
       [nil nil "1"]
       [nil nil "5"]
       ["1" nil nil]]
      (fretboard-strings
       (all-tones)
       [:e :b :g :d :a :e]
       12))
     (add-layer
      #_add-flats
      #_add-sharps
      add-pattern)
     (add-layer
      (partial add-root :e)))

(->> (fretboard-strings
      (all-tones)
      [:e :b :g :d :a :e]
      10)
     (add-layer
      #_(partial add-chord-tones [:e :b :g])
      (partial add-intervals [[:e "1"] [:b "b3"] [:g "5"]]))
     (add-layer
      (partial add-root nil)))

(defn fretboard-str
  [tone-f matrix]
  (let [add-table-stuff
        (fn [row]
          (str "|" (apply str (interpose "|" (map #(fformat " %-3s" %) row))) "|"))
        rows
        (->> matrix
             (map
              (fn [row]
                (->> row
                     (map tone-f))))
             (map add-table-stuff))
        row-length (-> rows first count)]
    (->> rows
         (list-insert (add-table-stuff (->> matrix first (map (comp str :x)))) 0)
         (list-insert (str "|" (apply str (take (- row-length 2) (repeat "-"))) "|") 1)
         (str/join "\n"))))

(->> (fretboard-strings
      (all-tones)
      [:e :b :g :d :a :e]
      12)
     (add-layer
      #_(partial add-chord-tones [:e :b :g])
      (partial add-intervals [[:e "1"] [:b "b3"] [:g "5"]]))
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

(->> (find-fretboard-pattern
      (all-tones)
      :e
      [["1" nil nil]
       ["5" nil nil]
       ["b3" nil nil]
       [nil nil "1"]
       [nil nil "5"]
       ["1" nil nil]]
      (fretboard-strings
       (all-tones)
       [:e :b :g :d :a :e]
       10))
     (add-layer
      #_add-flats
      #_add-sharps
      #_add-pattern
      (partial add-intervals [[:e "1"] [:b "b3"] [:g "5"]]))
     ;; (trim-matrix #(every? nil? (map :out %))) ;; Trim fretboard
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

(->> (find-fretboard-pattern
      (all-tones)
      :c
      [["5" nil nil]
       [nil nil "3"]
       [nil nil "1"]
       [nil nil "5"]
       ["1" nil nil]
       ["5" nil nil]]
      (fretboard-strings
       (all-tones)
       [:e :b :g :d :a :e]
       10))
     (add-layer
      #_add-flats
      #_add-sharps
      add-pattern)
     ;; (trim-matrix #(every? nil? (map :out %))) ;; Trim fretboard
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

;; Public functions

(defn pattern-with-intervals
  [key-of pattern fretboard-matrix]
  (->> (find-fretboard-pattern
        (all-tones)
        key-of
        pattern
        fretboard-matrix)
       (add-layer add-pattern-with-intervals)
       (add-layer (partial add-root nil))))

(->> (pattern-with-intervals
      :a
      [["5" nil nil]
       [nil nil "3"]
       [nil nil "1"]
       [nil nil "5"]
       ["1" nil nil]
       ["5" nil nil]]
      (fretboard-strings
       (all-tones)
       [:e :a :d :g :b :e]
       10))
     (trim-matrix #(every? nil? (map :out %)))
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

(defn pattern-with-tones
  [key-of pattern fretboard-matrix]
  (->> (find-fretboard-pattern
        (all-tones)
        key-of
        pattern
        fretboard-matrix)
       (add-layer add-pattern)
       (add-layer (partial add-root key-of))))

(->> (fretboard-strings
      (all-tones)
      [:e :b :g :d :a :e]
      10)
     (pattern-with-tones
      :c
      [["5" nil nil]
       [nil nil "3"]
       [nil nil "1"]
       [nil nil "5"]
       ["1" nil nil]
       ["5" nil nil]])
     (trim-matrix #(every? nil? (map :out %)))
     ;; (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     ;; (println)
     )
(defn with-all-tones
  "
  `tones` - `[:e :b :g]`"
  [tones fretboard-matrix]
  (->> fretboard-matrix
       (add-layer (partial add-chord-tones tones))
       (add-layer (partial add-root (first tones)))))

(->> (fretboard-strings
      ;; (all-tones)
      [:e :b :g :d :a :e]
      16)
     (with-all-tones [:e :b :g])
     #_(utils/trim-matrix #(every? nil? (map :out %)))
     ;; (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     ;; (println)
     )
(defn with-all-intervals
  "
  chord-tones-and-intervals: `[[:c \"1\"] [:d \"2\"] [:e \"3\"] [:f \"4\"] [:g \"5\"] [:a \"6\"] [:b \"7\"]]`"
  [chord-tones-and-intervals fretboard-matrix]
  (->> fretboard-matrix
       (add-layer (partial add-intervals chord-tones-and-intervals))
       (add-layer (partial add-root nil))))

(->> (with-all-intervals
       [[:e "1"] [:b "b3"] [:g "5"]]
       (fretboard-strings
        (all-tones)
        [:e :b :g :d :a :e]
        12))
     (trim-matrix #(every? nil? (map :out %)))
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

(defn with-all-sharps
  [fretboard-matrix]
  (->> fretboard-matrix
       (add-layer add-sharps)))

(->> (with-all-sharps
       (fretboard-strings
        (all-tones)
        [:e :b :g :d :a :e]
        12))
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

(defn with-all-flats
  [fretboard-matrix]
  (->> fretboard-matrix
       (add-layer add-flats)))

(->> (with-all-flats
       (fretboard-strings
        (all-tones)
        [:e :b :g :d :a :e]
        12))
     (fretboard-str (fn [{:keys [out]}] (if (nil? out) "" out)))
     (println))

;; Harmonization

(def triad   (juxt #(nth % 0) #(nth % 2) #(nth % 4)))
(def seventh (juxt #(nth % 0) #(nth % 2) #(nth % 4) #(nth % 6)))

(defn gen-harmonization [scales chords key-of scale' steps-fn]
  (let [scale         (->> scales
                           (filter (comp #(% scale') :scale/scale))
                           first)

        scale-indexes (get scale :scale/indexes)
        scale-tones   (tones-by-indexes
                       (tones-starting-at key-of)
                       scale-indexes)]
    (->> scale-tones
         (mapv
          (fn [t]
            (let [index-chord-tones (steps-fn
                                     (tones-starting-at scale-tones t))
                  found-chord       (find-chord
                                     chords
                                     (all-tones)
                                     index-chord-tones)
                  interval-tones    (mapv (fn [interval' index']
                                            (sharp-or-flat index' interval'))
                                          (:chord/intervals found-chord)
                                          index-chord-tones)]
              (assoc
               found-chord
               :key-of key-of
               :chord/index-tones index-chord-tones
               :chord/interval-tones interval-tones
               :chord/root-tone (first interval-tones)
               :chord-name (chord-name chords interval-tones)))))
         (mapv
          #(assoc %7
                  :harmonization/index      %1
                  :harmonization/position   %2
                  :harmonization/mode       %3
                  :harmonization/mode-str   %4
                  :harmonization/family     %5
                  :harmonization/family-str %6)
          (range 1 100)
          (if (= scale' :major)
            ["I" "ii" "iii" "IV" "V" "vi" "vii"]
            ["i" "ii" "III" "iv" "v" "VI" "VII"])
          (if (= scale' :major)
            [:ionian  :dorian  :phrygian :lydian :mixolydian :aeolian :locrian]
            [:aeolian :locrian :ionian   :dorian :phrygian   :lydian  :mixolydian])
          (if (= scale' :major)
            ["Ionian"  "Dorian"  "Phrygian" "Lydian" "Mixolydian" "Aeolian" "Locrian"]
            ["Aeolian" "Locrian" "Ionian"   "Dorian" "Phrygian"   "Lydian"  "Mixolydian"])
          (if (= scale' :major)
            [:tonic :subdominant :tonic :subdominant :dominant :tonic :dominant]
            [:tonic :subdominant :tonic :subdominant :dominant :subdominant :dominant])
          (if (= scale' :major)
            ["T" "S" "T" "S" "D" "T" "D"]
            ["T" "S" "T" "S" "D" "S" "D"])))))

(comment
  (gen-harmonization
   (vals @v5.se.jherrlin.music-theory.definitions/scales)
   (vals @v5.se.jherrlin.music-theory.definitions/chords)
   :c
   :major
   triad #_seventh)
  (gen-harmonization
   (vals (se.jherrlin.music-theory.definitions/scales))
   (vals (se.jherrlin.music-theory.definitions/chords))
   :c
   :major
   triad #_seventh)
  )

{:minor
 #:scale{:id        :minor,
         :intervals ["1" "2" "b3" "4" "5" "b6" "b7"],
         :indexes   [0 2 3 5 7 8 10],
         :title     "minor",
         :order     2}
 :major
 #:scale{:id        :major,
         :intervals ["1" "2" "3" "4" "5" "6" "7"],
         :indexes   [0    2   4   5   7   9  11],
         :title     "major",
         :order     1}}

(defn harmonization-str [xs]
  (str
   "  T = Tonic (stable), S = Subdominant (leaving), D = Dominant (back home)"
   "\n\n"
   (->> xs (map (comp #(fformat "  %-10s" %) str :harmonization/index)) (str/join) (str/trim))
   "\n"
   (->> xs (map (comp #(fformat "  %-10s" %) str :harmonization/position)) (str/join) (str/trim))
   "\n"
   (->> xs (map (comp #(fformat "  %-10s" %) str :harmonization/mode-str)) (str/join) (str/trim))
   "\n"
   (->> xs (map (comp #(fformat "  %-10s" %) str :harmonization/family-str)) (str/join) (str/trim))
   "\n"
   (->> xs (map (comp #(fformat "  %-10s" %) str :chord-name)) (str/join) (str/trim))))

(comment
  (->> (gen-harmonization
        @v4.se.jherrlin.music-theory.definitions/scales
        @v4.se.jherrlin.music-theory.definitions/chords
        :c
        :major
        triad)
       (harmonization-str)
       (println)))

;; REPL stuff below line

(let [tones     [:d :f# :a]
      tones-set (set tones)]
  (->> (all-tones)
       (rotate-until #(% :d#))
       (map (fn [tone]
              (cond-> {:tone tone}
                (seq (set/intersection tones-set tone))
                (assoc :match true))))))

(set/intersection #{:d :f# :a} #{:gb :f#})
(set/intersection #{:d :f# :a} #{:gb})

;; Find the chord, cartesian product of all chords and key-of
;; (->> (for [{:chord/keys [intervals] :as chord} (vals @v2.se.jherrlin.music-theory.definitions/chords-atom)
;;            tone                                (apply concat (all-tones))]
;;        (let [tones (intervals->tones
;;                     (all-tones)
;;                     sharp-or-flat
;;                     rotate-until
;;                     tone
;;                     intervals)]
;;          (assoc chord :key-of tone :tones (set tones))))
;;      (group-by :tones)
;;      ;; (map (fn [[a b]] [a (count b)]))
;;      ;; (sort-by  second #(compare %2 %1))
;;      )

(defn define-chord
  ([id intervals-str]
   (define-chord id {} intervals-str))
  ([id {:keys [sufix chord] :as meta-data} intervals-str]
   {:pre [(uuid? id) (map? meta-data) (string? intervals-str)]}
   (let [intervals' (->> (re-seq (re-pattern models.chord/regex) intervals-str)
                         (vec))
         indexes    (intervals/functions-to-semitones intervals')
         name'      (-> chord
                        name
                        (str/replace "-" " "))
         types      (let [intervals (set intervals')]
                      (cond-> #{}
                        (intervals "3")                      (conj :major)
                        (intervals "b3")                     (conj :minor)
                        (intervals "b7")                     (conj :dominant)
                        (set/subset? #{"b3" "b5"} intervals) (conj :diminished)
                        (-> (count intervals) (= 3))         (conj :triad)))
         chord      (merge
                     {:id              id
                      :type            :chord
                      :chord/chord     chord
                      :chord/intervals intervals'
                      :chord/indexes   indexes
                      :chord/name      name'
                      :chord/sufix     sufix
                      :chord/types     types}
                     (->> meta-data
                          (map (fn [[k v]]
                                 [(->> k name (str "chord/") keyword) v]))
                          (into {})))]
     (if (models.chord/valid-chord? chord)
       chord
       (throw (ex-info "Chord is not valid" (models.chord/explain-chord chord)))))))

(define-chord
  (random-uuid)
  {:chord        :major
   :sufix        ""
   :explanation  "major"
   :display-text "major"}
  "1 3 5")

(defn define-scale
  ([id scale intervals-str]
   (define-scale id scale {} intervals-str))
  ([id scale meta-data intervals-str]
   (let [intervals' (->> (re-seq (re-pattern models.chord/regex) intervals-str)
                         (vec))
         indexes    (intervals/functions-to-semitones intervals')
         scale'      (merge
                     {:id              id
                      :type            :scale
                      :scale/scale     scale
                      :scale/intervals intervals'
                      :scale/indexes   indexes}
                     (->> meta-data
                          (map (fn [[k v]]
                                 [(->> k name (str "scale/") keyword) v]))
                          (into {})))]
     (if (models.scale/valid-scale? scale')
       scale'
       (throw (ex-info "Scale is not valid" (models.scale/explain-scale scale')))))))

(define-scale
  (random-uuid)
  #{:ionian :major}
  "1, 2, 3, 4, 5, 6, 7")

(defn define-pattern
  ([id pattern]
   (define-pattern id {} pattern))
  ([id {:keys [tuning type belongs-to] :as meta-data} pattern]
   (let [pattern'   (->> pattern
                         (intevals-string->intervals-matrix)
                         (trim-matrix))
         meta-data  (->> meta-data
                         (map (fn [[k v]]
                                [(->> k name (str "fretboard-pattern/") keyword) v]))
                         (into {}))
         ;; On what strings are the pattern defined. Mainly used for triads.
         on-strings (->> pattern'
                         (map-indexed vector)
                         (vec)
                         (filter (fn [[string-idx intervals-on-string]]
                                   (some seq intervals-on-string)))
                         (map (fn [[string-idx _]] string-idx))
                         (vec))
         inversion? (->> pattern'
                         (reverse)
                         (apply concat)
                         (remove nil?)
                         (first)
                         (= "1")
                         (not))
         pattern*   (merge
                     {:fretboard-pattern/order 1000}
                     meta-data
                     {:id                           id
                      :type                         :pattern
                      :fretboard-pattern/belongs-to belongs-to
                      :fretboard-pattern/type       type
                      :fretboard-pattern/tuning     tuning
                      :fretboard-pattern/pattern    pattern'
                      :fretboard-pattern/str        pattern
                      :fretboard-pattern/inversion? inversion?
                      :fretboard-pattern/on-strings on-strings})]
     (if (models.fretboard-pattern/validate-fretboard-pattern? pattern*)
       pattern*
       (throw (ex-info "Pattern is not valid" (models.fretboard-pattern/explain-fretboard-pattern pattern*)))))))

(define-pattern (random-uuid)
  {:belongs-to :major
   :name   :major
   :chord  :major
   :tuning [:e :b :g :d :a :e]
   :type   :chord}
  "3   -
   -   1
   5   -
   -   -
   -   -
   -   -")

(defn chords-map [chords]
  (->> (for [root                                              (->> (all-tones)
                                                                    (apply concat))
             {:chord/keys [sufix intervals indexes] :as chord} (->> #_@v4.se.jherrlin.music-theory.definitions/chords
                                                                chords
                                                                    vals)]
         (assoc chord
                :root root
                :name (str (-> root name str/lower-case) sufix)
                :interval-tones (tones-by-key-and-intervals root intervals)
                :index-tones (tones-by-key-and-indexes root indexes)))
       (map (juxt :name identity))
       (into {})))

(comment
  (chords-map
   @v5.se.jherrlin.music-theory.definitions/chords)
  )

(defn chord-by-name [chords chord-name]
  (get (chords-map chords) chord-name))

(comment
  (chord-by-name @v4.se.jherrlin.music-theory.definitions/chords "dm7"))

(defn chords-by-names [chords chord-names]
  (map (partial chord-by-name chords) chord-names))

(comment
  (chords-by-names @v4.se.jherrlin.music-theory.definitions/chords ["dm7" "g7" "cmaj7"]))

(defn read-incomming-chord-names [s]
  (->> (str/split s #"[ |,-]")
       (remove str/blank?)
       (map str/lower-case)))

(defn scale-match-score
  "How well does a chord"
  [scale-tones chord-tones]
  {:pre [(set? scale-tones)
         (set? chord-tones)]}
  (->> (set/intersection scale-tones chord-tones)
       (count)))

(scale-match-score #{:a :b :c} #{:a :b})

(defn filter-map-by-namespace-key
  [namespace-kw m]
  {:pre [(keyword? namespace-kw)
         (map? m)]}
  (->> m
       (filter (comp #{namespace-kw} keyword namespace first))
       (into {})))

(->> {:match-score/first-chord-root 1,
      :chord/types                  #{:triad :minor},
      :root                         :a,
      :match-score/scale            3}
     (filter-map-by-namespace-key :match-score))




;; 1. Find chord tones and data
;; 2. Find scale with most tones in common with the chords
;; 3. Match scale harmonization

(comment
  (let [intervals         ["1" "2" "3" "4" "5" "6" "7"]
      tones-to-interval (->> (tones-by-key-and-intervals
                              :d
                              intervals)
                             (map (fn [a b]
                                    {b a})
                                  intervals)

                             (apply merge))
      tones-in-original (->> (-> "f# a a b a a b c# d d e d c# b c# d e d c#"
                                 (str/split #"\s"))
                             (map keyword))]
  (->> tones-in-original
       (mapv #(get tones-to-interval %))))
  )
