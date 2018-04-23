(ns giffy-nav.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]
            [giffy-nav.events :as events]
            ))

(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" []
    (re-frame/dispatch [::events/set-active-panel :home-panel]))

  (defroute "/login" []
    (re-frame/dispatch [::events/set-active-panel :login-panel]))
  
  (defroute "/signup" []
    (re-frame/dispatch [::events/set-active-panel :signup-panel]))

  (defroute "/favorites" []
    (re-frame/dispatch [::events/set-active-panel :favorites-panel]))

  (defroute "*" []
    (re-frame/dispatch [::events/set-active-panel :home-panel]))  
  ;; --------------------
  (hook-browser-navigation!))
