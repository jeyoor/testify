(ns testify.debug
    (:require
        [redis.core :as redis]
        [testify.appear :as appear]
        [testify.persist :as persist]
        [clojure.string :as string]
        [net.cgrand.enlive-html :as html]))

(defn echo [params] 
  (str "<h1> hello </h1>" params "<br />" (get  params 
                                                :gender)))
(defn dumpy [prefix] 
  (persist/gist prefix))

(defn fetchy [key] 
  (str key " returned " (persist/fetch key))
)

(defn storey [key val] 
  (str "set " key " to " val " returned " (persist/store key val)))


(defn bfetch [kay] 
  (persist/do-redis (redis/get kay)))

(defn bstore [kay] 
  (persist/do-redis (redis/set kay "testvalue")))

