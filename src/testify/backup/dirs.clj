(ns testify.dirs
    (:require 
        [clojure.string :as string]
    )

)


(gen-class :name testify.dirs.EmptyDirectoryException :extends Exception)
;return main html links
(defn main-menu []
    "<a href='/templates'> Add page </a> <br /> \n
    <a href='/admin'> Administration mode </a> <br /> \n"
)
;return html links to admin functions
(defn admin-menu []
    "<a href='/admin/templates'> View/Add Templates (HTML layouts) </a> <br /> \n
     <a href='/admin/resources'> View/Add Resources (images, videos, css, etc.) </a> <br /> \n
     <a href='/'> Back to homepage <br /> \n
     "
     
)
(defn template-menu []
    "<br /> Administrators can add more templates"
)

(defn admin-template-menu []
    "<a href='/templater'> Add template </a> <br /> \n"
)
(defn resource-menu []
        "<a href='/resourcer'> Add resource </a> <br /> \n"
)

(defn cur-dir [] (.. System (getProperties) (get "user.dir")) )

;differentiate cloud foundry & other paths by env variable
(defn real-dir [relpath]
    (let [testifydir (. System getenv "TESTIFYDIR") ]
        (cond
            (nil? testifydir) (str "/resources/" relpath)
            :else (str testifydir "/" relpath)
        )
    )
)
(defn path-dir [] (real-dir "pages") )
;return the File[] from accessing dirname off Testify root
(defn dir-listing [dirname]
    (let
        [dir-list
            (. 
                (new java.io.File 
                  (str 
                    (.. System (getProperties) (get "user.dir")) (real-dir dirname)
                   ) 
                ) 
                listFiles
            )
        ]
        (cond
            (nil? (first dir-list) ) (throw 
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
        (= inList ()) 
            (str
                (string/join " "  outList)
                (main-menu)
            )
        :else (list-pagesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
                (cons 
                    (str 
                        "<a href='/reader?fname=" fname "'>" fname "</a> <br /> \n"
                        ;No page editing coded yet
                        ;"<a href='/editor?fname=" fname "'> Edit Page </a> <br /> \n"
                    ) 
                    outList
                )
            )
        ) 
    )
    
)
(defn list-pages []
    (try
        (list-pagesw  
            (dir-listing "/pages" )
            (str "") 				
        )
        ;todo have ring middleware here? :-)
        (catch testify.dirs.EmptyDirectoryException e 
            (str
                "<h1>  The Pages directory is empty!Edit</h1>"
                (path-dir)
                (main-menu)
            )  
        )
    )
)     
    

(defn admin-pagesw [inList outList]
    (cond
        (= inList ()) 
            (str 
                (string/join " "  outList)
                (admin-menu)
            )
                
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
    (try
        (admin-pagesw  
                    (dir-listing "/pages" )
                    (str "") 				
        )
        (catch testify.dirs.EmptyDirectoryException e 
                ;print the administrative menu even if there are no pages
                (admin-menu)
        )
    )
)    
 
(defn list-templatesw [inList outList]
    (cond
        (= inList ()) 
            (str
                (string/join " "  outList)
                (template-menu)
            )
        :else (list-templatesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
               ; TODO 8B change this to allow deleting/modifying/adding templates?
                (cons 
                    (str 
                        fname 
                        " <a href='/editor?template=" fname "'>Make page</a>   \n" 
                        " <a href='/admin/previewtemplate?fname=" fname "'>Preview</a>  \n"
                    ) 
                    outList
                )
            )
        ) 
    )
    
)
(defn list-templates []
 
    (list-templatesw  
			    (dir-listing "/templates" )
                (str "") 				
	)
)   

(defn admin-templatesw [inList outList]
    (cond
        (= inList ()) 
            (str
                (string/join " "  outList)
                (admin-template-menu)
            )
            
        :else (admin-templatesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
               ; TODO 8B change this to allow deleting/modifying/adding templates?
                (cons 
                    (str 
                        fname 
                        " <a href='/editor?template=" fname "'>Make page</a>   \n" 
                        " <a href='/admin/previewtemplate?fname=" fname "'>Preview</a>  \n"
                        " <a href='/admin/deletetemplate?fname=" fname "'>Delete</a> <br /> \n"
                    ) 
                    outList
                )
            )
        ) 
    )
    
)
(defn admin-templates []
 
    (admin-templatesw  
			    (dir-listing "/templates" )
                (str "") 				
	)
)

(defn list-resourcesw [inList outList]
    (cond
        (= inList ()) 
            (str
                (string/join " "  outList)
                (resource-menu)
            )
        :else (list-resourcesw (rest inList) 
            (let [fname (.  (first inList) getName) ]
               ; have to double-escape the \ 
               ; TODO 8B change this to allow deleting/modifying/adding templates?
                (cons (str "<a href='/" fname "'>" fname "</a> \n"
                    "<a href='/admin/deleteresource?fname=" fname "'> Delete </a> <br /> \n"
                ) outList)
            )
        ) 
    )
    
)
(defn list-resources []
 
    (list-resourcesw  
			    (dir-listing "/public" )
                (str "") 				
	)
)  
