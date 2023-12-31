(ns priolist-reframe.views
  (:require
   [re-frame.core :as re-frame]
   [priolist-reframe.events :as events]
   [priolist-reframe.subs :as subs]
   ))

(defn entries-panel [entries]
  (let [current-entry (re-frame/subscribe [::subs/current-entry])
        emit-update-current-entry (fn [e] (re-frame/dispatch [::events/update-current-entry (-> e .-target .-value)]))
        emit-add-entry #(re-frame/dispatch [::events/add-entry @current-entry])]
    [:div {:class "box"}
     [:div {:class "columns"}
      [:div {:class "column"}
       [:input {:class "input"
                :type "text"
                :on-change emit-update-current-entry
                :value @current-entry}]]
      [:div {:class "column"}
       [:button {:class "button is-success"
                 :on-click emit-add-entry} "Add"]]]
     (map (fn [entry] [:div {:class ""} entry]) entries)]))

(defn phase-entries-panel []
  (let [entries (re-frame/subscribe [::subs/entries])]
     [:div
      (entries-panel @entries)
      [:button {:class "button is-primary"} "Decide"]]))

(defn main-panel []
  (let [phase (re-frame/subscribe [::subs/phase])]
    [:div {:class "container"}
     [:div {:class "box"}
      [:h1 {:class "title"} "Priority List"]
      (case @phase
        :phase-entries (phase-entries-panel)
        :default (phase-entries-panel))]]))
