(ns nanomsg.benchmark
  (:require [nanomsg :as nn])
  (:gen-class))

(defmacro thread
  [& body]
  `(let [func#  (fn [] ~@body)
         t#     (Thread. func#)]
     (.setDaemon t# false)
     (.start t#)
     t#))

(defn- sleep
  [t]
  (Thread/sleep t))

(defn thread-interrumped?
  "Checks if a current thread has a interrumped
  flag activated."
  []
  (.isInterrupted (Thread/currentThread)))

(defn run-interval
  "Execute a function repeatedly every one interval.

  This function use a safe way to execute a loop
  for execute in one thread, because it checks
  thread interruption on each loop iteration."
  ([f] (run-interval f 0))
  ([f msecs]
   (while (complement thread-interrumped?)
     (do
       (f)
       (when (> msecs 0)
         (sleep msecs))))))

(defn bench-reqrep []
  (println "Start Req/Rep Benchmark...")
  (let [c   (atom 0)
        t1  (thread (let [s (nn/socket :rep {:bind "ipc:///tmp/bench-reqrep"})]
                       (run-interval #(let [received (nn/recv-bytes! s)]
                                        (nn/send-bytes! s received)))
                       (nn/close! s)))
        t2  (thread (let [s    (nn/socket :req {:connect "ipc:///tmp/bench-reqrep"})
                          data (into-array Byte/TYPE (take 100 (repeatedly #(rand-int 20))))]
                       (run-interval #(do
                                        (nn/send-bytes! s data)
                                        (nn/recv-bytes! s)
                                        (swap! c inc)))
                       (nn/close! s)))]
    (println "Collecting data (10s)...")
    (sleep 10000)
    (.interrupt t2)
    (.interrupt t1)
    (println (format "req/rep: %s/s" (/ @c 10.0)))
    (nn/terminate!)))


(defn -main []
  ;; (let [sended    (atom 0)
  ;;       received  (atom 0)
  ;;       pubsock   (nn/socket :pub)]
  ;;   (nn/bind! pubsock "ipc:///tmp/bench")
  ;;   (sleep 1000)

  ;;   (let [reporter (fn []
  ;;                    (loop [initial-sended    @sended
  ;;                           initial-received  @received]
  ;;                      (sleep 1000)
  ;;                      (println (format "Sended: %s/s Received: %s/s"
  ;;                                       (- @sended initial-sended)
  ;;                                       (- @received initial-received)))
  ;;                      (recur @sended @received)))
  ;;         receiver (fn []
  ;;                    (let [sock (nn/socket :sub)]
  ;;                      (nn/connect! sock "ipc:///tmp/bench")
  ;;                      (nn/subscribe! sock "test")
  ;;                      (loop []
  ;;                        (let [r (nn/recv! sock)]
  ;;                          (swap! received inc))
  ;;                        (recur))))]

  ;;     (println "Starting...")

  ;;     ;; Start receiver and reporter threads
  ;;     (thread receiver)
  ;;     (thread reporter)

  ;;     (doseq [x (range 1000000000000)]
  ;;       (nn/send! pubsock "test foobar")
  ;;       (swap! sended inc)))))
