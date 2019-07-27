(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))


(defn greet [req]
 {:status 200 :body "Hello, Grego!" :headers {}})

(defn about [req]
 {:status 200 :body "I'm Gregorio Cabral and I created this app.\nFollow mi on twitter @greggocabral" :headers {}})

(defn goodbye [req]
 {:status 200 :body "Goodbye, cruel world" :headers {}})

(defn request [req]
 {:status 200 :body (str req) :headers {}})

(defn yo [req]
 {:status 200
  :body (format "Yo! %s!" (get-in req [:route-params :name]))
  :headers {}})

(def ops
  {"+" +
   "-" -
   ":" /
   "*" *})

(defn calc [req]
  (let [{:keys [num1 num2 op]} (get req :route-params)
        f (get ops op)]
    (if f
      {:status 200
       :body (str (f (Integer. num1) (Integer. num2)))
       :headers {}}
      {:status 404
       :body "Operand not found"
       :headers {}})))

(defroutes app
  (GET "/" [] greet)
  (GET "/about" [] about)
  (GET "/goodbye" [] goodbye)
  (GET "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:num1/:op/:num2" [] calc)
  (not-found "Page not found"))

(defn -main [port]
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload (var app))
                   {:port (Integer. port)}))
