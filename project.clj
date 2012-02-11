(
    defproject testify "0.0.2"
    :description "A Web Framework for Personal Storying"
    :dependencies 
        [
        [org.clojure/clojure "1.2.0"]
        [org.clojure/clojure-contrib "1.2.0"]
        [compojure "0.6.4"]
        [ring/ring-jetty-adapter "0.3.10"]
        ;Using code straight from github instead
		;[org.clojars.tavisrudd/redis-clojure "1.3.0"]
        [enlive "1.0.0"]
        [commons-pool/commons-pool "1.5.5"]
        ]
  :dev-dependencies [[lein-ring "0.4.5"]]
  :ring {:handler testify.core/app}
 )
