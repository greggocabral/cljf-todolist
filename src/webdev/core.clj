(ns webdev.core
  (:require [webdev.item.model :as items]
            [webdev.item.handler :as items-handler])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(def db (or (System/getenv "DATABASE_URL")
            "jdbc:postgresql://localhost/webdev01"))

(defn greet [req]
 {:status 200 :body "Hello, Grego!" :headers {}})

(defn about [req]
 {:status 200 :body "I'm Gregorio Cabral and I created this app.\nFollow me on twitter @greggocabral" :headers {}})

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

(defroutes routes
  (GET "/" [] greet)
  (GET "/about" [] about)
  (GET "/goodbye" [] goodbye)
  (ANY "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:num1/:op/:num2" [] calc)

  (GET "/items" [] items-handler/handle-index-items)
  (POST "/items" [] items-handler/handle-create-item!)
  (PUT "/items/:item-id" [] items-handler/handle-update-item!)
  (DELETE "/items/:item-id" [] items-handler/handle-delete-item!)

  (not-found "Page not found"))


(defn wrap-db [handler]
  (fn [req]
    (handler (assoc req :webdev/db db))))

(defn wrap-server [handler]
  (let [server "mymac"]
    (fn [req]
      (assoc-in (handler req) [:headers "Server"] server))))

(def sim-methods {"PUT" :put
                  "DELETE" :delete})

(defn wrap-simulated-methods [handler]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(def app
  (wrap-server
    (wrap-file-info
      (wrap-resource
        (wrap-db
          (wrap-params
            (wrap-simulated-methods
              routes)))
        "static"))))

(defn -main [port]
  (items/create-table! db)
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table! db)
  (jetty/run-jetty (wrap-reload (var app))
                   {:port (Integer. port)}))
