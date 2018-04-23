(ns giffy-nav.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 :search-input
 (fn [db _]
   (:search-input db)))

(re-frame/reg-sub
 :gifs
 (fn [db _]
   (:gifs db)))

(re-frame/reg-sub
  :modal-is-open
  (fn [db _]
    (:modal-is-open db)))

(re-frame/reg-sub
  :selected-gif
  (fn [db _]
    (:selected-gif db)))

(re-frame/reg-sub
  :authenticated
  (fn [db _]
    (:authenticated db)))

(re-frame/reg-sub
  :user
  (fn [db _]
    (:user db)))

(re-frame/reg-sub
  :error
  (fn [db _]
    (:error db)))

(re-frame/reg-sub
  :loading
  (fn [db _]
    (:loading db)))

(re-frame/reg-sub
  :favorite-gifs
  (fn [db _]
    (:favorite-gifs db)))
