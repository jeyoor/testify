(ns testify.default
    (:require
        [testify.persist :as p]
    )
)

(defn create-testdata []
    (p/create-resource "test.css" "text/css" "div { bacground: #DDDDDD }")
    (p/create-resource "test.html" "text/html" "Hello, world!")
    (println "created resources")

    (p/create-page "jeyan_test" {":name" "bob", ":age" "32"} "basic" "DELETESECRET")

    (p/create-page "jeyan_video" {":name" "bob", ":age" "32"}  "chalk"  "OTHERDELETESECRET")

    (println "created pages")

    (p/create-template "basic" "<p id=name class=text>Namehere </p>")
    (p/create-template "chalk" "<p id=name class=fancy class=text> Fancy name here </p>")

    (println "created templates")

)


(defn list-everything []
    (list
        (p/dump "p")
        (p/dump "t")
        (p/dump "r")
    )
)
