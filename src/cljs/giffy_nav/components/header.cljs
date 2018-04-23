(ns giffy-nav.components.header
  (:require
    [re-frame.core :refer [dispatch subscribe]]))

(defn sign-out []
  (#(dispatch [:sign-out-user])))

(defn auth-links []
   (if @(subscribe [:authenticated])
    [:ul.nav.navbar-nav.navbar-right 
     [:li.nav-item [:a.nav-link {:href "#/favorites"} "My Favorites"]]
     [:li.nav-item [:a.nav-link {:href "#/login" :on-click sign-out} "Sign Out"]]]
    [:ul.nav.navbar-nav.navbar-right
     [:li.nav-item [:a.nav-link {:href "#/login"} "Login"]]
     [:li.nav-item [:a.nav-link {:href "#/signup"} "Sign Up"]]])) 


(defn header []
  [:nav.navbar.navbar-default
   [:div.container-fluid
    [:div.navbar-header
     [:a.navbar-brand {:href "#/"} "React2Gifs"]]
    [auth-links]]])
     
     
