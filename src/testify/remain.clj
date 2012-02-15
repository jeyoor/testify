(ns testify.remain
  (:require [redis.core :as redis])  
  (:require [clojure.string :as string]) 
  (:require [clojure.contrib.str-utils :as str-utils]) 
  (:require [redis.core :as redis]))

(defn redis-creds [] 
  (if-let [servc (. System getenv "VCAP_SERVICES")]                        
    (get (first (get (load-string (. servc replaceAll ":" " "))    "redis-2.2")) "credentials")
    {"hostname" "127.0.0.1", "port" 6379, "password" "test"}))

(defmacro do-redis [& stmts] 
  `(let [cred# (redis-creds)] 
     (redis/with-server {:host (get cred# "hostname") 
                         :port (get cred# "port") 
                         :password (get cred# "password") 
                         :db 0} 
       (let [final#
             (do 
               ~@stmts)
             ]
                                        ;TODO with-server reset connection
                                        ;(redis/quit)
         final#))))

(defn fetch [key] 
  (do-redis (redis/get key)))

(defn store [key val] 
  (do-redis (redis/set key val)))

(defn sstore [sname val] 
  (do-redis (redis/sadd sname val)))

(defn sdelete [sname val] 
  (do-redis (redis/srem sname val)))

(defn hstore [hname & valpairs] 
  (do-redis (apply redis/hmset hname valpairs)))

(defn hfetch [hname] 
  (do-redis (redis/hgetall hname)))

(defn delete [key] 
  (do-redis (redis/del key)))

;three accsessors, they split a key with form  "cat:name:field"
(defn cat [key] 
  (re-find #"^[^:]*" key))

(defn id [key]
  ;regex away the    cat       OR field names
  (str-utils/re-gsub #"(^[^:]*:)|(:[^:]*$)" "" key))

(defn field [key] 
  (re-find #":[^:]*$" key))


(defn dump
  "get all values matching this key prefix  with (optional suffix)"
  ([prefix] 
     (do-redis    
      (redis/keys (str                   
                   prefix                   
                   "*"))))  
  ([prefix suffix] 
     (do-redis (redis/keys (str prefix "*" suffix)))))

(defn gist [prefix]
"produce an interleaved lazy seq of all key-value pairs matching a pattern"
         ;split keys string into a list
  (let [keys (string/split (dump prefix) (re-pattern " ") )]
    ;interleave keys and vals [use fetch on keylist]
    (interleave keys (map fetch keys))))