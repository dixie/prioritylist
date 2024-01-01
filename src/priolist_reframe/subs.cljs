(ns priolist-reframe.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::phase
 (fn [db]
   (:phase db)))

(rf/reg-sub
 ::current-entry
 (fn [db]
   (:current-entry db)))

(rf/reg-sub
 ::entries
 (fn [db]
   (:entries db)))

(rf/reg-sub
 ::entry-a
 (fn [db]
   (:entry-a db)))

(rf/reg-sub
 ::entry-b
 (fn [db]
   (:entry-b db)))
