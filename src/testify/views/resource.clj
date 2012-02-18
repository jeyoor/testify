(ns testify.views.resource
    (:use
        testify.remain
        testify.appear
        testify.process
    )
    (:require
        [testify.process.dyn-html :as dhtml]
        [testify.util :as util]
        [clojure.string :as string]
        [clojure.contrib.io :as io]
    ))

(defn form-resource []
    (link-base "Enter a name and pick a file to upload." "Back to admin" "/admin/resource"
        (save-form "POST" "/admin/template/save"
            (conj
                (input-row (list "text" "TemplateName" ))
                (input "file" "file-input" "FileName" "coolfile.jpg")
            ))))

(defn save-resource [rname rfile]
    (if (or (nil? rfile) (nil? rname))
        (link-base "" "Back to Resource Add" "/admin/resource/form" "Error! File or name missing")
        (store (str "resource:" rname ":file") (io/to-byte-array rfile))
    ))

(defn delete-resource [rname]
    (if (nil? rname)
        (link-base "Delete a resource" "Back to admin" "/admin/" )
    ))

