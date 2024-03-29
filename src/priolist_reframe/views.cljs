(ns priolist-reframe.views
  (:require
   [re-frame.core :as rf]
   [priolist-reframe.events :as events]
   [priolist-reframe.subs :as subs]))

(defn- entries-panel [entries]
  (let [current-entry (rf/subscribe [::subs/current-entry])
        emit-update-current-entry (fn [e] (rf/dispatch [::events/update-current-entry (-> e .-target .-value)]))
        emit-add-entry #(rf/dispatch [::events/add-entry @current-entry])]
    [:nav.panel
     [:p.panel-heading "Choices"]
     [:div.panel-block
        [:input.input {:type "text"
                       :on-key-down #(when (= (.-key %) "Enter") (rf/dispatch [::events/add-entry @current-entry]))
                       :on-change emit-update-current-entry
                       :value @current-entry}]
      [:button.button.is-info {:on-click emit-add-entry} "Add Choice"]]
     (map (fn [entry] [:div.panel-block
                       [:div.tile entry]
                       [:button.button.is-danger.is-one-fifths.is-small.is-light {:on-click #(rf/dispatch [::events/remove-entry entry])} "Remove"]]) entries)]))

(defn phase-entries-panel []
  (let [entries (rf/subscribe [::subs/entries])
        button-decide (if (> (count @entries) 1) :button.button.is-primary.is-fullwidth :button.button.is-primary.is-fullwidth.is-static)]
    [:div
     [:h2.subtitle "Step 1/3: Your list of choices"]
     (entries-panel @entries)
     [:div.columns
      [:div.column [:button.button.is-info.is-fullwidth {:on-click #(rf/dispatch [::events/start-import])} "Import"]]
      [:div.column [:button.button.is-danger.is-fullwidth {:on-click #(rf/dispatch [::events/clear-entries])} "Clear"]]
      [:div.column [button-decide {:on-click #(rf/dispatch [::events/start-decide])} "Decide"]]]]))

(defn phase-decide-panel []
  (let [entry-a (rf/subscribe [::subs/entry-a])
        entry-b (rf/subscribe [::subs/entry-b])]
    [:div
     [:h2.subtitle "Step 2/3: Select one preferred from two choices"]
     [:div.columns
      [:div.column [:div.button.is-large.is-primary.is-fullwidth {:on-click #(rf/dispatch [::events/select-a])} @entry-a]]
      [:div.column [:div.button.is-large.is-link.is-fullwidth {:on-click #(rf/dispatch [::events/select-b])} @entry-b]]]]))

(defn phase-result-panel []
  (let [entry-a (rf/subscribe [::subs/entry-a])]
    [:div
     [:h2.subtitle "Step 3/3: Winning choice"]
     [:div.notification.is-primary @entry-a]
     [:button.button.is-primary {:on-click #(rf/dispatch [::events/initialize-db])} "Restart"]]))

(defn modal-import-panel []
  (let [phase (rf/subscribe [::subs/phase])
        import-text (rf/subscribe [::subs/import-text])
        emit-update-import (fn [e] (rf/dispatch [::events/update-import-text (-> e .-target .-value)]))
        emit-reset-import  (fn [_] (rf/dispatch [::events/update-import-text ""]))
        modal (if (= @phase :phase-import) :div.modal.is-active :div.modal)]
  [modal
   [:div.modal-background
    [:div.modal-content [:div.box
                         [:label.label "Each line is one choice"]
                         [:textarea.textarea {:on-change emit-update-import
                                              :placeholder "Some Choice 1\nSome Choice 2\nSome Choice 3\n...\n" :value @import-text}]
                         [:div.columns
                          [:div.column [:button.button.is-link.is-fullwidth {:on-click #(rf/dispatch [::events/end-import])} "Import"]]
                          [:div.column [:button.button.is-primary.is-fullwidth {:on-click emit-reset-import} "Clear"]]
                          [:div.column [:button.button.is-danger.is-fullwidth {:on-click #(rf/dispatch [::events/cancel-import])} "Cancel"]]]]]]]))

(defn main-panel []
  (let [phase (rf/subscribe [::subs/phase])]
    [:div.container
     (modal-import-panel)
     [:div.box
      [:h1.title "Choice Terminator"]
       (case @phase
         :phase-import  (phase-entries-panel)
         :phase-entries (phase-entries-panel)
         :phase-decide (phase-decide-panel)
         :phase-result (phase-result-panel)
         :default (phase-entries-panel))]]))
