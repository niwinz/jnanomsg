(ns nanomsg-tests
  (:require [nanomsg :as nn]
            [clojure.test :refer :all]))

(def s-bind-tcp "tcp://127.0.0.1:5555")
(def s-bind-ipc "ipc:///tmp/test.ipc")
(def s-bind-inproc "inproc://test")

(defn sleep
  [t]
  (Thread/sleep t))

(deftest main-tests
  (testing "ReqRep"
    (let [req-sock (nn/socket :req)
          result (future
                   (let [rep-sock (nn/socket :rep)]
                     (nn/bind! rep-sock s-bind-ipc)
                     (let [received (nn/recv! rep-sock)]
                       (nn/send! rep-sock received)
                       received)))]
      (sleep 500)
      (nn/connect! req-sock s-bind-ipc)
      (nn/send! req-sock "foo")
      (is (= (str (nn/recv! req-sock)) "foo"))))

  (testing "PubSub"
    (let [pub-sock (nn/socket :pub)]
      (nn/bind! pub-sock s-bind-ipc)
      (let [recvd1   (future
                       (let [sub-socket (nn/socket :sub)]
                         (nn/connect! sub-socket s-bind-ipc)
                         (nn/subscribe! sub-socket "ss1")
                         (nn/recv! sub-socket)))
            recvd2   (future
                       (let [sub-socket (nn/socket :sub)]
                         (nn/connect! sub-socket s-bind-ipc)
                         (nn/subscribe! sub-socket "ss2")
                         (nn/recv! sub-socket)))]

        (sleep 500)
        (nn/send! pub-sock "ss2 foo")
        (nn/send! pub-sock "ss1 bar")

        (is (= (str @recvd1) "ss1 bar"))
        (is (= (str @recvd2) "ss2 foo")))))

  (testing "Pipeline"
    (let [sockname  (str s-bind-ipc "pipeline")
          push-sock (nn/socket :push {:bind sockname})
          receiver  (future
                      (let [pull-sock (nn/socket :pull {:connect sockname})
                            received  (nn/recv! pull-sock)]
                        (nn/close! pull-sock)
                        received))]
      (sleep 500)
      (nn/send! push-sock "message1")
      (is (= (str @receiver) "message1"))))

  (testing "Pair"
    (let [sock (nn/socket :pair)]
      (nn/bind! sock s-bind-tcp)
      (let [recvd1   (future
                       (let [socket (nn/socket :pair)]
                         (nn/connect! socket s-bind-tcp)
                         (nn/recv! socket)))]
        (sleep 500)
        (nn/send! sock "foo")
        (is (= (str @recvd1) "foo")))))

  (testing "Bus"
    (let [sock (nn/socket :bus)]
      (nn/bind! sock s-bind-ipc)
      (let [recvd1   (future
                       (let [socket (nn/socket :bus)]
                         (nn/connect! socket s-bind-ipc)
                         (nn/recv! socket)))
            recvd2   (future
                       (let [socket (nn/socket :bus)]
                         (nn/connect! socket s-bind-ipc)
                         (nn/recv! socket)))]

        (sleep 500)
        (nn/send! sock "foo")
        (is (= (str @recvd1) "foo")
        (is (= (str @recvd2) "foo")))))))
