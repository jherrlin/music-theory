(ns se.jherrlin.music-theory.webapp.menu
  (:require
   [re-frame.core :as re-frame]))


(defn menu [component]
  (let [current-route @(re-frame/subscribe [:current-route])
        path-params   @(re-frame/subscribe [:path-params])
        query-params  @(re-frame/subscribe [:query-params])]
    [:<>
     (when component
       [component])]))
