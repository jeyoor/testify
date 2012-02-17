(ns testify.process.transform
  (:require
   [clojure.contrib.string :as string]
   [net.cgrand.enlive-html :as html]))

(defn split-names
  "for every"
  [list]
  (loop [number 0 savelist nil worklist list]
    (if (empty? worklist)
      (partition 3 (reverse savelist))
      (if (even? number)         
         (recur (+ number 1) (cons (first worklist) savelist) (rest worklist))
         (recur (+ number 1) (list*  (re-find #"(?s)\s+.*" (first worklist))(re-find #"\w* " (first worklist)) savelist) (rest worklist)))))) 

;TODO: more idiomatic here?
(defn accum-alternates
  "iterate over a list, saving second, fourth, etc items"
  [list]
  (loop [number 0 savelist nil worklist list]
    (if (empty? worklist)
      (reverse savelist)
      (if (even? number)         
         (recur (+ number 1) savelist (rest worklist))
         (recur (+ number 1) (cons (first worklist) savelist) (rest worklist))))))
    
(defn parse-transforms
  "take a string of user transforms, return a list of args to the at macro" 
  [str]
  ;ignore the odd entries
  ;split on [,],(, and ) 
  (split-names (accum-alternates (string/split #"[\[|\(|\]|\)]" str))))

;for learning/posterity 
(defmacro str-to-sym
  "call the function with the name of the given string"
  [name args]
  `(apply (load-string ~name) (list ~args)))

(defmacro single-transform
  "take one user-script transformation statement and apply it to the given HTML nodes"
  [nodes transform]
  `(let [selector# (first ~transform)
         funame# (second ~transform) 
         args# (nth ~transform 2)]
     ;TODO: check and transform args based on function used
     (cond
      (or (.contains funame# "content") (.contains funame# "append")) (html/transform ~nodes [(keyword selector#)] ((load-string (str "html/" funame#))(html/html-snippet args#)))
      :else (html/transform ~nodes [(keyword selector#)] ((load-string (str "html/" funame#)) args#)))))

(defn make-transform
  "apply user transformation script to some html nodes" 
  [nodes #^java.lang.String script]
  (loop [transes (parse-transforms script) nde nodes] 
    (if (empty? transes) nde
        (recur (rest transes) (single-transform nodes (first transes))))))
    ;(println  (second trans)))))

(defn file-transform
  "apply a user transformation from the specified transform file to the the specified HTML file"
  [#^java.lang.String html #^java.lang.String transform ]
  (spit "/tmp/test.html"  (apply str (html/emit* (make-transform (html/html-snippet (slurp html)) (slurp transform))))))

(defn test-transform
  [#^java.lang.String html #^java.lang.String transform ]
  (apply str  (html/emit*  (make-transform (html/html-snippet (slurp html)) (slurp transform)))))
