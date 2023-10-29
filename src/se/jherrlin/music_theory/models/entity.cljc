(ns se.jherrlin.music-theory.models.entity
  "An entity is something, like a chord.
  It has a key, a specific instrument and an id."
  (:require
   [malli.core :as m]
   [clojure.string :as str]))

(def Entity
  [:map
   [:instrument-type :keyword]
   [:tuning          :keyword]
   [:key-of          :keyword]
   [:id              uuid?]])

(def Entities
  [:vector
   {:min 1}
   Entity])

(def valid-entity?   (partial m/validate Entity))
(def valid-entities? (partial m/validate Entities))


(defn entity-to-string
  [{:keys [instrument-type tuning key-of id] :as entity}]
  {:pre [(valid-entity? entity)]}
  (let [instrument-type' (name instrument-type)
        tuning'          (name tuning)
        key-of'          (name key-of)
        id'              (str id)]
    (str/join "," [instrument-type' tuning' key-of' id'])))

(defn entities-to-string [es]
  (when (seq es)
    (->> es
         (map entity-to-string)
         (str/join "|"))))

(comment
  (entity-to-string
   {:instrument-type :fretboard
    :tuning          :mandolin
    :key-of          :c
    :id              #uuid "b5b5e1a8-eed8-4398-8f65-725d050aeb57"})

  (entities-to-string
   [{:instrument-type :fretboard
     :tuning          :mandolin
     :key-of          :c
     :id              #uuid "b5b5e1a8-eed8-4398-8f65-725d050aeb57"}
    {:instrument-type :fretboard
     :tuning          :mandolin
     :key-of          :c
     :id              #uuid "b5b5e1a8-eed8-4398-8f65-725d050aeb57"}])
  )

(defn string-to-entity [s]
  (let [[instrument-type tuning key-of id] (str/split s #",")
        entity
        {:instrument-type (keyword instrument-type)
         :tuning          (keyword tuning)
         :key-of          (keyword key-of)
         :id              (parse-uuid id)}]
    (if (valid-entity? entity)
      entity
      (throw
       (ex-info "String is not a valid Entity." entity)))))

(defn string-to-entities [s]
  (->> (str/split s #"\|")
       (map string-to-entity)))

(comment
  (string-to-entity
   "fretboard,mandolin,c,b5b5e1a8-eed8-4398-8f65-725d050aeb57")
  (string-to-entities
   "fretboard,mandolin,c,b5b5e1a8-eed8-4398-8f65-725d050aeb57|fretboard,mandolin,c,b5b5e1a8-eed8-4398-8f65-725d050aeb57")
  )
