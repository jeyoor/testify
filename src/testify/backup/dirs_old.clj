(ns testify.dirs
    (:require [clojure.string :as string])
;at compile-time, create emptydir exception class
    (
        :gen-class
            :name testify.dirs.EmptyDirectoryException 
            :extends Exception
    
    )
)


;TODO: define macros; for getting file arrays

;return the File[] from accessing dirname off Testify root
(defn dir-listing [dirname]
    (let
        [dir-list
            (. 
                (new java.io.File 
                  (str 
                    (.. System (getProperties) (get "user.dir")) dirname 
                   ) 
                ) 
                listFiles
            )
        ]
        (cond
            (nil? dir-list) (throw 
                                (new testify.dirs.EmptyDirectoryException 
                                    "msg: Tried to list contents of an empty directory!"
                                ) 
                            )
            :else dir-list
        )
    )
)


(defn list-pagesw [inList outList]
    (cond
        (= inList ()) (string/join " "  outList)
        :else (list-pagesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
                (cons (str "<a href=/reader?fname=" fname ">" fname "</a> <br /> \n") outList)
            )
        ) 
    )
    
)
(defn list-pages []
 
    (list-pagesw  
	            ;list pages directory TODO MACRO?
			    (dir-listing "/resources/pages" )
                (str "") 				
	)
)     
    

(defn admin-pagesw [inList outList]
    (cond
        (= inList ()) (string/join " "  outList)
        :else (admin-pagesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
                (cons (str "<a href='/reader?fname=" fname "'>" fname "</a> \n"
                    "<a href='/admin/deletepage?fname=" fname "'> Delete </a> <br /> \n"
                ) outList)
            )
        ) 
    )
    
)
(defn admin-pages []
 
    (admin-pagesw  
	            ;list pages directory TODO MACRO?
			    (. 
	                (new java.io.File 
                      (str 
						(.. System (getProperties) (get "user.dir")) "/resources/pages" 
					   ) 
                    ) 
					listFiles
                )
                (str "") 				
	)
)    
 
(defn list-templatesw [inList outList]
    (cond
        (= inList ()) (string/join " "  outList)
        :else (list-templatesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
               ; TODO 8B change this to allow deleting/modifying/adding templates?
                (cons (str "<a href='/reader?fname=" fname "'>" fname "</a> \n"
                    "<a href='/admin/deletepage?fname=" fname "'> Delete </a> <br /> \n"
                ) outList)
            )
        ) 
    )
    
)
(defn list-templates []
 
    (list-templatesw  
	            ;list pages directory TODO MACRO?
			    (. 
	                (new java.io.File 
                      (str 
						(.. System (getProperties) (get "user.dir")) "/resources/templates" 
					   ) 
                    ) 
					listFiles
                )
                (str "") 				
	)
)     