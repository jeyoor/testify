(ns testify.persist
    (:require [redis.core :as redis]
    (:require [clojure.string :as string])
    (:require [clojure.contrib.str-utils :as str-utils])
)

(defn redis-creds []
    (if-let [servc (. System getenv "VCAP_SERVICES")]
        ;if
        (get 
            (first 
                (get 
                    (load-string (. servc replaceAll ":" " "))    "redis-2.2"
                )
            )
            "credentials"
        )            
        ;else
        {"hostname" "127.0.0.1", "port" 6379, "password" "happytown"}
        
    )
)




(defmacro do-redis [& stmts]
    `(let [cred# (redis-creds)]
        (redis/with-server
            {:host (get cred# "hostname") :port (get cred# "port") :db 0}
            (redis/auth (get cred# "password"))            
             ~@stmts
        )
    )
)

(defn fetch [key] 

    
        ;(redis/with-server
          ;  {:host "127.0.0.1" :port 6379 :db 0}
            
            ;{:host (get (redis-creds) "hostname") :port (get (redis-creds) "port") :db 0}
            ;(redis/auth (get (redis-creds) "password"))            
            
         ;   (redis/auth "happytown")            
         ;    (redis/get key) 
        ;)

    
    (do-redis
        (redis/get key) 
    )
    
)
(defn store [key val] 
    (do-redis
        (redis/set key val)                    
    )
)


;get all values matching this key prefix
(defn dump [prefix]
    (do-redis
        (redis/keys (str prefix "*")) 
    )
)
;produce an interleaved lazy seq of all key-value pairs matching a pattern
(defn gist [prefix]
        ;split keys string into a list
        (let [keys (string/split (dump prefix) (re-pattern " ") )]
                ;interleave keys and vals [use fetch on keylist]
                (interleave keys (map fetch keys))
        )    
)

;given a catergory, name, and a field-value map, create a thing
;see design doc for more details on in-redis structure
(defn create-thing [cat name & fieldmap]
    (do-redis
        ;if the name has not already been added
        (if (redis/sadd cat name)
            ;add the fieldpairs
            (do
                (doseq [i fieldmap]
                    (println i)
                    ;There's no colon between name and (first i) cuz 
                    ;Ring automatically prefixes a colon to web request results
                    (redis/set (str cat ":" name (first i)) (first (rest i)))
                )
                (str "Added " name " to " cat)
            )
            ;else give error message
            (str "A " cat " named " name " already exists")
        )
    )
)

(defn- grab-thing [cat name & fields]
    (do-redis
        ;simple illustration of correct get syntax
        ;not meant to be used publicly
        (map #( do-redis (redis/get (str cat ":" name %))) fields)
    )
)

(defn get-thing [cat name & fields]
    (do-redis
        (if (redis/sismember cat name)
                        ;interleave the fields with the finding of each in db
            (interleave fields (map #( do-redis (redis/get (str cat ":" name %))) fields) )            
            (str "The " cat " called " name " was not found .")
        )
    )
)

(defn update-thing [cat name & fieldmap]
    (do-redis
        ;if the name has not already been added
        (if (redis/sadd cat name)
            ;give error message
            (str "A " cat " named " name " does not exist")
            ;else update the fieldpairs
            (do
                (doseq [i fieldmap]
                    ;no second colon needed (see "create" above)
                    (redis/set (str cat ":" name (first i)) (first (rest i)))
                )
                (str "Updated " name " in " cat)
            )
        )
    )
)


(defn delete-thing [cat name & fields]
    (do-redis
        ;if we can remove the name from the uberset
        (if (redis/srem cat name)
            ;else delete the entry in set
            (do
                ;no second colon needed (see "create" above)
                ;TODO: process this list nicely? (deleted all ok, etc.)
                (str 
                    (map #( do-redis (redis/del (str cat ":" name %))) fields)
                    
                     " Deleted " name " from " cat
                )
            
                
            )
            ;else give error message
            (str "A " cat " named " name " does not exist. No operation performed.")
        )
    )
)

;three accsessors, they split a key with form  "cat:name:field"
(defn cat [key]
    (re-find #"^[^:]*" key) 
)

(defn name [key]
    ;regex away the    cat       OR field names
    (str-utils/re-gsub #"(^[^:]*:)|(:[^:]*$)" "" key)
)

(defn field [key]
    (re-find #":[^:]*$" key) 
)

(defn find-things [cat]
    ;only take distinct thingnames
    (distinct 
        (map
            ;regex away the cat names
            #(str-utils/re-gsub #"^[^:]*:" "" %) 
            (map 
                ;regex away the field names
                #(str-utils/re-gsub #":[^:]*$" "" %) 
                ;dump all thingnames in the category
                (dump (str cat ":"))
            ) 
        )
        
    )
)

;take orphan fields and either save or discard them
;also, build parents for orphan fields after filtering
(defn restore-fields [cat & fields]
    (do-redis
    ;if unknown name with invalid field
        (doseq [i (dump (str cat ":"))]
            
            ;if valid field
            (if (some  #{(field i)} fields)
                ;keep the valid field
                ;if known name
                (if (redis/sismember cat (name i) )
                    ;name is known, all is well
                    ()
                    ;add the unknown name
                    (redis/sadd cat (name i))
                )
                ;invalid field, DELETE it
                (redis/del i)
            )            
        )
    )
)

;given a map containing sane defaults, restore category
(defn restore-names [cat & fieldmap]
    (do-redis
        (doseq [curname (redis/smembers cat)]
            (doseq [j fieldmap]
                (let [fieldname (first j) default (first (rest j)) ]
                    ;if field already exists
                    (if (redis/exists (str cat ":" curname fieldname))
                        ;fine
                        ()
                        ;else, create default
                        (redis/set (str cat ":" curname fieldname) default)
                    )
                )
            )        
        )
    )
)
;start resource-specific
(defn resource-fields []
    [":type",":file"]
)

(defn resource-fieldmap [fieldvals]
    (if (= (count fieldvals) (count (resource-fields)))
        (partition 2 (interleave  (resource-fields) fieldvals ))
        ;else, 
        (throw (Exception. "arity of fields and  values do not match"))
    )
)
;fieldvals are :type and :file right now
(defn create-resource [name & fieldvals]
     ;added "apply" to "unbox" the fieldvals from extra list   
    (apply create-thing "resource" name (resource-fieldmap fieldvals))
)
(defn update-resource [name & fieldvals]
    (apply update-thing "resource" name (resource-fieldmap fieldvals))
)
(defn get-resource [name]
    (apply get-thing "resource" name (resource-fields))
)
(defn delete-resource [name]
    (apply delete-thing "resource" name (resource-fields))
)
(defn restore-resource []
    (apply restore-fields "resource" (resource-fields))
    (apply restore-names "resource" (resource-fieldmap "text/plaintext" "restored"))
)

;start page-specific
(defn page-fields []
    
        [":template",":subs",":token"]
)

(defn page-fieldmap [& fieldvals]
    (if (= (count fieldvals) (count (page-fields)))
        (partition 2 (interleave  (page-fields) fieldvals ))
            ;else, 
            (throw (Exception. "arity of fields and values do not match"))
    )
)

(defn create-page [name & fieldvals]
    (apply create-thing "page" name (page-fieldmap fieldvals))
)

(defn update-page [name & fieldvals]
    (apply update-thing "page" name (page-fieldmap fieldvals))
)

(defn get-page [name]
    (apply get-thing "page" name (page-fields))
)

(defn delete-page [name]
    (apply delete-thing "page" name (page-fields))
)

(defn restore-page []
    (apply restore-fields "page" (page-fields))
    (apply restore-names "page" (page-fieldmap "basic" ":name Pocohantas" "AAAA"))
)

;start template-specific
(defn template-fields []
    [":html"]
)

(defn template-fieldmap
    (if (= (count fieldvals) (count (template-fields)))
        (partition 2 (interleave  (template-fields) fieldvals ))        
        (throw (Exception. "arity of fields and values do not match"))
    )
)
(defn restore-template []
    (restore-fields "template" (template-fields))
    (restore-names "template" (template-fieldmap "<p id='name' class='text'>John Smith</p>"))
)

(defn test-redis []
	     (do-redis	       
	         (redis/set "foo" "bar")
	         (println (redis/get "foo"))))


