(ns webdev.item.view
  (:require [hiccup.core :refer [html h]]
            [hiccup.page :refer [html5]]
            [webdev.web-utils :as wu]))

(defn update-item-form [item]
  (html
    [:form
     {:method "POST" :action (format "/items/%s" (:id item))}
     [:input {:type :hidden
              :name "_method"
              :value "PUT"}]
     [:input {:type :hidden
              :name "checked"
              :value (if (:checked item) "false" "true")}]
     [:div.btn-group
      (if (:checked item)
        [:input.btn.btn-primary.btn-xs
         {:type :submit
          :value "Mark not done"}]
        [:input.btn.btn-primary.btn-xs
         {:type :submit
          :value "Mark done"}])]]))


(defn delete-item-form [item]
  (html
    [:form
     {:method "POST" :action (format "/items/%s" (:id item))}
     [:input {:type :hidden
              :name "_method"
              :value "DELETE"}]
     [:div.btn-group
      [:input.btn.btn-danger.btn-xs
       {:type :submit
        :value "Delete"}]]]))

(defn new-item []
  (html
    [:form.form-horizontal
     {:method "POST" :action "/items"}
     [:div.form-group
      [:label.control-label.col-sm-2 {:for :name-input}
       "Name"]
      [:div.col-sm-8
       [:input#name-input.form-control
        {:name :name
         :placeholder "Name"}]]]
     [:div.form-group
      [:label.control-label.col-sm-2 {:for :description-input}
       "Description"]
      [:div.col-sm-8
       [:input#description-input.form-control
        {:name :description
         :placeholder "Description"}]]]
     [:div.form-group
      [:div.col-sm-8.sm-offset-2
       [:input.btn.btn-primary
        {:type :submit
         :value "New Item"}]]]]))

(defn items-page [items]
  (let [body  [:div.container
               [:div.row
                [:div.col-sm-8
                 [:h1 "Tasks"]
                 [:a {:href "/"}
                  "Go home"]]]
               [:div.row
                (if (seq items)
                  [:table.table.table-striped
                   [:thead
                    [:tr
                     [:th "Name"]
                     [:th "Description"]
                     [:th ""]
                     [:th ""]]]
                   [:tbody
                    (map (fn [item]
                           [:tr
                            [:td (h (:name item))]
                            [:td (h (:description item))]
                            [:td (update-item-form item)]
                            [:td (delete-item-form item)]])
                         items)]]
                  [:div.col-sm-offset-1 "No items in the list"])]
               [:h2 "Create a new item"]
               (new-item)]]
    (wu/base-page "Tasks - List" body)))
