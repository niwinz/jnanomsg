(ns nanomsg.benchmark
  (:require [nanomsg :as nn])
  (:gen-class))

(defn- thread
  [func]
  (let [th (Thread. func)]
    (.start th)))

(defn- sleep
  [t]
  (Thread/sleep t))


(defn -main
  []
  (let [sended    (atom 0)
        received  (atom 0)
        pubsock   (nn/socket :pub)]
    (nn/bind! pubsock "ipc:///tmp/bench")
    (sleep 1000)

    (let [reporter (fn []
                     (loop [initial-sended    @sended
                            initial-received  @received]
                       (sleep 1000)
                       (println (format "Sended: %s/s Received %s/s"
                                        (- @sended initial-sended)
                                        (- @received initial-received)))
                       (recur @sended @received)))
          receiver (fn []
                     (let [sock (nn/socket :sub)]
                       (nn/connect! sock "ipc:///tmp/bench")
                       (nn/subscribe! sock "test")
                       (loop []
                         (let [r (nn/recv! sock)]
                           (swap! received inc))
                         (recur))))]

      (println "Starting...")

      ;; Start receiver and reporter threads
      (thread receiver)
      (thread reporter)

      (doseq [x (range 1000000000000)]
        (nn/send! pubsock "test foobar")
        (swap! sended inc)))))
