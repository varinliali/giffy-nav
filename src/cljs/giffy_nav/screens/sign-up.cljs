(ns giffy-nav.screens.sign-up
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch subscribe]]))
    


(def default-input {:email {:value "" :touched false :error nil}
                    :password {:value "" :touched false :error nil}
                    :password-confirmation {:touched false :value "" :error nil}})

(def input (r/atom default-input))


(defn get-key [key name] 
  (get-in @input [(keyword name) key]))
 
(defn set-error
  ([key msg] (swap! input assoc-in [(keyword key) :error] msg))
  ([key key-2 msg] 
   (swap! input assoc-in [(keyword key) :error] msg)
   (swap! input assoc-in [(keyword key-2) :error] msg)))


(defn validate [name]
  (let [error (get-key :error name)
        value (get-key :value name)
        password (get-key :value "password")
        conf (get-key :value "password-confirmation")
        pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]
    (case name
      "email" (cond
                (= (count value) 0) (set-error "email" "Email can't be null")
                (not= true (string? value) (string? (re-matches pattern value))) (set-error "email" "incorrect email")
                :else (set-error "email" nil))
      "password" (cond 
                   (= (count value) 0) (set-error "password" "Password can't be null")
                   (and (not= (count conf) 0) (not= value conf)) (set-error "password" "Passwords don't match")
                   :else (set-error "password" "password-confirmation" nil))
      "password-confirmation" (cond 
                                (= (count value) 0) (set-error "password-confirmation" "Password-confirmation can't be null")
                                (and (not= (count password) 0) (not= value password)) (set-error "password-confirmation" "Passwords don't match")
                                :else (set-error "password-confirmation" nil)))))

(defn is-valid? [keyval]
  (let [[_ val] keyval]
    (or (= (count (-> val :value)) 0) (not= nil (-> val :error)))))  
          

(defn submit-form [e]
  (.preventDefault e)

  (doseq [key (keys @input)] (swap! input assoc-in [key :touched] true))
  (doseq [key (keys @input)] (validate (name key)))
                                
                                
  (when (= 0 (count (keys (filter #(is-valid? %) @input))))
    (#(dispatch [:register-user @input]))
    (reset! input default-input)))

(defn on-change [e name]
  ;(.log js/console e.target)
  (let [value (-> e .-target .-value)]
    (swap! input assoc-in [(keyword name) :value] value)
    (validate name)))

(defn custom-input [{:keys [label name type]}]
  (let [error (get-key :error name)
        touched (get-key :touched name)
        value (get-key :value name)]

  [:fieldset.form-group {:class (if (and (not= error nil) touched) "has-error" "" )}
   [:label.control-label label]
   [:div
    [:input.form-control {:placeholder label
                          :type type
                          :value value
                          :on-change #(on-change % name)
                          :on-focus #(swap! input assoc-in [(keyword name) :touched] true)}]
    (when (and (not= error nil) touched) [:span.help-block error])]]))

(defn sign-up []
  [:div.container>div.col-md-6.col-md-offset-3
   [:h2.text-center "Sign Up"]
   
   (when-not (= @(subscribe [:error]) nil)
     [:div.alert.alert-danger
      @(subscribe [:error])])
   
   [:form {:on-submit submit-form}
    [custom-input 
     {:label "Email" :name "email" :type "text"}]
    [custom-input 
     {:label "Password" :name "password" :type "password"}]
    [custom-input 
     {:label "Password Confirmation" :name "password-confirmation" :type "password"}]
    [:button.btn.btn-primary {:action "submit"} "Sign up"]]])
