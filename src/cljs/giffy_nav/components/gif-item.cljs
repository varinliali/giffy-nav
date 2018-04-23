(ns giffy-nav.components.gif-item
  (:require   
    [reagent.core :as r]
    [re-frame.core :refer [dispatch]]))

(defn favorite-heart [gif favorite?]
  (if favorite?
    [:i.favorite.fa.fa-heart {:on-click #(dispatch [:unfavorite-gif gif])}]
    [:i.favorite.fa.fa-heart-o {:on-click #(dispatch [:favorite-gif gif])}]))

(defn gif-item [gif favorite?]
  (let [url (get-in gif [:images :downsized :url])]
    [:div.gif-item 
     [favorite-heart gif favorite?]
     [:img {:src url :on-click #(dispatch [:open-modal gif])}]]))

