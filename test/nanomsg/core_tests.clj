(ns nanomsg.core-tests
  (:require [nanomsg.core :as nn]
            [octet.core :as buf]
            [clojure.test :refer :all]))

(def s-bind-tcp "tcp://127.0.0.1:5555")
(def s-bind-ipc "ipc:///tmp/test.ipc")
(def s-bind-inproc "inproc://test")

(def spec1 (buf/spec buf/bool buf/bool))
(def spec2 (buf/spec buf/int64 buf/int64 buf/int64))

(use-fixtures :once
  (fn [f]
    (println "start")
    (f)
    (println "terminate")
    (nn/terminate!)))

(defn into-buffer
  ([spec data] (into-buffer spec data {}))
  ([spec data opts]
   (let [size (buf/size spec)
         buffer (buf/allocate size opts)]
     (buf/write! buffer data spec opts)
     buffer)))

(deftest socket-req-rep
  (future
    (with-open [sock (nn/socket :rep {:bind s-bind-tcp})]
      (let [received (nn/recv! sock)]
        (nn/send! sock received))
      (let [received (nn/recv! sock)]
        (nn/send! sock received))))

  (with-open [sock (nn/socket :req {:connect s-bind-tcp})]
    (nn/send! sock (into-buffer spec1 [true false]))
    (let [buffer (nn/recv! sock)
          result (buf/read buffer spec1)]
      (is (= result [true false])))

    (nn/send! sock (into-buffer spec1 [false true]))
    (let [buffer (nn/recv! sock)
          result (buf/read buffer spec1)]
      (is (= result [false true])))))

(deftest socket-pipeline
  (let [p1 (promise)
        p2 (promise)]
    (future
      (with-open [sock (nn/socket :pull {:connect s-bind-tcp})]
        (let [received (nn/recv! sock)]
          (deliver p1 received))
        (let [received (nn/recv! sock)]
          (deliver p2 received))))

    (with-open [sock (nn/socket :push {:bind s-bind-tcp})]
      (nn/send! sock (into-buffer spec1 [true false]))
      (nn/send! sock (into-buffer spec1 [false true]))

    (let [buffer1 (deref p1)
          buffer2 (deref p2)
          result1 (buf/read buffer1 spec1)
          result2 (buf/read buffer2 spec1)]
      (is (= result1 [true false]))
      (is (= result2 [false true]))))))

(deftest socket-pair
  (let [p1 (promise)
        p2 (promise)]
    (future
      (with-open [sock (nn/socket :pair {:connect s-bind-tcp})]
        (nn/send! sock (into-buffer spec1 [true false]))
        (let [received (nn/recv! sock)]
          (deliver p1 received))))

    (future
      (with-open [sock (nn/socket :pair {:bind s-bind-tcp})]
        (let [received (nn/recv! sock)]
          (nn/send! sock (into-buffer spec1 [false true]))
          (deliver p2 received))))

    (let [buffer1 (deref p1)
          buffer2 (deref p2)
          result1 (buf/read buffer1 spec1)
          result2 (buf/read buffer2 spec1)]
      (is (= result2 [true false]))
      (is (= result1 [false true])))))

(deftest socket-pub-sub
  (with-open [sock (nn/socket :pub)]
    (nn/bind! sock s-bind-ipc)

    (let [f1 (future
               (with-open [sock (nn/socket :sub)]
                 (nn/connect! sock s-bind-ipc)
                 (nn/subscribe! sock (into-array Byte/TYPE [1]))
                 (nn/recv! sock)))]

      (nn/send! sock (into-buffer spec1 [true false]))
      (let [buffer @f1
            result (buf/read buffer spec1)]
        (is (= result [true false]))))))

(deftest socket-bus
  (let [p1 (promise)
        p2 (promise)]
    (future
      (with-open [sock (nn/socket :bus {:connect s-bind-inproc})]
        (let [received (nn/recv! sock)]
          (deliver p1 received))))

    (future
      (with-open [sock (nn/socket :bus {:connect s-bind-inproc})]
        (let [received (nn/recv! sock)]
          (deliver p2 received))))

    (future
      (with-open [sock (nn/socket :bus {:bind s-bind-inproc})]
        (nn/send! sock (into-buffer spec1 [false true]))
        (Thread/sleep 500)))

    (let [buffer1 (deref p1)
          buffer2 (deref p2)
          result1 (buf/read buffer1 spec1)
          result2 (buf/read buffer2 spec1)]
      (is (= result1 [false true]))
      (is (= result2 [false true])))))
