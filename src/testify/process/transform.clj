(ns testify.process.transform
  (:require
   [clojure.set :as set]
   [clojure.contrib.string :as string]
   [net.cgrand.enlive-html :as html]
   [testify.process.fileops :as fileops]))

;;;;Parsing functions

(defn split-names
  "for every"
  [list]
  (loop [number 0 savelist nil worklist list]
    (if (empty? worklist)
      (partition 3 (reverse savelist))
      (if (even? number)         
         (recur (+ number 1) (cons (first worklist) savelist) (rest worklist))
         (recur (+ number 1) (list*  (re-find #"(?s)\s+.*" (first worklist))(re-find #"\w* " (first worklist)) savelist) (rest worklist)))))) 

;;TODO: more idiomatic here?
(defn accum-alternates
  "iterate over a list, saving second, fourth, etc items"
  [list]
  (loop [number 0 savelist nil worklist list]
    (if (empty? worklist)
      (reverse savelist)
      (if (even? number)         
         (recur (+ number 1) savelist (rest worklist))
         (recur (+ number 1) (cons (first worklist) savelist) (rest worklist))))))
    
(defn lexify-transforms
  "take a string of user transforms, return a list of args to the at macro" 
  [str]
  ;;ignore the odd entries
  ;;split on [,],(, and ) 
  (split-names (accum-alternates (string/split #"[\[|\(|\]|\)]" str))))

;;;;Transformation primitives

;;for learning/posterity (not used in rest of file)
(defmacro str-to-sym
  "call the function with the name of the given string"
  [name args]
  `(apply (load-string ~name) (list ~args)))

(defn need-nodes?
  "Check enlive fun name for needed args-to-nodes conversion."
  [name]
  ;;funs that require conversion from text args to HTML nodes
  (let [conv-funs #{"append" "prepend" "before" "after" "substitute" "content"}]
    (set/subset? #{(string/trim name)} conv-funs)))

(defn process-selector
  "Process a string to a proper enlive selector"
  [str]
  ;;Make every "word" a keyword
  ;;TODO: account for advanced tricks like text-node
  (map keyword (string/split #"\s" str)))

(defmacro process-call
  "Take a function name string and an argument string and return the proper enlive helper function" 
  [funstr argstr]
  ;TODO: Code this instead of the hornet's nest below :-)
  `(let [funame# ~funstr
         args# ~argstr]))

(defn process-fname
  "Take a function name string and return the appropriate Clojure symbol"
  [fname]
  (load-string (str "net.cgrand.enlive-html/" (.trim fname))))

(defn single-transform
  "take one user-script transformation statement and apply it to the given HTML nodes"
  [nodes transform]
  (let [selector (process-selector (first transform))
         funame (second transform)
         fusym (process-fname (second transform))
         args (nth transform 2)]
     ;;TODO: check and transform args based on function used
     (cond
      ;;check for content, append, prepend, 
      (need-nodes? funame)
      (html/transform
       nodes (vec selector)
       (fusym (html/html-snippet args)))
      :else
      (html/transform
       nodes (vec selector)
       (fusym args)))))

;;;;Transformation operations

(defn make-transform
  "apply user transformation script to some html nodes" 
  [nodes #^java.lang.String script]
  (let [transes (lexify-transforms script)]
    (reduce single-transform nodes transes)))

(defn bulk-transform
  "apply a user transformation from the specified transform file to the the specified HTML file"
  [#^java.lang.String html #^java.lang.String transform ]
  (apply str (html/emit* (make-transform (html/html-snippet (slurp (fileops/find-template html))) (slurp (fileops/find-template transform))))))

;;TODO: code generic bulk and specific file or redis string retrieval


;;;;Testing/debugging

(defn test-transform
  [#^java.lang.String html #^java.lang.String transform ]
   (make-transform (html/html-snippet (slurp html)) (slurp transform)))

(defn test-lex
  "test the lexical analysis"
  [#^java.lang.String transform]
  (lexify-transforms (slurp transform)))
