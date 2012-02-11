;much of this courtesy Mark McGrana's code
;http://mmcgrana.github.com/2010/07/develop-deploy-clojure-web-applications.html

(ns testify.middleware
    (:use compojure.core)
    (:require [clj-stacktrace.repl :as strp])
)

(defn- erlog [msg & vals]
    (let [line (apply format msg vals)]
        (locking System/err (println line))
    )
)

(defn- applog [msg & vals]
    (let [line (apply format msg vals)]
        (locking System/out (println line))
    )
)

(defn development? []
    (nil? (. System getenv "VMC_APP_NAME"))
)

(defn wrap-errorlog [handler]
    (fn [req]
        (try
            (handler req)
            (catch Exception e
                (erlog "Message:%s\n" (. e getMessage))
                (erlog "Cause:%s\n" (. e getCause))
                (erlog "Exception:%s\n" (strp/pst-str e))

                (throw e)
            )
        )
    )
)

;define a custom error page middleware
(defn wrap-failsafe [handler]
    (fn [req]
        (try
            (handler req)
            (catch Exception e
                (let [stacky (. e getStackTrace)
                      resp  {
                            :status 500 :headers {"Content-Type" "text/html"}
                            :body "<h3>Testify has encountered a server error!</h3>"
                            }
                      stkfrm (nth stacky 0)
                      clsname (. stkfrm getClassName)
                      mthdname (. stkfrm getMethodName)
                     ]

                    ;this indicates a 404 	compojure.core$routing$fn__353.invoke(core.clj:98)
                    (if (. clsname startsWith "compojure.core$routing")
                        (assoc resp :status 404 :body "<h3>Page not found</h3>")
                    )
                )
            )
        )
    )
)
