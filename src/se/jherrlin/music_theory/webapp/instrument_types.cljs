(ns se.jherrlin.music-theory.webapp.instrument-types)




(defmulti chord-view :instrument-type)

(defmethod chord-view :keyboard [_]
  [:div
   [:h2 "keyboard"]])

(defmethod chord-view :fretboard [_]
  [:div
   [:h2 "strings"]])
