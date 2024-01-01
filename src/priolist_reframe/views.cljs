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
    [:div.block
     [:div.columns
      [:div.column
       [:input.input {:type "text"
                :on-key-down #(when (= (.-key %) "Enter") (re-frame/dispatch [::events/add-entry @current-entry]))
                :on-change emit-update-current-entry
                :value @current-entry}]]
      [:div.column
       [:button.button.is-success {:on-click emit-add-entry} "Add Choice"]]]
     (map (fn [entry] [:div entry]) entries)]))

(defn phase-entries-panel []
  (let [entries (re-frame/subscribe [::subs/entries])]
    [:div
     [:h2.subtitle "Create list of choices"]
     (entries-panel @entries)
     [:button.button.is-primary {:on-click #(re-frame/dispatch [::events/start-decide])} "Decide"]]))

(defn phase-decide-panel []
  (let [entry-a (re-frame/subscribe [::subs/entry-a])
        entry-b (re-frame/subscribe [::subs/entry-b])]
    [:div
     [:h2.subtitle "Dedice between two options"]
     [:div.block
      [:div.button.is-large.is-primary {:on-click #(re-frame/dispatch [::events/select-a])} @entry-a]
      [:div.button.is-large.is-link {:on-click #(re-frame/dispatch [::events/select-b])} @entry-b]]]))

(defn phase-result-panel []
  (let [entry-a (re-frame/subscribe [::subs/entry-a])]
    [:div
     [:h2.subtitle "Winning choice"]
     [:div.notification.is-primary @entry-a]
     [:button.button.is-primary {:on-click #(re-frame/dispatch [::events/initialize-db])} "Restart"]]))

(defn main-panel []
  (let [phase (re-frame/subscribe [::subs/phase])]
    [:div.container
     [:div.box
      [:h1.title "Choice Master"]
       (case @phase
         :phase-entries (phase-entries-panel)
         :phase-decide (phase-decide-panel)
         :phase-result (phase-result-panel)
         :default (phase-entries-panel))]]))
