(defmacro grab-id [name nodes] 
  `(html/at nodes ~(str "[:#]" (html/substitute (html/html-content  "<h1>HELLO</h1>")))))
(defmacro grab-id [name] 
  `(html/at blah ~(str "[:#" name "]") 
            (html/substitute (html/html-content  "<h1>HELLO</h1>"))))




