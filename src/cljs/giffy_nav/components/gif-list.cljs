(ns giffy-nav.components.gif-list
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [subscribe]]
   [giffy-nav.components.gif-item :refer [gif-item]]
   ))

(defn gif-items [gifs]
  (doall
    (for [gif (vals gifs)]
      (let [favorite? (contains? @(subscribe [:favorite-gifs]) (:id gif))]
        ^{:key (:id gif)} [gif-item gif favorite?]))))

(defn gif-list [gifs]
  [:div.gif-list (gif-items gifs)])

