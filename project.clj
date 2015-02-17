(defproject veman "0.1.0-SNAPSHOT"
  :description "Vand-E-Man Exploration"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
  [[org.clojure/clojure "1.6.0"]
   [org.scream3r/jssc "2.8.0"]
   [midje "1.6.3"]]
  :main veman.core


  :profiles {:dev {:dependencies [[midje "1.6.3"]]}}
  )

