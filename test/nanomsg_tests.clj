(ns nanomsg-tests
  (:require [nanomsg :as nn]
            [clojure.test :refer :all]))

(def s-bind-tcp "tcp://127.0.0.1:5555")
(def s-bind-ipc "ipc:///tmp/test.ipc")
(def s-bind-inproc "inproc://test")

(defn thread
  [func]
  (let [t (Thread. func)]
    (.start t)))

(defn sleep
  [t]
  (Thread/sleep t))

(deftest main-tests
  (testing "Simple send/reply"
    (let [req-sock (nn/socket :req)
          result (future
                   (let [rep-sock (nn/socket :rep)]
                     (nn/bind rep-sock s-bind-ipc)
                     (let [received (nn/recv rep-sock)]
                       (nn/send rep-sock received)
                       received)))]
      (nn/connect req-sock s-bind-ipc)
      (nn/send req-sock "foo")
      (is (= (nn/recv req-sock) "foo"))))

  (testing "Simple subscribe"
    (let [pub-sock (nn/socket :pub)]
      (nn/bind pub-sock s-bind-ipc)
      (let [recvd1   (future
                       (let [sub-socket (nn/socket :sub)]
                         (nn/connect sub-socket s-bind-ipc)
                         (nn/subscribe sub-socket "ss1")
                         (nn/recv sub-socket)))
            recvd2   (future
                       (let [sub-socket (nn/socket :sub)]
                         (nn/connect sub-socket s-bind-ipc)
                         (nn/subscribe sub-socket "ss2")
                         (nn/recv sub-socket)))]

        (nn/send pub-sock "ss2 foo")
        (nn/send pub-sock "ss1 bar")

        (is (= @recvd1 "ss1 bar"))
        (is (= @recvd2 "ss2 foo"))))))
