(ns giffy-nav.screens.login
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch subscribe]]))
    

(def default-input {:email {:value "" :touched false :error nil}
                    :password {:value "" :touched false :error nil}})

(def input (r/atom default-input))

(defn get-key [key name] 
  (get-in @input [(keyword name) key]))
 

(defn set-error [key msg]
  (swap! input assoc-in [(keyword key) :error] msg))

(defn validate [name]
  (let [error (get-key :error name)
        value (get-key :value name)
        touched (get-key :touched name)
        pattern #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"]

    (case name
      "email" (cond
                (= (count value) 0)
                  (set-error "email" "Email can't be null")
                (not= true (string? value) (string? (re-matches pattern value)))
                  (set-error "email" "incorrect email")
                :else (set-error "email" nil))
      "password" (if
                   (= (count value) 0) (set-error "password" "Password can't be null") 
                   (set-error "password" nil)))))



(defn is-valid? [keyval]
  (let [[_ val] keyval]
    (or (= (count (-> val :value)) 0) (not= nil (-> val :error)))))

(defn submit-form [e]
  (.preventDefault e)
  
  (doseq [key (keys @input)] (swap! input assoc-in [key :touched] true))
  (doseq [key (keys @input)] (validate (name key)))

  (when (= 0 (count (keys (filter #(is-valid? %) @input)))) 
    (#(dispatch [:sign-in-user @input]))
    (reset! input default-input)))
   

(defn on-change [e name]
  ;(.log js/console e.target)
  (let [value (-> e .-target .-value)]
    (swap! input assoc-in [(keyword name) :value] value)
    (validate name)))

(defn custom-input [{:keys [label name type]}]
  (let [value (get-key :value name)
        error (get-key :error name)
        touched (get-key :touched name)] 
    [:fieldset.form-group {:class (if (and (not= error nil) touched) "has-error" "")}
     [:label.control-label label]
     [:div
      [:input.form-control {:placeholder label
                            :type type
                            :value value
                            :on-change #(on-change % name)
                            :on-focus #(swap! input assoc-in [(keyword name) :touched] true)}]
      (when (and (not= error nil) touched) [:span.help-block error])]]))


(defn login []
  [:div.container>div.col-md-6.col-md-offset-3 
    [:h2.text-center "Log In"]

    (when-not (= @(subscribe [:error]) nil)
     [:div.alert.alert-danger
      @(subscribe [:error])])

    [:form {:on-submit submit-form}   
     [custom-input 
      {:type "text" :name "email" :label "Email"}] 
     [custom-input 
      {:type "password" :name "password" :label "Password"}]
     [:button.btn.btn-primary {:action "submit"} "Sign In"]]])
