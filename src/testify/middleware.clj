;much of this is inspired by Mark McGrana
;http://mmcgrana.github.com/2010/07/develop-deploy-clojure-web-applications.html
;TODO make more unique?

(ns testify.middleware
    (:use compojure.core)
    (:require [clj-stacktrace.repl :as strp]))


(defn- erlog 
  "Print an error message to stderr"
  [msg & vals] 
  (let [line (apply format msg vals)] 
    (locking System/err (println line))))

                                        
(defn- applog 
  "Print a message to stdout"
  [msg & vals]
  (let [line (apply format msg vals)] 
    (locking System/out (println line))))


(defn development?
  "Return true if we're in dev environment; false otherwise"
  [] 
  (nil? (. System getenv "VMC_APP_NAME")))


(defn wrap-errorlog
  "try-catch with exceptions logged"
  [handler] 
  (fn [req] 
    (try (handler req) 
         (catch Exception 
             e
           (erlog "Message:%s\n" (. e getMessage)) 
           (erlog "Cause:%s\n" (. e getCause)) 
           (erlog "Exception:%s\n" (strp/pst-str e))
           (throw e)))))


(defn wrap-failsafe
  "A custom error page middleware"
  [handler] 
  (fn [req] 
    (try (handler req) 
         (catch Exception 
             e 
           (let [stacky (. e getStackTrace)
                 resp  {:status 500 
                        :headers {"Content-Type" "text/html"}
                        
                        :body
                        
                        "<h3>Testify has encountered a server error!</h3>"                        
                        }
                 stkfrm (nth stacky 0) clsname (. stkfrm getClassName)
                 mthdname (. stkfrm getMethodName)]
             ;this indicates a 404 	compojure.core$routing$fn__353.invoke(core.clj:98)
             (if (. clsname startsWith "compojure.core$routing") 
               (assoc resp 
                 :status 404 
                 :body "<h3>Page not found</h3>")))))))