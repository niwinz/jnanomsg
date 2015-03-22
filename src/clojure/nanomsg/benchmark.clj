(ns nanomsg.benchmark
  (:require [nanomsg.core :as nn]
            [octet.core :as buf])
  (:gen-class))

(defn- sleep
  [t]
  (Thread/sleep t))

(defmacro thread
  [& body]
  `(let [func# (fn [] ~@body)
         thr# (Thread. func#)]
     (.setDaemon thr# true)
     (.start thr#)))

(defn into-buffer
  ([spec data] (into-buffer spec data {}))
  ([spec data opts]
   (let [size (buf/size spec)
         buffer (buf/allocate size opts)]
     (buf/write! buffer data spec opts)
     buffer)))

(def s-bind-tcp "tcp://127.0.0.1:5555")
(def s-bind-ipc "ipc:///tmp/test.ipc")
(def s-bind-inproc "inproc://test")

(def spec1 (buf/spec buf/bool buf/bool))
(def spec2 (buf/spec buf/int64 buf/int64 buf/int64))

(def messages 9000000)

(defn simple-benchmark
  []
  (let [sended (atom 0N)
        received (atom 0N)
        latch (java.util.concurrent.CountDownLatch. 1)
        msg (into-buffer spec1 [true false] {:type :direct})]
    (letfn [(watcher-loop []
              (loop [initial-sended    @sended
                     initial-received  @received]
                (sleep 1000)
                (println (format "Sended: %s/s Received %s/s Totales(%s, %s)"
                                 (- @sended initial-sended)
                                 (- @received initial-received)
                                 @sended @received))
                (recur @sended @received)))
            (server-loop []
              (try
                (with-open [sock (nn/socket :pub {:bind s-bind-tcp})]
                  (dotimes [i messages]
                    (nn/send! sock msg)
                    (swap! sended inc)))
                (catch Exception e
                  (println e))
                (finally
                  (.countDown latch))))
            (client-loop [match]
              (try
                (with-open [sock (nn/socket :sub {:connect s-bind-tcp})]
                  (nn/subscribe! sock match)
                  (loop []
                    (let [buffer (nn/recv! sock)]
                      (swap! received inc)
                      (recur))))
                (catch Exception e
                  (println e))))]

      (thread (server-loop))
      (thread (watcher-loop))
      (thread (client-loop (into-array Byte/TYPE [1])))
      (.await latch)
      (println "finished"))))

;; (defn bench-fn1 []
;;   (let [sended    (atom 0)
;;         received  (atom 0)
;;         pubsock   (nn/socket :pub)]
;;     (nn/bind! pubsock "ipc:///tmp/bench")
;;     (sleep 1000)

;;     (let [reporter (fn []
;;                      (loop [initial-sended    @sended
;;                             initial-received  @received]
;;                        (sleep 1000)
;;                        (println (format "Sended: %s/s Received %s/s"
;;                                         (- @sended initial-sended)
;;                                         (- @received initial-received)))
;;                        (recur @sended @received)))
;;           receiver (fn []
;;                      (let [sock (nn/socket :sub)]
;;                        (nn/connect! sock "ipc:///tmp/bench")
;;                        (nn/subscribe! sock "test")
;;                        (loop []
;;                          (let [r (nn/recv! sock)]
;;                            (swap! received inc))
;;                          (recur))))]

;;       (println "Starting...")

;;       ;; Start receiver and reporter threads
;;       (thread receiver)
;;       (thread reporter)

;;       (doseq [x (range 1000000000000)]
;;         (nn/send! pubsock "test foobar")
;;         (swap! sended inc)))))


;; (defn bench-async []
;;   (let [sended    (atom 0)
;;         received  (atom 0)
;;         s1        (nn/socket :pub {:bind "ipc:///tmp/bench.async" :async true})
;;         s2        (nn/socket :sub {:connect "ipc:///tmp/bench.async" :async true})]
;;     (nn/subscribe! s2 "Hello")
;;     (thread (fn []
;;               (loop [initial-sended    @sended
;;                      initial-received  @received]
;;                 (sleep 1000)
;;                 (println (format "Sended: %s/s Received %s/s"
;;                                  (- @sended initial-sended)
;;                                  (- @received initial-received)))
;;                 (recur @sended @received))))
;;     (async/go
;;       (loop []
;;         (async/<! (nn/send! s1 "Hello World"))
;;         (swap! sended inc)
;;         (recur)))
;;     (async/go
;;       (loop []
;;         (async/<! (nn/send! s1 "Hello World"))
;;         (swap! sended inc)
;;         (recur)))
;;     (async/go
;;       (loop []
;;         (let [recd (async/<! (nn/recv! s2))]
;;           (swap! received inc)
;;           (recur))))))

(defn -main
  []
  (simple-benchmark))
