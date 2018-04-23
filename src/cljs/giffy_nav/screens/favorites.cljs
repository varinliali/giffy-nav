(ns giffy-nav.screens.favorites
  (:require
    [re-frame.core :refer [subscribe]]
    [giffy-nav.components.gif-list :refer [gif-list]]
    [giffy-nav.components.gif-modal :refer [gif-modal]]))

(defn favorites []
  [:div
   [gif-list @(subscribe [:favorite-gifs]) true]
   [gif-modal]])
