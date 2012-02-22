(ns testify.views.page
    (:use
        testify.remain
        testify.appear
        testify.process
        testify.process.transform
    )
    (:require
      [net.cgrand.enlive-html :as html]
      [testify.process.dyn-html :as dhtml]
      [testify.util :as util]
      [clojure.string :as string]
      [clojure.contrib.io :as io]
    ))

(defn list-page
    "retrive a list of pages from the data store and return html link list"
    []
                   ;get ids from full keynames
    (let [pagelist (map id (dump "page" "token"))]
      ;;use the ids to populate a linklist
      ;;TODO: switch this to use template context
      ;;(link-base nil  "Add Page?" "/template" (linknodes pagelist "/page/view?pname="))
      (bulk-transform "../templates/base.html" "../templates/main.tr")))


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

(defn delete-page
    "if both nil, diplay form, otherwise take args and delete the page"
    ([])
    ;TODO codethis
    ([pname token])
)
