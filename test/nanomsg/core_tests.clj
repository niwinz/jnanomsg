(ns nanomsg.core-tests
  (:require [nanomsg.core :as nn]
            [clojure.test :refer :all]))

(def s-bind-tcp "tcp://127.0.0.1:5555")
(def s-bind-ipc "ipc:///tmp/test.ipc")
(def s-bind-inproc "inproc://test")

(use-fixtures :each
  (fn [f]
    (f)
    (Thread/sleep 100)
    (nn/terminate!)))

(deftest reqrep-test
  (future
    (with-open [sock (nn/socket :rep {:bind s-bind-ipc})]
      (let [received (nn/recv-str! sock)]
        (nn/send! sock (str received " world")))
      (let [received (nn/recv-str! sock)]
        (nn/send! sock (str received " world")))))

  (with-open [sock (nn/socket :req {:connect s-bind-ipc})]
    (Thread/sleep 200)

    (nn/send! sock "hello")
    (let [result (nn/recv-str! sock)]
      (is (= result "hello world")))

    (nn/send! sock "test")
    (let [result (nn/recv-str! sock)]
      (is (= result "test world")))))

(deftest multiple-reqrep-tcp-test
  (letfn [(start-server [name]
            (with-open [sock (nn/socket :rep)]
              (nn/bind! sock s-bind-tcp)
              (loop [counter 0]
                (let [data (nn/recv-str! sock)]
                  (nn/send! sock (str "rsp:" name ":" counter))
                  (recur (inc counter))))))]
    (future
      (try
        (start-server "s1")
        (catch Exception e
          (println "S1 error")
          (.printStackTrace e))))

    (future
      (try
        (start-server "s2")
        (catch Exception e
          (println "S2 error")
          (.printStackTrace e))))

    (Thread/sleep 200)

    (with-open [sock (nn/socket :req)]
      (nn/connect! sock s-bind-tcp)

      (dotimes [i 10]
        (nn/send! sock (str "=request: " i))
        (let [response (nn/recv-str! sock)]
          (println "=response: " response))))))

(deftest poller-test
  (future
    (try
      (with-open [sock (nn/socket :rep {:bind s-bind-tcp})]
        (let [received (nn/recv! sock)]
          (nn/send! sock received)))
      (catch Exception e
        (.printStackTrace e))))

  (with-open [sock (nn/socket :req {:connect s-bind-tcp})]
    (let [poller (nn/poller 1)]
      (nn/register! poller sock #{:poll-out})
      (is (not (nn/readable? poller sock)))
      (is (not (nn/writable? poller sock)))
      (nn/poll! poller 1000)

      (is (not (nn/readable? poller sock)))
      (is (nn/writable? poller sock))
      (nn/send! sock "hello" false)

      (nn/unregister! poller sock)
      (nn/register! poller sock #{:poll-in})

      (nn/poll! poller 1000)
      (is (nn/readable? poller sock))
      (is (not (nn/writable? poller sock)))

      ;; (is (= "hello" (nn/recv-str! sock #{:dont-wait})))
      (is (= "hello" (nn/recv-str! sock {:dont-wait true})))

      )))

(deftest pipeline-test
  (let [p1 (promise)
        p2 (promise)]
    (future
      (with-open [sock (nn/socket :pull {:connect s-bind-tcp})]
        (let [received (nn/recv-str! sock)]
          (deliver p1 received))
        (let [received (nn/recv-str! sock)]
          (deliver p2 received))))

    (with-open [sock (nn/socket :push {:bind s-bind-tcp})]
      (nn/send! sock "hello")
      (nn/send! sock "world"))

    (let [result1 (deref p1 1000 nil)
          result2 (deref p2 1000 nil)]
      (is (= result1 "hello"))
      (is (= result2 "world")))))

(deftest pair-test
  (let [r1 (future
             (with-open [sock (nn/socket :pair {:bind s-bind-tcp})]
               (Thread/sleep 500)
               (let [received (nn/recv-str! sock)]
                 (nn/send! sock "foobar2")
                 received)))
        r2 (future
             (with-open [sock (nn/socket :pair {:connect s-bind-tcp})]
               (Thread/sleep 500)
               (nn/send! sock "foobar1")
               (let [received (nn/recv-str! sock)]
                 received)))]

    (let [result1 (deref r1 2000 nil)
          result2 (deref r2 2000 nil)]
      (is (= result2 "foobar2"))
      (is (= result1 "foobar1")))))

(deftest pubsub-test
  (with-open [sock1 (nn/socket :pub)
              sock2 (nn/socket :sub)]
    (nn/bind! sock1 s-bind-ipc)
    (nn/connect! sock2 s-bind-ipc)
    (nn/subscribe! sock2 "hello")
    (let [f1 (future
               (nn/send! sock1 "hello|world"))]
      (let [result (nn/recv-str! sock2)]
        (is (= result "hello|world"))))))

(deftest bus-test
  (let [p1 (future
             (with-open [sock (nn/socket :bus {:connect s-bind-inproc})]
               (nn/recv-str! sock)))

        p2 (future
             (with-open [sock (nn/socket :bus {:connect s-bind-inproc})]
               (nn/recv-str! sock)))]

    (future
      (with-open [sock (nn/socket :bus {:bind s-bind-inproc})]
        (Thread/sleep 200)
        (nn/send! sock "hello")))

    (let [result1 (deref p1 1000 nil)
          result2 (deref p2 1000 nil)]
      (is (= result1 "hello"))
      (is (= result2 "hello")))))
