(ns nanomsg.benchmark
  (:require [nanomsg :as nn]
            [nanomsg.async :as nna]
            [clojure.core.async :as async])
  (:gen-class))

(defn- thread
  [func]
  (let [th (Thread. func)]
    (.setDaemon th false)
    (.start th)))

(defn- sleep
  [t]
  (Thread/sleep t))


(defn bench-fn1 []
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


(defn bench-async []
  (let [sended    (atom 0)
        received  (atom 0)
        s1        (nn/socket :pub {:bind "ipc:///tmp/bench.async" :async true})
        s2        (nn/socket :sub {:connect "ipc:///tmp/bench.async" :async true})]
    (nn/subscribe! s2 "Hello")
    (thread (fn []
              (loop [initial-sended    @sended
                     initial-received  @received]
                (sleep 1000)
                (println (format "Sended: %s/s Received %s/s"
                                 (- @sended initial-sended)
                                 (- @received initial-received)))
                (recur @sended @received))))
    (async/go
      (loop []
        (async/<! (nn/send! s1 "Hello World"))
        (swap! sended inc)
        (recur)))
    (async/go
      (loop []
        (async/<! (nn/send! s1 "Hello World"))
        (swap! sended inc)
        (recur)))
    (async/go
      (loop []
        (let [recd (async/<! (nn/recv! s2))]
          (swap! received inc)
          (recur))))))


(defn -main
  []
  (bench-fn1))
