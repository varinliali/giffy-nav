(ns giffy-nav.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [cljsjs.firebase]
            [giffy-nav.secret :refer [fb-config]]
            [giffy-nav.events :as events]
            [giffy-nav.routes :as routes]
            [giffy-nav.views :as views]
            [giffy-nav.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (js/firebase.initializeApp (clj->js fb-config))
  (mount-root))
