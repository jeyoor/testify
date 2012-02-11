(ns testify.persist2
    (:use testify.persist)
    (:require [redis.core :as redis])
    (:require [clojure.string :as string])
    (:require [clojure.contrib.str-utils :as str-utils])
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