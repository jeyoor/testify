(ns testify.default
    (:require 
        [testify.persist :as p]
    )
)

(comment
(defn create-testdata []
    (p/create-resource "test.css" "text/css" "div { bacground: #DDDDDD }")
    (p/create-resource "test.html" "text/html" "Hello, world!")
    
    (p/create-page "jeyan_test" "basic" {":name" "bob", ":age" "32"} DELETESECRET")
    (p/create-page "jeyan_video" "chalk" {":name" "bob", ":age" "32"}  "OTHERDELETESECRET")
    
    (p/create-template "basic" "<p id=name class=text>Namehere </p>")
    (p/create-template "chalk" "<p id=name class=fancy class=text> Fancy name here </p>")
    
)
)

(comment
(defn list-everything []
    (list
        (p/dump "p")
        (p/dump "t")
        (p/dump "r")
    )
)
)