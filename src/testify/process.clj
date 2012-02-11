(ns testify.process
    (:use
        testify.remain
        testify.appear

    )
    (:require
        [net.cgrand.enlive-html :as html]
        [testify.process.dyn-html :as dhtml]
        [testify.util :as util]
        [clojure.string :as string]
        [clojure.contrib.io :as io]
    )
)

(defn linknodes
    "given a list of ids, return links pointing at targetIDHERE"
    [ids target]
    (map #(ul-link % (str target %) ) ids)
)

(defn dbl-linknodes
    [ids target1 operation target2]
    (map #(ul-link-delete % (str target1 %) operation (str target2 %)  ) ids)
)

(defn list-page
    "retrive a list of pages from the data store and return html link list"
    []
                   ;get ids from full keynames
    (let [pagelist (map id (dump "page" "token"))]
                        ;use the ids to populate a linklist
            (link-base nil  "Add Page?" "/template" (linknodes pagelist "/page/view?pname="))
    )

)

(defn list-template
    "retrive a list of templates from the data store and return html link list"
    []
                  ;get ids from full keynames
    (let [tmpltlist (map id (dump "template" "html"))]
                        ;use the ids to populate a linklist
            (base "Pick a template" (linknodes tmpltlist "/page/form?tname="))

    )
)

(defn admin-template
    "retrive a list of templates from the data store and return html link list"
    []
                  ;get ids from full keynames
    (let [tmpltlist (map id (dump "template" "html"))]
                        ;use the ids to populate a linklist

        (link-base
            "Manage templates" "Add a Template" "/admin/template/form"
            (form "POST" "/admin/template/delete"
                (dbl-linknodes tmpltlist "/template/preview?tname=" "delete" "Delete ")
            )
        )
    )
)

(defn admin-resource
    "retrive a list of resources from the data store and return html link list with admin properties"
    []
                  ;get ids from full keynames
    (let [reslist (map id (dump "resource" "file"))]
            ;use the ids to populate a linklist
        (link-base
            "Manage resources" "Add a Resource" "/admin/resource/form"
            (form "POST" "/admin/resource/delete"
                (dbl-linknodes reslist "/resource/" "delete" "Delete ")
            )
        )
    )
)

(defn form-page [template]
    ;todo: unroll with let?
    (if (nil? template)
        (link-base "" "Please choose a template from the list." "/template/" "ERROR! No template specified")
        (base "Enter text, an image URL, or a GodTube video URL"
            (page-form "POST" "/page/save" template
                (map
                    #(apply input-row %)
                    (cons
                        ;add an input for filename
                        (list "text" "PageName" )
                        (dhtml/get-pairs (fetch (str "template:" template ":html")))
                    )
                )
            )
        )
    )
)

;TODO: validate user's responses here
;TODO: check for DB errors here?
;TODO: transactional dbstuff?
(defn save-page [pname template savedata]
    (let [  ;NOTE: must change :keyword to ":keyword"
            keyz  (keys savedata)
            valuez (util/scrub (vals savedata))
            token (util/gen-token pname)
         ]
        ;add new page to mommaset
        (if (sstore "page" pname)
            (do
                ;New page! store  page's deletion tokes
                (store (str "page:" pname ":token") token)
                ;store the page's template
                (store (str "page:" pname ":template") template)
                ;store the subsmap
                (apply hstore (str "page:" pname ":subs") (interleave keyz valuez))
                ;print the tokes
                (link-base
                    "Success! Page saved."
                    "Back to main"
                    "/"
                    (message (str "Your deletion token is "
                         token ". Please save your token somewhere safe."
                         "You will need if you ever want to remove your page from Testify." ) )
                )
            )
            ;page exists, say error
            (link-base
                "Error! Page exists with that name."
                "Back to add page"
                "javascript:javascript:history.go(-1)"

                (message (str "A page with that name already exists. Please try again." ) )
            )

        )
    )
)

(defn view-page [pname]
    (let [ prefix (str "page:" pname) subsm (hfetch (str prefix ":subs")) tname (fetch (str prefix ":template"))]
        (let [thtml (fetch (str "template:" tname ":html")) tnodes (html/html-snippet thtml)]
            ;TODO finishthis
            (string/join (dhtml/subsw tnodes subsm))
        )
    )
)

(defn admin-menu []
    (link-base
        "Administration"
        "Back to User's View"
        "/"
        (list
            (ul-link "Manage pages" "/admin/page")
            (ul-link "Manage templates" "/admin/template")
            (ul-link "Manage resources" "/admin/resource")
        )
    )
)

(defn admin-page []
                      ;get ids from full keynames
    (let [paglist (map id (dump "page" "token"))]
            ;use the ids to populate a linklist
        (link-base
            "Manage pages" "Add a Page" "/template"
            (form "POST" "/admin/page/delete"
                (dbl-linknodes paglist "/page/view?pname=" "delete" "Delete ")
            )
        )
    )
)

(defn delete-page
    "if both nil, diplay form, otherwise take args and delete the page"
    ([])
    ;TODO codethis
    ([pname token])
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

(defn form-resource []
    (link-base "Enter a name and pick a file to upload." "Back to admin" "/admin/resource"
        (save-form "POST" "/admin/template/save"
            (conj
                (input-row (list "text" "TemplateName" ))
                (input "file" "file-input" "FileName" "coolfile.jpg")
            )
        )
    )
)
(defn save-resource [rname rfile]
    (if (or (nil? rfile) (nil? rname))
        (link-base "" "Back to Resource Add" "/admin/resource/form" "Error! File or name missing")
        (store (str "resource:" rname ":file") (io/to-byte-array rfile))
    )
)



(defn delete-resource [rname]
    (if (nil? rname)
        (link-base "Delete a resource" "Back to admin" "/admin/" )
    )
)

(defn admin-delete-page []
;TODO codethis
)







