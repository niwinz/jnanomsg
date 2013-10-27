(ns nanomsg.test
  (:require [nanomsg :as nn])
  (:gen-class))


(defn -main
  [& args]
  (let [pub-sock (nn/socket :pub)
        sub-sock (nn/socket :sub)]
    (nn/bind pub-sock "ipc:///tmp/test.ipc")
    (nn/connect sub-sock "ipc:///tmp/test.ipc")

    (nn/subscribe sub-sock "kk")

    (println "Init:", pub-sock sub-sock)

    (println "Sending...")
    (nn/send pub-sock "kk hello world")

    (println "Receiving...")
    (println (nn/recv sub-sock))))
