(ns testify.core
    (:use testify.dirs)
    (:use testify.fileops)
    (:use compojure.core)
    (:require 
        [compojure.route :as route]
        [compojure.handler :as handler]
        )

)


;UTILITY
;check input if key or value
;BEWARE! HERE THERE BE SIDE EFFECTS!
(defn checkkeys [inputmap]

    (doseq [[paramname paramvalue] inputmap]
       (println (str paramname " " paramvalue) )
    )
   "<h1>Done!</h1>"
)

;Ok, 9/5/2011
; putting & here encased the rest of the arguments as a LIST
; this threw the serialization attempt off, big time. No &, its OK! PTL! GOD!
(defn dump-map [fname  inputmap]
    (binding [*print-dup* true]
        ( spit (str "resources/pages/" fname) inputmap) 
        (println inputmap)
        (str "<h1>Echoed response test map to " fname  " </h1>")
    )
)


;UTILITY
; macro 
(defmacro frfirst [colls] 
    `(let [colls# ~colls] 
        (first (rest (first colls#) ) ) 
    ) 
)

(defn subsw [submap target]
    (cond
        ;if the list is all eaten up, return the result!
        (= submap ()) target
        ;TODO: check target viability here?       
        :else 
            (subsw (rest submap) 
                (. target replaceAll 
                    (str (ffirst submap) ) (str (frfirst submap) ) 
                )
            )
    )
)
; given a key-value mapset, substitute everything in the given string
(defn simpl-subs [fname]
    (cond
        ;todo: add exception class?
        (nil? fname) (throw (Exception. (str "no page file name given! (Check fname get param)") ) )
        :else (let [submap 
                
                ( load-file (str  "resources/pages/" fname))              
             ]
                      
            ;DEBUG
            ;(str submap)
            ;(get submap :template)
            
            (cond 
                (nil? (get submap :template)) (throw (Exception. (str "template file name in page file!") ) )
                :else (let [raw-templ 
                        (slurp 
                            (str "resources/templates/" (get submap :template)) 
                        )
                     ]
                     ;DEBUG
                     ;(str raw-templ)             
                    (subsw submap raw-templ)
                )
            
            )
            
        ) 
    )
)


(defn strprint [inmap]
   (str inmap)
)

(defroutes main-routes
  ;match main
  (GET "/admin/deletepage" [fname] (testify.fileops/deletepage fname) )
  (GET "/admin" [] (testify.dirs/admin-pages) )
  (GET "/reader" [fname] (simpl-subs fname) )
  (POST "/writer"  [fname & everything] (dump-map fname everything) )
  (GET "/" [] (testify.dirs/list-pages) )

  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
