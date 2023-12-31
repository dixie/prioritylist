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
