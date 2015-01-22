(defproject hungry-horris "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "https://github.com/walkermatt/hungry-horris"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]]
  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]
  :source-paths ["src"]
  :cljsbuild {
    :builds [{:id "hungry-horris"
              :source-paths ["src"]
              :compiler {
                :output-to "hungry_horris.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
