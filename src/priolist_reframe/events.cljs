(ns priolist-reframe.events
  (:require
   [re-frame.core :as rf]
   [priolist-reframe.db :as db]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-db
 ::add-entry
 (fn [db [_ txt]]
   (let [new-entries (if (empty? txt)
                       (:entries db)
                       (conj (:entries db) txt))]
     (-> db
         (assoc :entries new-entries)
         (assoc :current-entry "")))))

(rf/reg-event-db
 ::update-current-entry
 (fn [db [_ txt]]
   (assoc db :current-entry txt)))

(rf/reg-event-db
 ::clear-entries
 (fn [db _ ]
   (assoc db :entries [])))

(rf/reg-event-db
 ::remove-entry
 (fn [db [_ txt]]
   (let [new-entries (remove #(= % txt) (:entries db))]
     (assoc db :entries new-entries))))

(rf/reg-event-db
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
  (let [entries (concat (:entries db) [selected-entry])
        [a b & remaining] entries]
    (if (> (count entries) 1)
      (-> db
          (assoc :entry-a a)
          (assoc :entry-b b)
          (assoc :entries remaining))
      (-> db
          (assoc :entry-a (first entries))
          (assoc :phase :phase-result)))))

(rf/reg-event-db
 ::select-a
 (fn [db [_ _]]
   (process-selection db (:entry-a db))))

(rf/reg-event-db
 ::select-b
 (fn [db [_ _]]
   (process-selection db (:entry-b db))))
