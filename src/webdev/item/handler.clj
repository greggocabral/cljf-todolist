(ns webdev.item.handler
  (:require [webdev.item.model :as items]
            [webdev.item.view :as render-items]))

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (items/read-items db)]
    {:status 200
     :headers {}
     :body (render-items/items-page items)}))

(defn handle-create-item! [req]
  (let [name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        db (:webdev/db req)
        item-id (items/create-item! db name description)]

    {:status 302
     :headers {"Location" "/items"}
     :body ""}))

(defn handle-update-item! [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params :item-id])
                        java.util.UUID/fromString)
        checked? (= "true" (get-in req [:params "checked"]))
        existed? (items/update-item! db item-id checked?)]
    (if existed?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :headers {}
       :body "List not found "})))

(defn handle-delete-item! [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params :item-id])
                        java.util.UUID/fromString)
        existed? (items/delete-item! db item-id)]
    (if existed?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :headers {}
       :body "List not found "})))
