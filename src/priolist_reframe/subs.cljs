(ns priolist-reframe.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::phase
 (fn [db]
   (:phase db)))

(re-frame/reg-sub
 ::current-entry
 (fn [db]
   (:current-entry db)))

(re-frame/reg-sub
 ::entries
 (fn [db]
   (:entries db)))
