(ns testify.process.changers
  (:require
   [clojure.contrib.string :as string]))

(defn split-names
  "for every"
  [list]
  (loop [number 0 savelist nil worklist list]
    (if (empty? worklist)
      (partition 3 (reverse savelist))
      (if (even? number)         
         (recur (+ number 1) (cons (first worklist) savelist) (rest worklist))
         (recur (+ number 1) (list*  (re-find #"\s.*" (first worklist))(re-find #"\w* " (first worklist)) savelist) (rest worklist)))))) 
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

(defmacro single-transform
  "take one user-script transformation statement and apply it to the given HTML nodes"
  [nodes transform]
  (let [selector (first transform)
        funame (second transform)
        args (nth 3 transform)]
    (str selector funname args)))
(defmacro make-transform
  "apply user transformation script to some html nodes" 
  [nodes script]

  )