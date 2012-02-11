(ns testify.fileops
    (:require 
        [clojure.string :as string]
        [clojure.java.io :as cio]
        [ring.util.response :as response]
        [testify.dirs :as dirs]
    ) 
)
;START UTILITY
(defmacro frfirst [colls] 
    `(let [colls# ~colls] 
        (first (rest (first colls#) ) ) 
    ) 
)

;START Debug
(defn dump-everything [everything]
    (str everything)
)
(defn dump-file [everything]
    (slurp ((everything :tempcode) :tempfile))
)

;todo: add ajax preview on mouseover
;todo: add linkback?
(defn previewtemplate [fname]
    (response/redirect (str "/previews/" fname ".jpg") )
)
;START Delete operations
(defn deletepage [fname]
    (cond 
        (. 
            (new java.io.File 
                (str 
                    (.. System (getProperties) (get "user.dir")) 
                    (dirs/real-dir "pages/") 
                    fname
                ) 
                
            ) 
            delete
        )       "Page deleted! <a href='/admin'> Back to admin </a>"
        :else 
            (str 
                "Error, page not deleted. <a href='/admin'> Back to admin </a>"
                (.. System (getProperties) (get "user.dir")) (dirs/real-dir "pages/") fname
             )
        
    )
    ;testify.dirs/admin-pages
)
(defn deletetemplate [fname]
    (cond 
        (. 
            (new java.io.File 
                (str 
                    (.. System (getProperties) (get "user.dir")) (dirs/real-dir "templates/") fname
                ) 
                
            ) 
            delete
        )       "Template deleted! <a href='/admin'> Back to admin </a>"
        :else "Error, template not deleted. <a href='/admin'> Back to admin </a>"
        
    )
)
(defn deleteresource [fname]
    (cond 
        (. 
            (new java.io.File 
                (str 
                    (.. System (getProperties) (get "user.dir")) (dirs/real-dir "public/") fname
                ) 
                
            ) 
            delete
        )       "Resource deleted! <a href='/admin'> Back to admin </a>"
        :else "Error, resource not deleted. <a href='/admin'> Back to admin </a>"
        
    )
)

;START Edit/create template section
(defn dump-template [fname template]
    ;output the template code into fname
    (spit 
        ;spit file to templates folder
        (str (.. System (getProperties) (get "user.dir")) (dirs/real-dir "templates/") fname) 
        ;crack open the http request and get template file (slurp ok for ascii)
        (slurp (template :tempfile) )
    )
    (str
        ;debug
        ;template
        "New template added! <a href='/admin'> Back to admin </a>"
    )
)

(defn templatize  []
    (str
    
        "<form action='write_template' enctype='multipart/form-data' method='POST'>
        Filename <br />
        <input type='text' name='fname' /> <br />
        Template file <br />
        <input type='file' name='template' /> <br />
        <input type='submit' name='submitter' />
        </form>"
    )
)
; START Edit/create resources section
(defn dump-resource [fname resource]
    ;output the resource code into fname
    
    ;copy works binary OR ascii
    (cio/copy 
        ;crack open the http request and get resource filename
        (new java.io.File (str (resource :tempfile) ) )
        ;spit file to resources folder
        (new java.io.File (str (.. System (getProperties) (get "user.dir")) (dirs/real-dir "public/") fname)  )
    )
    
    
    (str
        ;debug  

        "New resource added! <a href='/admin'> Back to admin </a>"
    
    )
)

(defn resourcize  []
    (str
    
        "<form action='write_resource' enctype='multipart/form-data' method='POST'>
        Filename <br />
        <input type='text' name='fname' /> <br />
        Resource file <br />
        <input type='file' name='resource' /> <br />
        <input type='submit' name='submitter' />
        </form>"
    )
)

;START Edit/create page section
;Ok, 9/5/2011
; putting & here encased the rest of the arguments as a LIST
; this threw the serialization attempt off, big time. No &, its OK! PTL! GOD!
(defn dump-map [fname  inputmap]
    (binding [*print-dup* true]
        ( spit (str (.. System (getProperties) (get "user.dir")) (dirs/real-dir "pages/") fname) inputmap) 
        ;DEBUG
        ;(println inputmap)
        (str 
            "<h1>Saved response to map " fname  " </h1> <br/> \n"
            "<a href='/'>return to homepage</a> <br/> \n"
        )
        
    )
)


;regex template file, get sub words
(defn findsubs [template]
    ;TODO: file error handling here
    (let [raw-templ 
        (slurp 
            (str (.. System (getProperties) (get "user.dir")) (dirs/real-dir "/templates/") template) 
        )
        ]
        ;remove duplicates TODO: be smarter and check for template errors here?
        (distinct (re-seq (re-pattern ":\\w*") raw-templ) )
    )
)

;for editing, rip strings from file
(defn populatesubs [subs fname]
 (println "TODO! CODETHIS!")
 "TODO! CODETHIS!"
)

(defn widgetsubsw [subs text template]
    (cond
        ;todo: valid HTML heading here? Call global HTML header function?
        ; move this html to a view section?
        (= subs ())
            (str 
                "<form action='/writer' method='POST'> \n" 
                    "<input type='hidden' name='template' value='" template "' /> <br/> \n"
                    "File Name </br> \n" 
                    "<input type='text' name='fname' size='20'/> <br/> \n"
                        (string/join text) 
                    "<input type='submit' />"
                "</form>"                 
            )
        :else
            ;strip initial colon
            (let [field (. (first subs) substring  1 (. (first subs) length) ) ]
                (widgetsubsw 
                    (rest subs)
                    (cons
                        (str
                           field "</br> \n"
                           "<textarea rows='5' cols='23' name='" field "'></textarea> <br/> \n"
                        )
                        text
                    )
                    template
                )
            )
    )

)

;create html entry form pointing to writer from subs
(defn widgetsubs [subs template]
    (widgetsubsw subs (str "") template )
)

;given strings and subs, make html entry form
(defn widgetpops [subs text]
 (println "TODO! CODETHIS!")
 "TODO! CODETHIS!"
)

(defn editize [template fname]
    (cond
        ;adding a new page
        (nil? fname)
            (widgetsubs (findsubs template) template)
        ;editing an existing page
        :else   
            "TODO CODETHIS"
    )
)


(defn subsw [submap target]
    (cond
        ;if the list is all eaten up, return the result!
        (= submap ()) target
        ;TODO: check target viability here?       
        :else 
            (subsw (rest submap) 
                (. target replaceAll 
                    (str (ffirst submap) ) (str (frfirst submap) ) 
                )
            )
    )
)

; START Perform substitutions
; given a key-value mapset, substitute everything in the given string
(defn simpl-subs [fname]
    (cond
        ;todo: add exception class?
        (nil? fname) (throw (Exception. (str "no page file name given! (Check fname get param)") ) )
        :else (let [submap 
                
                ( load-file (str  (.. System (getProperties) (get "user.dir")) (dirs/real-dir "/pages/") fname))              
             ]
                      
            ;DEBUG
            ;(str submap)
            ;(get submap :template)
            
            (cond 
                (nil? (get submap :template)) (throw (Exception. (str "template file name in page file!") ) )
                :else (let [raw-templ 
                        (slurp 
                            (str (.. System (getProperties) (get "user.dir")) (dirs/real-dir "/templates/") (get submap :template)) 
                        )
                     ]
                     ;DEBUG
                     ;(str raw-templ)             
                    (subsw submap raw-templ)
                )
            
            )
            
        ) 
    )
)
