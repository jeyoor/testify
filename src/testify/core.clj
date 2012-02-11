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



(defroutes page-routes
    ;Redirect to something useful
    ;(GET "/" [] )
    (GET "/form" [tname] (process/form-page tname))
    (GET "/view" [pname] (process/view-page pname))
    (POST "/save" [save PageName template & savedata] (process/save-page PageName template savedata))
    (POST "/delete" [pname token] (process/delete-page))
)

(defroutes admin-routes

    (GET "/" [] (process/admin-menu))
    (GET "/templates/preview" [tname] (process/preview-template tname))
    (GET "/template/form" [] (process/form-template))
            ;use the "save" param but don't pass it [drop btn name before saving everything]
    (POST "/template/save" [save TemplateName thtml] (process/save-template TemplateName thtml))
    (POST "/template/delete" [tname] (process/delete-template tname))
    (GET "/resource/form" [] (process/form-resource))
    (POST "/resource/save" [save ResourceName rfile] (process/save-resource ResourceName rfile))
    (POST "/resource/delete" [rname] (process/delete-resource rname))
    (GET "/template" [] (process/admin-template))
    (GET "/resource" [] (process/admin-resource))
    (GET "/page" [] (process/admin-page))
    ;pages
    (POST "/page/delete" [] (process/admin-delete-page))
)

(defroutes main-routes

    (context "/page" [] page-routes)
    (context "/admin" [] admin-routes)

    ;misc
    (GET "/on_dev" [] (str (middleware/development?)))
    (GET "/template" [] (process/list-template))
    (GET "/" [] (process/list-page) )
    (route/resources "/")
    (route/not-found "<h3>Page not found</h3>")
)


(def app
    (if (middleware/development?)
        ;production blocks error stacktraces from printing, but logs 'em

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
  (let [port (System/getenv "PORT")]
    (if port  (run-jetty app {:port (Integer/parseInt port)})
     (run-jetty app {:port 8000}))
    ))
