(ns giffy-nav.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]
            [giffy-nav.subs :as subs]
            [giffy-nav.components.header :refer [header]]
            [giffy-nav.components.gif-modal :refer [gif-modal]]
            [giffy-nav.components.search-bar :refer [search-bar]]
            [giffy-nav.components.gif-list :refer [gif-list]]
            [giffy-nav.screens.login :refer [login]]
            [giffy-nav.screens.sign-up :refer [sign-up]]
            [giffy-nav.screens.favorites :refer [favorites]]
            ))

;; home
(defn home-panel []
  (r/create-class
    {:component-did-mount
     #(re-frame/dispatch [:fetch-favorite-gifs])
     :display-name "home-panel"
     :reagent-render
     (fn []
       [:div
          [search-bar]
          [gif-modal]
          [gif-list @(re-frame/subscribe [:gifs])]])}))

;; login
(defn login-panel []
  (r/create-class
    {:component-did-mount
     #(re-frame/dispatch [:verify-auth])
     :display-name "login panel"
     :reagent-render
      (fn []
        [:div 
          [login]])}))

;; signup
(defn signup-panel []
  [:div 
   [sign-up]])

;; favorites
(defn favorites-panel []
  [favorites])

;; main
(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :login-panel [login-panel]
    :signup-panel [signup-panel]
    :favorites-panel [favorites-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [:div
     [header] 
     [show-panel @active-panel]]))



