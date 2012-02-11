(ns testify.test.persist
  (:use [testify.persist])
  (:use [clojure.test]))

(deftest redis-store
    (is (= "OK" (store "foo" "bar")))
)

(deftest redis-fetch
    (is (= "bar" (fetch "foo")))
)