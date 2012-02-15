(ns testify.html-macros
  (:use net.cgrand.enlive-html))


;courtesy of enlive-tutorial (David Nolan)
(defmacro maybe-substitute
  ([expr] `(if-let [x# ~expr] (substitute x#) identity))
  ([expr & exprs] `(maybe-substitute (or ~expr ~@exprs))))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(defmacro maybe-href
  ([expr] `(if-let [x# ~expr] (set-attr :href x#) identity))
  ([expr & exprs] `(maybe-href (or ~expr ~@exprs))))

(defmacro maybe-id
  ([expr] `(if-let [x# ~expr] (set-attr :id x#) identity))
  ([expr & exprs] `(maybe-href (or ~expr ~@exprs))))

(defmacro maybe-name
  ([expr] `(if-let [x# ~expr] (set-attr :name x#) identity))
  ([expr & exprs] `(maybe-href (or ~expr ~@exprs))))

(defmacro maybe-class
  ([expr] `(if-let [x# ~expr] (set-attr :class x#) identity))
  ([expr & exprs] `(maybe-class (or ~expr ~@exprs))))

(defmacro maybe-type
  ([expr] `(if-let [x# ~expr] (set-attr :type x#) identity))
  ([expr & exprs] `(maybe-class (or ~expr ~@exprs))))

(defmacro maybe-value
  ([expr] `(if-let [x# ~expr] (set-attr :value x#) identity))
  ([expr & exprs] `(maybe-class (or ~expr ~@exprs))))

(defmacro maybe-appear
  ([expr] `(if-let [x# ~expr] (content x#) nil))
  ([expr & exprs] `(maybe-appear (or ~expr ~@exprs))))

(defmacro maybe-prepend
  ([expr] `(if-let [x# ~expr] (prepend x#) nil))
  ([expr & exprs] `(maybe-prepend (or ~expr ~@exprs))))

;new macro, replaces content of nodes with id "blah"
;make it good for reduce! :-)
(defmacro content-by-id [nodes [id content]]
  `(at ~nodes
       ~(read-string (str "[:#" id "]")) (maybe-content content)))

