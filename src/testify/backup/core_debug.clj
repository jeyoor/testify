(ns testify.core
    (:use 
        compojure.core
        ring.adapter.jetty
    )
    (:require         
        [compojure.route :as route]
        [compojure.handler :as handler]
        [testify.middleware :as middleware]
        [testify.process :as process]
        [testify.process.dyn-html :as dyn-html]        
        [testify.persist :as persist]
        [testify.debug :as debug]
        
        
    )
)

(defroutes main-routes

    (GET "/admin/template/preview" [tname] (process/preview-template tname) )
    (GET "/admin/template/form" [] (process/form-template) )
            ;use the "save" param but don't pass it [drop btn name before saving everything]
    (POST "/admin/template/save" [save TemplateName thtml] (process/save-template TemplateName thtml))
    (POST "/admin/template/delete" [tname] (process/delete-template tname))
    (GET "/admin/resource/form" [] (process/form-resource) )
    (POST "/admin/resource/save" [save ResourceName rfile] (process/save-resource ResourceName rfile))
    (POST "/admin/resource/delete" [rname] (process/delete-resource rname))
    (POST "/admin/page/delete" [] (process/admin-delete-page))
    (GET "/admin/template" [] (process/admin-template) )


    (GET "/admin/resource" [] (process/admin-resource) )
    
        ;TODO: ONLY IN DEBUG
    ;(when (middleware/development?) 
        (GET "/debug/fetch" [key] (debug/fetchy key ) ) 
    ;)
    ;(when (middleware/development?) 
        (GET "/debug/store" [key val] (str (debug/storey key val) ) ) 
    ;)
    (when (middleware/development?) (GET "/debug/bfetch" [kay] (debug/bfetch kay) ) )
    (when (middleware/development?) (GET "/debug/bstore" [kay] (debug/bstore kay) )    )
    (when (middleware/development?) (GET "/debug/restore/resource" [kay] (persist/restore-resource)))
    (when (middleware/development?) (GET "/debug/testinput" [] (dyn-html/testy2))) 
    
        ;debug
    ;TODO FIXTHIS, write macro?
    (when (middleware/development?) (GET "/debug/echo" [& params] (debug/echo params) ) )
    (when (middleware/development?) (GET "/debug/dump" [prefix] (str (debug/dumpy prefix) ) ) )
    
    ;pages
    
    (GET "/page/form" [tname] (process/form-page tname))    
    (GET "/page/view" [pname] (process/view-page pname))
    (POST "/page/save" [save PageName template & savedata] (process/save-page PageName template savedata))
    (POST "/page/delete" [pname token] (process/delete-page))
    ;misc
    (GET "/admin" [] (process/admin-page) )    
    (GET "/on_dev" [] (str (middleware/development?)))
    (GET "/template" [] (process/list-template))
    (GET "/" [] (process/list-page) )
    (route/resources "/")
    (route/not-found "<h3>Page not found</h3>")
)

(def app
    (if (middleware/development?)
        ;production blocks error stacktraces from printing, but logs'm

            ;thread the object instead of the symbol
        (->
            #'main-routes
            (handler/site)
        )
        (->
            #'main-routes
            (handler/site)
            (middleware/wrap-errorlog)
            ;TODO: 
            ;(middleware/wrap-failsafe)        
        )
    )
)

(defn -main []
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty app {:port port})))
