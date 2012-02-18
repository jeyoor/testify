(ns testify.routes
    (:use
        compojure.core
    )
    (:require
        [compojure.route :as route]
        [compojure.handler :as handler]
        [testify.middleware :as middleware]
        [testify.persist :as persist]
        [testify.debug :as debug]

      [testify.views.page :as page]
      [testify.views.admin :as admin]
      [testify.views.template :as template]
      [testify.views.resource :as resource]
    )
)



(defroutes page-routes
    ;Redirect to something useful
    ;(GET "/" [] )
    (GET "/form" [tname] (page/form-page tname))
    (GET "/view" [pname] (page/view-page pname))
    (POST "/save" [save PageName template & savedata] (page/save-page PageName template savedata))
    (POST "/delete" [pname token] (page/delete-page))
)

(defroutes admin-routes

    (GET "/" [] (admin/admin-menu))
    (GET "/templates/preview" [tname] (template/preview-template tname))
    (GET "/template/form" [] (template/form-template))
            ;use the "save" param but don't pass it [drop btn name before saving everything]
    (POST "/template/save" [save TemplateName thtml] (template/save-template TemplateName thtml))
    (POST "/template/delete" [tname] (template/delete-template tname))
    (GET "/resource/form" [] (resource/form-resource))
    (POST "/resource/save" [save ResourceName rfile] (resource/save-resource ResourceName rfile))
    (POST "/resource/delete" [rname] (resource/delete-resource rname))
    (GET "/template" [] (admin/admin-template))
    (GET "/resource" [] (admin/admin-resource))
    (GET "/page" [] (admin/admin-page))
    ;pages
    (POST "/page/delete" [] (admin/admin-delete-page))
)

(defroutes main-routes

    (context "/page" [] page-routes)
    (context "/admin" [] admin-routes)

    ;misc
    (GET "/on_dev" [] (str (middleware/development?)))
    (GET "/template" [] (template/list-template))
    (GET "/" [] (page/list-page) )
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
        )))
