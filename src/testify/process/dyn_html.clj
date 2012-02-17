
  (:use testify.remain 
        testify.appear
        testify.html-macros) 
  (:require [redis.core :as redis] 
            [net.cgrand.enlive-html :as html] 
            [testify.util :as util]))

(defn get-html [id] 
  (do-redis (fetch (str "template:" id ":html"))))

;See DESIGN for CSS field types
;TODO: implement .image,.video, and .multitext
(defn get-fields [nodes] 
  (html/select nodes #{[:.text] 
                       [:.image] 
                       [:.video] 
                       [:.multitext]}))


(defn get-types
  "for each node in list, destructure to find its type"
  [nodelist] 
  (map #(let [{{classy :class} :attrs} %] classy)
   nodelist))

(defn get-ids [nodelist] 
  (map #(let [{{idy :id} :attrs} %] idy)
   nodelist))

(defn map-ids-types [nodelist] 
  (zipmap  (get-types nodelist) 
           (get-ids nodelist)))

(defn part-ids-types [nodelist] 
  (partition 2 (interleave  (get-types nodelist) 
                            (get-ids nodelist))))

(defn get-pairs
  "return a list of two-item lists containing fieldname and fieldtype"
  [nodelist] 
  (part-ids-types (get-fields (html/html-snippet nodelist))))

(defn zap-vids [nodes] 
  (html/at nodes [:.video] 
           (html/substitute (html/html-snippet  "<script>HELLO</script>"))))

(defn content-transform
  "Find all nodes with given id and apply content transform to them"
  [nodes [id content]] 
  (html/transform nodes [(keyword (str "#" id))] 
                  (maybe-content content)))

;take list of :id->ncontent subs, and a set of nodes.
;Replace the content of the nodes with id matching :id with ncontent
(defn subsw [nodes subs]
                                  ;replace : with ""
  (let [keyz (map #(util/strip-colon (str %)) (keys subs))
        valz (vals subs)
        paired (partition 2 (interleave keyz valz))]
    ;for each content-value pair, replace keyz with valz
    (html/emit* (reduce content-transform nodes paired))))


;TESTING
(def node3 (html/html-snippet
            (str 
             "<p id=name class=text>Namehere </p> <img src=cool /> <p> other"
             "tag </p> <img id=face src=blag.jpg class=image /> <video"
             "id=smile class=video ")))

;More examples TODO: move to debug later?
(defn testy [] 
  (map #(apply input-row %) 
       (part-ids-types (get-fields node3))))
(defn testy2 [] 
  (base "Enter text, an image URL, or a GodTube video URL" (form "POST" "write/template"  (testy))))
