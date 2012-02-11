(ns testify.util
    (:use [clojure.contrib.io :only [to-byte-array]])
    (:require [clojure.contrib.str-utils :as str-utils])
)

(def md (. java.security.MessageDigest getInstance "SHA"))

(defn gen-token [PageName]
    (. md update (to-byte-array (str PageName (rand 50))))
    (apply str (. md digest ))
)

(defn scrub [strlist]
    "remove any htmlish chars <,>,&,\\,and %"
    ;LOL \\\\ -> \\ -> backslash
    (map #(str-utils/re-gsub (re-pattern "[%<>\\&\\[\\]q\\\\]") "" %) strlist)
)

(defn strip-colon
    "remove a leading colon from a string, or nil"
    [#^java.lang.String strn]
    (if (. strn startsWith ":")
        (. strn substring 1 (. strn length))
        (throw (Exception. "No leading colon on string!"))
    )
)
