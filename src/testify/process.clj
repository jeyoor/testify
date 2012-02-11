(ns testify.process
    (:use
        testify.remain
        testify.appear
    )
    (:require
        [net.cgrand.enlive-html :as html]
        [testify.process.dyn-html :as dhtml]
        [testify.util :as util]
        [clojure.string :as string]
        [clojure.contrib.io :as io]
    )
)

(defn linknodes
    "given a list of ids, return links pointing at targetIDHERE"
    [ids target]
    (map #(ul-link % (str target %) ) ids)
)

(defn dbl-linknodes
    [ids target1 operation target2]
    (map #(ul-link-delete % (str target1 %) operation (str target2 %)  ) ids)
)
