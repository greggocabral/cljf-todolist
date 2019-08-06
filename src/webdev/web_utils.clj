(ns webdev.web-utils
  (:require [hiccup.core :refer [html h]]
            [hiccup.page :refer [html5]]))

(defn base-page [title body]
  (html5 {:lang :en}
         [:head
          [:title title]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/bootstrap/css/bootstrap.min.css"
                  :rel :stylesheet}]]
         [:body
          body]
         [:script {:src "/bootstrap/css/bootstrap.min.js"}]))
