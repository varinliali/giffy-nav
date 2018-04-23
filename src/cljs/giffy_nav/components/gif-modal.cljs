(ns giffy-nav.components.gif-modal
  (:require 
    [re-frame.core :refer [subscribe dispatch]]
    [cljsjs.react-modal]
    [reagent.core :as r]))


(defn request-close []
  (print "REQUEST CLOSE")
 (#(dispatch [:close-modal])))


(def modalJS (r/adapt-react-class js/ReactModal))
(defn modal [children]
 (let [modal-is-open @(subscribe [:modal-is-open])] 
  [modalJS {:contentLabel "a Gif from giffy!"
            :isOpen modal-is-open
            :onRequestClose request-close}
   children]))


(defn gif-modal []
  (let [selected-gif @(subscribe [:selected-gif]) 
        url (get-in selected-gif [:images :original :url])]
    [modal
     [:div {:class "gif-modal"}
      [:img {:src url}]
      [:p [:strong] "Source: "
       [:a {:href (:source selected-gif)}]
        (:source selected-gif)]
        [:p [:strong "Rating: "
          [:a {:href (:rating selected-gif)}]
          (:rating selected-gif)]]
          [:button {:on-click request-close} "Close"]]]))


