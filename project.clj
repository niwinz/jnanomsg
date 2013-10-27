(defproject clj-nanomsg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [net.java.dev.jna/jna "4.0.0"]]
  :jvm-opts ["-Djna.debug_load=true"
             "-Djna.library.path=/home/niwi/niwi-slides/extend-python-2/sources"
             "-server" "-Xmx4g" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"]

  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :main nanomsg.test
  :profiles {:uberjar {:aot :all}})
