(ns giffy-nav.db)

(def default-db
  {:name "re-frame"
   :user nil 
   :search-input ""
   :gifs nil
   :modal-is-open false
   :selected-gif nil
   :authenticated false
   :error nil
   :loading false
   :favorite-gifs nil
   })
