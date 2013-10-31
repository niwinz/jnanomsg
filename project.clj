(defproject clj-nanomsg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.java.dev.jna/jna "4.0.0"]]
  :jvm-opts ["-server" "-Xmx8g" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"]

  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :plugins [[lein-javadoc "0.1.1"]]
  :javadoc-opts {:output-dir "docs/_build/javadoc"
                 :package-names ["nanomsg" "nanomsg.ffi" "nanomsg.pubsub"]}
  :main nanomsg.benchmark
  :profiles {:uberjar {:aot :all}})
