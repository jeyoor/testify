(ns testify.process.fileops)

;;differentiate cloud foundry & other paths by env variable
(defn real-dir [relpath] 
  (let [testifydir (. System getenv "TESTIFYDIR") ] 
    (cond (nil? testifydir) 
          (str "/resources/" relpath) 
          :else (str testifydir "/" relpath))))