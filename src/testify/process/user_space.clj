(ns testify.process.user-space)

(defn find-funs
  "return a map of public namespace mappings" 
  [ns] nil )

(defn assoc-here
  "given one mapping, define a copycat function within this namespace"
  [mapping]  nil) 

(defmacro make-fun
  [name value]
  `(def ~name ~value))

(make-fun hcontent #'net.cgrand.enlive-html/content)
(make-fun happend #'net.cgrand.enlive-html/append)
(make-fun hsnippet #'net.cgrand.enlive-html/html-snippet) 
(make-fun rfetch #'testify.remain/fetch)
