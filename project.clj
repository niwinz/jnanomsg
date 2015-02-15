(defproject jnanomsg "0.4.0-SNAPSHOT"
  :description "Nanomsg clojure/java wrapper."
  :url "https://github.com/niwibe/jnanomsg"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [net.java.dev.jna/jna "4.1.0"]]
  :jvm-opts ["-server" "-Xmx2g" "-XX:+UseG1GC"]
  :javac-options ["-target" "1.7" "-source" "1.7" "-Xlint:-options"]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :plugins [[lein-javadoc "0.1.1"]
            [codox "0.6.8"]]
  :javadoc-opts {:output-dir "doc/api/java"
                 :package-names ["nanomsg" "nanomsg.jna" "nanomsg.pubsub" "nanomsg.exceptions"
                                 "nanomsg.reqrep" "nanomsg.bus" "nanomsg.pair" "nanomsg.pipeline"]}
  :codox {:output-dir "doc/api/clojure"
          :src-dir-uri "http://github.com/niwibe/jnanomsg/blob/master/"
          :src-linenum-anchor-prefix "L"}
  :main nanomsg.benchmark
  :profiles {:uberjar {:aot :all}})
