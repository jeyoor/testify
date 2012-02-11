(ns testify.test.core
  (:use [testify.core])
  (:use [clojure.test]))

(deftest not-found
   (let [req {:uri "/error"} resp (app req)]
    (is (= 404 (:status resp)))
   )
)

