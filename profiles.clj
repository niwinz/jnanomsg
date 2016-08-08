{:dev
 {:javadoc-opts {:output-dir "doc/api/java"
                 :package-names ["nanomsg"
                                 "nanomsg.pubsub"
                                 "nanomsg.exceptions"
                                 "nanomsg.reqrep"
                                 "nanomsg.bus"
                                 "nanomsg.pair"
                                 "nanomsg.pipeline"]}

  :codeina {:target "doc/api/clojure"
            :reader :clojure
            :src-uri "http://github.com/niwibe/jnanomsg/blob/master/"
            :src-uri-prefix "#L"
            :exclude [nanomsg.benchmark
                      nanomsg.impl]}

  :jvm-opts ["-server" "-Xmx2g" "-XX:+UseG1GC"]
  :javac-options ^:replace ["-target" "1.8" "-source" "1.8"
                            "-Xlint:-options", "-Xlint:unchecked"]


  :plugins [[lein-javadoc "0.1.1"]
            [lein-ancient "0.6.7"]
            [funcool/codeina "0.3.0"]]
  :main nanomsg.benchmark}

 :uberjar
 {:aot :all}}


