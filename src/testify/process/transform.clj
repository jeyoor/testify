(ns testify.process.transform
  (:require
   [clojure.set :as set]
   [clojure.pprint]
   [clojure.contrib.string :as string]
   [clojure.contrib.str-utils :as str-utils]
   [net.cgrand.enlive-html :as html]
   [testify.remain :as remain]
   [testify.process.fileops :as fileops]))

;;;;Lexing functions

(defn escape-quotes
  "Replace literal quotes with backslash-quote"
  [str]
  (str-utils/re-gsub #"\"" "\134\134\"" str))

(defn replace-backquotes
  "change backquotes to double-quotes"
  [str]
  (str-utils/re-gsub #"`" "\"" str))

(defn pre-read
  "apply transformations needed before passing the strings to the reader"
  [str]
  (replace-backquotes (escape-quotes str)))

;;TODO: more idiomatic here?
(defn accum-alternates
  "iterate over a list, saving evens and reading odds"
  [list]
  (loop [even 0 savelist nil worklist list]
    (if (empty? worklist)
      (reverse savelist)
      (if (zero? even )
        (recur (bit-xor even 7) (cons (first worklist) savelist) (rest worklist))
        (recur (bit-xor even 7) (cons (read-string (pre-read (first worklist))) savelist) (rest worklist))))))

(defn lexify-transforms
  "take a string of user transforms, return a list of args to the at macro"
  [str]
  ;;use rest because the first string is always empty [opening tilde]
  (partition 2 (accum-alternates (rest (string/split #"~" str)))))

;;;;Operator designations
(def transforms #{'append 'prepend 'before 'after 'substitute 'content})
(def remains #{'fetch 'store 'hfetch 'hstore})
(def alloweds (set/union transforms remains))

;;;;Transformation primitives

;;for learning/posterity (not used in rest of file)
(defmacro str-to-sym
  "call the function with the name of the given string"
  [name args]
  `(apply (load-string ~name) (list ~args)))

(defn allowed-sym?
  "Check if a symbol is allowed in the script"
  [name]
  ;;funs that require conversion from text args to HTML nodes
    (set/subset? #{name} alloweds))

(defn need-snippet?
  "Check if item is a string contains html and thus needs to be converted to nodes"
  [item]
  ;;funs that require conversion from text args to HTML nodes
  (and (string? item) (and (.contains item "<") (.contains item ">"))))


(defn find-space
  "Given a symbol, return a string of the namespace from whence it came"
  [sym]
  (cond
   (set/subset? #{sym} transforms) "net.cgrand.enlive-html/"
   (set/subset? #{sym} remains) "testify.remain/"
   :else "testify.process.user-space"))

(defn process-selector
  "Process a string to a proper enlive selector"
  [str]
  ;;Make every "word" a keyword
  ;;TODO: account for advanced tricks like text-node
  (map keyword (string/split #"\s" str)))

(defn prep-item
  "Prepare one code item"
  [item]
  ;;Switch on type
  (cond
   (symbol? item) (if (allowed-sym? item)
                    ;;Prefix all symbols with correct namespace
                    (symbol (str (find-space item) item))
                    ;;Not allowed symbol, replace with identity
                    (symbol "identity"))
   ;;Add html-snippet conversion if text contains html
   (string? item) (if (need-snippet? item)
                      (list (symbol "net.cgrand.enlive-html/html-snippet") item)
                      item)
   :else item)) 

(defn prep-code
  "Take a sequence of clojure code and prep it for evaluation"
  [seq]
  (map prep-item seq))

(defn single-transform
  "take one user-script transformation statement and apply it to the given HTML nodes"
  [nodes transform]
  (let [selector (process-selector (first transform))
        code (prep-code (second transform))]
    ;;DEBUG
    ;;(println (apply str code))
    ;;(println code)
    ;;(clojure.pprint/pprint code)
    (html/transform
     nodes (vec selector)
     ;;For good examples on using eval, see
     ;;http://clojuredocs.org/clojure_core/clojure.core/eval
     (eval code))))
  

;;;;Transformation operations

(defn make-transform
  "apply user transformation script to some html nodes"
  [nodes #^java.lang.String script]
  (let [transes (lexify-transforms script)]
    ;;TODO add try/catch with "error line #" messages here
    (reduce single-transform nodes transes)))

(defn bulk-transform
  "apply a user transformation from the specified transform file to the the specified HTML file"
  [#^java.lang.String html #^java.lang.String transform ]
  (apply str (html/emit* (make-transform (html/html-snippet (slurp (fileops/find-template html))) (slurp (fileops/find-template transform))))))

;;TODO: code generic bulk and specific file or redis string retrieval


;;;;Testing/debugging

(defn test-lex
  "test the lexical analysis"
  [#^java.lang.String transform]
  (lexify-transforms (slurp (fileops/find-template transform))))

(defn other-test
  "test loading a whole file" [])