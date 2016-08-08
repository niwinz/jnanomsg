(defproject jnanomsg "1.0.0-SNAPSHOT"
  :description "Nanomsg library for Java & Clojure"
  :url "https://github.com/niwibe/jnanomsg"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [net.java.dev.jna/jna "4.2.1"]]

  :javac-options ["-target" "1.8" "-source" "1.8"]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"])

