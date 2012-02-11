(ns testify.appear
    (:use
        net.cgrand.enlive-html
        testify.util
        testify.html-macros
        )
    (:require
        [clojure.string :as string]
    )
)


(deftemplate base "testify/base.html"
    [pagetitle content]
    ;if a header is passed in, give substi fun
    ;otherwise, give identity fun (leave html unchng)
    [:div#header :span#pagetitle] (maybe-appear pagetitle)
    ;no linkup on basic base
    [:div#header :a#biglink] nil
    [:div#content] (maybe-content content)

)

(deftemplate link-base "testify/base.html"
    [pagetitle linkname linkref content]
    ;if a header is passed in, give substi fun
    ;otherwise, give identity fun (leave html unchng)
    [:div#header :span#pagetitle] (maybe-appear pagetitle)
    [:div#header :a#biglink]
                    (do->
                        (maybe-content linkname)
                        (maybe-href linkref)
                     )
    [:div#content]   (maybe-content content)


)
(defsnippet message "testify/base.html"
    [:span.message]
    [message]
    ;TODO: make this more expressive?
    [:span.message] (maybe-content message)
)
(defsnippet input "testify/user_input.html"
    [:input.field]
    [type name classy value]
    [:input.field]  (do->
                        (maybe-type type)
                        (maybe-class classy)
                        (maybe-name name)
                        (maybe-value value)
                    )
)

(defsnippet ul-link "testify/user_list.html"
    ;grab the whole unordered list
     [:ul.mainlist]
    ;take link name and ref as params
    [linkname linkref]
    ;todo check null links here?
    ;drill down to the first anchor tag
    [:ul.mainlist :li.mainlist :a.firstlink]
        (do->
            (content linkname)
            (set-attr :href linkref)
        )
)

(defsnippet ul-link-delete "testify/admin_list.html"
    ;grab the whole unordered list
     [:ul.mainlist]
    ;take link name and ref as params
    [linkname1 linkref1 postname postval]
    ;todo check null links here?
    ;drill down to the first anchor tag
    [:ul.mainlist :li.mainlist :a.firstlink]
        (do->
            (maybe-content linkname1)
            (maybe-href linkref1)
        )
    [:ul.mainlist :li.mainlist :input.delete]
        (do->
            (maybe-name postname)
            (maybe-value postval)
        )
)

(defsnippet form "testify/user_input.html"
    [:form]
    [method action content]
    [:form] (do->
              (set-attr :action action :method method)
              (maybe-content content)
            )
)

(defsnippet save-form "testify/user_input.html"
    [:form]
    [method action content]
    [:form] (do->
              (set-attr :action action :method method)
              (maybe-content content)
              (append (input "submit" "save" "saver" "Save Page"))
             )
)

;page form has a hidden field for the template name
(defsnippet page-form "testify/user_input.html"
    [:form]
    [method action template content]
    [:form] (do->
              (set-attr :action action :method method )
              (maybe-content content)
              (prepend (input "hidden" "template" "passer" template))
              (append (input "submit" "save" "saver" "Save Page"))
             )
)




(defsnippet input-row "testify/user_input.html"
    ;take the typeclass and the name, plug in into an inputrow
    ;TODO Have typing pick which field (textarea or which input) we pull
    ;TODO Give ajaxified validation based on typeclass to the user
    #{[:span.fieldname] [:textarea.field] [:br.ender]}
    [typeclass name]
    [:span.fieldname] (maybe-content (str name ":" typeclass))
    [:textarea.field] (do->
                       (maybe-class typeclass)
                       (maybe-name name)
                       )

)
