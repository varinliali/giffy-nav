(ns giffy-nav.components.search-bar
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [subscribe dispatch]]
   [cljsjs.react-debounce-input]
   ))

(declare styles)

(defn on-change [term]
  (let [search-term (-> term .-target .-value)]
    (#(dispatch [:search-input-change search-term]))))


(def debounceJS (r/adapt-react-class js/DebounceInput))
(defn search-bar [props]
  [debounceJS {:type "text"
               :value @(subscribe [:search-input]) 
               :placeholder "Search gifs you like"
               :minLength 2
               ;:style (:search-bar styles)
               :debounceTimeout 300
               :on-change on-change}])

(def styles {:search-bar {:width 200
                          :height 30
                          :paddingLeft 10
                          :paddingRight 10}})

