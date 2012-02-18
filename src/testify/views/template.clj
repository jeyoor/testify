(ns testify.views.template
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

(defn list-template
    "retrive a list of templates from the data store and return html link list"
    []
                  ;get ids from full keynames
    (let [tmpltlist (map id (dump "template" "html"))]
                        ;use the ids to populate a linklist
            (base "Pick a template" (linknodes tmpltlist "/page/form?tname="))

    )
)

(defn form-template []
    (base "Enter a name and html for the template. Use class=a, a E {text,image,video} to mark tags for substitution."
        (save-form "POST" "/admin/template/save"
            (map
                #(apply input-row %)
                (list
                    ;add an input
                    (list "text" "TemplateName" )
                    (list "text" "thtml")
                )
            )
        )
    )
)


;TODO validate for valid HTML
;TODO validate for AT LEAST ONE field
;TODO validate for correct typing?
;TODO warn if already exists
(defn save-template [tname thtml]
    (let [res1 (sstore "template" tname)
          res2 (store (str "template:" tname ":html") thtml)]
    (link-base "Template saved!" "Back to admin" "/admin" (str "New template? " res1 "Stored ok? " res2))
    )
)

;TODO: confirmation page?
(defn delete-template [tname]
    (let [res1 (sdelete "template" tname)
          res2 (delete (str "template:" tname ":html")) ]
    (link-base "Template deleted!" "Back to admin" "/admin" (str "Unlisted ok? " res1 "Deleted ok? " res2))
    )
)
;TODO: codethis with umm.. returning the template's html
(defn preview-template [tname]
    (link-base
        (str "Previewing template " tname)
        "Back to templates"
        "/template"
        (do-redis (fetch (str "template:" tname ":html")))
    )
)

