(ns priolist-reframe.events
  (:require
   [re-frame.core :as re-frame]
   [priolist-reframe.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::add-entry
 (fn [db [_ txt]]
   (let [new-entries (conj (:entries db) txt)]
     (-> db
         (assoc :entries new-entries)
         (assoc :current-entry "")))))

(re-frame/reg-event-db
 ::update-current-entry
 (fn [db [_ txt]]
   (assoc db :current-entry txt)))


(re-frame/reg-event-db
 ::start-decide
 (fn [db [_ _]]
   (let [[a b & remaining] (:entries db)]
     (-> db
         (assoc :entry-a a)
         (assoc :entry-b b)
         (assoc :entries remaining)
         (assoc :phase :phase-decide)
         (assoc :current-entry "")))))

(defn process-selection [db selected-entry]
  (let [entries (conj (:entries db) selected-entry)
        [a b & remaining] entries]
    (if (> (count entries) 1)
      (-> db
          (assoc :entry-a a)
          (assoc :entry-b b)
          (assoc :entries remaining))
      (-> db
          (assoc :phase :phase-result)))))

(re-frame/reg-event-db
 ::select-a
 (fn [db [_ _]]
   (process-selection db (:entry-a db))))

(re-frame/reg-event-db
 ::select-b
 (fn [db [_ _]]
   (process-selection db (:entry-b db))))
