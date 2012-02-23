(ns testify.process.fileops)

;;differentiate cloud foundry & other paths by env variable
(defn real-dir [relpath] 
  (let [testifyenv (. System getenv "TESTIFYDIR")
        userdir (.. System (getProperties) (get "user.dir"))] 
    (cond (nil? testifyenv)  
          (str userdir "/resources/" relpath) 
          :else (str userdir testifyenv "/" relpath))))

(defn find-template
  "given a template filename, return the full path to the template"
  [tname]
  (real-dir (str "templates/" tname))) 