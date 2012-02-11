(ns testify.fileops
    (:require [clojure.string :as string]) 
)
(defn deletepage [fname]
    (cond 
        (. 
            (new java.io.File 
                (str 
                    (.. System (getProperties) (get "user.dir")) "/resources/pages/" fname
                ) 
                
            ) 
            delete
        )       "File deleted! <a href='/admin'> Back to admin </a>"
        :else "File not deleted"
        
    )
    ;testify.dirs/admin-pages
)