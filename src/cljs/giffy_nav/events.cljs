(ns giffy-nav.events
  (:require [re-frame.core :as re-frame]
            [giffy-nav.db :as db]
            [giffy-nav.secret :refer [secret]]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [cljsjs.firebase]
            ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn [db [_ active-panel]]
   (if  (or (= active-panel :signup-panel) @(re-frame/subscribe [:authenticated]))
     (assoc db :active-panel active-panel)
     (assoc db :active-panel :login-panel))))

(defn giphy-url [term]
  (str "http://api.giphy.com/v1/gifs/search?q=" term "&api_key=" (:api-key secret)))

(re-frame/reg-event-fx
 :search-input-change
 (fn [{:keys [db]} [_ term]]
   {:http-xhrio {:method          :get
                 :uri             (giphy-url term)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:process-response]
                 :on-failure      [:bad-response]}
    :db (assoc db :search-input term)}))

(re-frame/reg-event-db
 :process-response
 (fn [db [_ gifs]]
   ;(assoc db :gifs (:data gifs))))
   (js/console.log (:data gifs))
   (assoc db :gifs (into {} (map #(identity {(:id %) %}) (:data gifs))))))

(re-frame/reg-event-db
 :bad-response
 (fn [_]
   (print "error fetching")))

(re-frame/reg-event-db
  :close-modal
  (fn [db]
    (assoc db :modal-is-open false :selected-gif nil)))

(re-frame/reg-event-db
  :open-modal
  (fn [db [_ gif]]
    (assoc db :modal-is-open true :selected-gif gif)))

(defn register-user [email password] 
  (-> js/firebase .auth 
      (.createUserWithEmailAndPassword email password)
      (.then #(re-frame/dispatch [:auth-user %])) 
      (.catch #(re-frame/dispatch [:auth-error %])))
  (re-frame/dispatch [:loading true]))

(defn sign-in-user [email password]
  (-> js/firebase .auth
      (.signInWithEmailAndPassword email password)
      (.then #(re-frame/dispatch [:auth-user %]))
      (.catch #(re-frame/dispatch [:auth-error %])))
  (re-frame/dispatch [:loading true]))

(defn sign-out-user []
  (-> js/firebase .auth .signOut)
  (re-frame/dispatch [:loading false]))

(defn verify-auth []
  (-> js/firebase .auth
    (.onAuthStateChanged #(when-not (nil? %) (re-frame/dispatch [:auth-user %]))))
  (re-frame/dispatch [:loading false]))

(re-frame/reg-event-fx
  :loading
  (fn [_ status]
    (print status)))

(defn js-obj-vals [x]
  (.. js/Object (values x)))

(defn js-obj-keys [x]
  (.. js/Object (keys x)))
 
(re-frame/reg-event-db
  :auth-error
  (fn [db [_ error]]
    (assoc db :error (second (js-obj-vals error))
              :loading false
              )))

(re-frame/reg-event-db
  :auth-user
  (fn [db [_ payload]]
    (js/console.log payload)
    (assoc db :authenticated true
              :error nil
              :active-panel :home-panel 
              :loading false
              :user {:uid (-> payload .-uid)
                     :email (-> payload .-email)})))


(re-frame/reg-event-fx
  :register-user
  (fn [{:keys [db]} [_ user]]
    (register-user (-> user :email :value) (-> user :password :value))))

(re-frame/reg-event-fx
  :sign-in-user
  (fn [{:keys [db]} [_ user]]
    (sign-in-user (-> user :email :value) (-> user :password :value))))

(re-frame/reg-event-db
  :sign-out-user
  (fn [db]
    (sign-out-user)
    (assoc db :user nil 
              :authenticated false 
              :error nil
              :active-panel :login-panel
              :gifs nil
              :favorite-gifs nil
              )))

(re-frame/reg-event-fx
  :verify-auth
  (fn []
    (verify-auth)))

(defn favorite-gif [gif] 
  (let [uid (-> js/firebase .auth .-currentUser .-uid)]
    (-> js/firebase .database 
        (.ref uid)
        (.update (clj->js {(keyword (:id gif)) gif})))
    (re-frame/dispatch [:loading true])))


(defn unfavorite-gif [gif]
  (let [uid (-> js/firebase .auth .-currentUser .-uid)]
    (-> js/firebase .database
        (.ref uid)
        (.child (:id gif))
        (.remove))
    (re-frame/dispatch [:loading true])))

(re-frame/reg-event-fx
  :favorite-gif
  (fn [{:keys [db]} [_ gif]]
    (favorite-gif gif)))

(re-frame/reg-event-fx
  :unfavorite-gif
  [re-frame/debug]
  (fn [{:keys [db]} [_ gif]]
    (unfavorite-gif gif)
    ;(print "tb deleted" (:id gif) "->" @(re-frame/subscribe [:favorite-gifs (:id gif)]))
    ))
    
; valid event type = "value", "child_added", "child_removed", "child_changed"
(defn fetch-favorite-gifs []
  (print "fetch fav")
  (let [uid (-> js/firebase .auth .-currentUser .-uid)
        path (-> js/firebase .database (.ref uid))]
    (-> path
        (.on "child_added" #(re-frame/dispatch [:favorite-gifs (-> % .val)])))
    (-> path
        (.on "child_removed" #(re-frame/dispatch [:remove-gif (-> % .val)])))
    (re-frame/dispatch [:loading true])))

(re-frame/reg-event-db
  :remove-gif
  (fn [db [_ gif]]
    (let [item (js->clj gif :keywordize-keys true)]
      (update db :favorite-gifs dissoc (:id item)))))

(re-frame/reg-event-db
  :favorite-gifs
  (fn [db [_ gif]]
    (let [item (js->clj gif :keywordize-keys true)]
      (assoc-in db [:favorite-gifs (:id item)] item))))

(re-frame/reg-event-fx
  :fetch-favorite-gifs
  (fn []
    (fetch-favorite-gifs)))







