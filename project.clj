(
    defproject testify "0.0.2"
    :description "A Web Framework for Personal Storying"
    :dependencies 
        [
        [org.clojure/clojure "1.2.0"]
        [org.clojure/clojure-contrib "1.2.0"]
        [compojure "0.6.4"]
        [ring/ring-jetty-adapter "0.3.10"]
        [enlive "1.0.0"]
        [org.clojars.tavisrudd/redis-clojure "1.3.1-SNAPSHOT"]
        ]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler testify.routes/app}
 )
