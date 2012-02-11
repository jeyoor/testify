(ns testify.core
    (:use
        ring.adapter.jetty
    )
    (:require
        [testify.debug :as debug]
        [testify.routes :as routes]


    )
)

(defn -main []
  (let [port (System/getenv "PORT")]
    (if port  (run-jetty routes/app {:port (Integer/parseInt port)})
     (run-jetty routes/app {:port 8000}))
    ))
