(ns testify.process.dyn-html
    (:use
        testify.remain
        testify.appear
    )
    (:require
        [redis.core :as redis]
        [net.cgrand.enlive-html :as html]
    )
)

(defn get-html [id]
    (do-redis
        (fetch (str "template:" id ":html") )
    )
)

(defn get-nodes [txt]
    ;parse the snippet
     (html/html-snippet txt)
)

;See DESIGN for CSS field types
(defn get-fields [nodes]
    (html/select nodes #{[:.text] [:.image] [:.video] [:.multitext]})
)

;for each node in list, destructure to find its type
(defn get-types [nodelist]
    (map #(let [{{classy :class} :attrs} %] classy) nodelist)
)

(defn get-ids [nodelist]
    (map #(let [{{idy :id} :attrs} %] idy) nodelist)
)

(defn map-ids-types [nodelist]
    (zipmap  (get-types nodelist) (get-ids nodelist))
)

(defn part-ids-types [nodelist]
    (partition 2 (interleave  (get-types nodelist) (get-ids nodelist)))
)

;TODO use this later for substitutions
(defn zap-vids [nodes]
    (html/at nodes
        [:.video] (html/substitute (html/html-snippet  "<script>HELLO</script>"))
    )
)



;TESTING
(def node3 (get-nodes "<p id=name class=text>Namehere </p> <img src=cool /> <p> other tag </p> <img id=face src=blag.jpg class=image /> <video id=smile class=video ") )
;TODO
(defn testy []
     (map #(apply input-row %) (part-ids-types (get-fields node3)))
)
(defn testy2 []
    (base "Enter data" (form "POST" "write/template"  (testy)))
)
