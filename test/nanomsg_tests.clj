(ns nanomsg-tests
  (:require [nanomsg.core :as nn]
            [nanomsg.async :as nna]
            [clojure.core.async :refer [<! >! go put! take!]]
            [clojure.test :refer :all]))

(def s-bind-tcp "tcp://127.0.0.1:5555")
(def s-bind-ipc "ipc:///tmp/test.ipc")
(def s-bind-inproc "inproc://test")

(defn sleep
  [t]
  (Thread/sleep t))

;; (deftest core-async-tests
;;   (testing "Channel send and receive strings."
;;     (let [p  (promise)
;;           c1 (nna/chan :socktype :push :bind (str s-bind-ipc ".async1"))
;;           c2 (nna/chan :socktype :pull :connect (str s-bind-ipc ".async1"))]
;;       (put! c1 "foo")
;;       (take! c2 (fn [v] (deliver p v)))
;;       (is (= @p "foo"))))
;;   (testing "Channel send and receive clojure maps"
;;     (let [p  (promise)
;;           c1 (nna/chan :socktype :push :bind (str s-bind-ipc ".async2"))
;;           c2 (nna/chan :socktype :pull :connect (str s-bind-ipc ".async2"))]
;;       (put! c1 {:foo 1})
;;       (take! c2 (fn [v] (deliver p v)))
;;       (is (= @p {:foo 1})))))

(deftest socket-req-rep
  (testing "ReqRep"
    (with-open [req-sock (nn/socket :req)]
      (let [result (future
                     (with-open [rep-sock (nn/socket :rep)]
                       (nn/bind! rep-sock s-bind-ipc)
                       (let [received (nn/recv! rep-sock)]
                         (nn/send! rep-sock received)
                         received)))]
        (sleep 500)
        (nn/connect! req-sock s-bind-ipc)
        (nn/send! req-sock "foo")
        (is (= (nn/recv! req-sock) "foo"))))))

(deftest socket-pub-sub
  (testing "PubSub"
    (with-open [pub-sock (nn/socket :pub)]
      (nn/bind! pub-sock s-bind-ipc)
      (let [recvd1 (future
                     (with-open [sub-socket (nn/socket :sub)]
                       (nn/connect! sub-socket s-bind-ipc)
                       (nn/subscribe! sub-socket "ss1")
                       (nn/recv! sub-socket)))
            recvd2 (future
                     (with-open [sub-socket (nn/socket :sub)]
                       (nn/connect! sub-socket s-bind-ipc)
                       (nn/subscribe! sub-socket "ss2")
                       (nn/recv! sub-socket)))]

        (sleep 500)
        (nn/send! pub-sock "ss2 foo")
        (nn/send! pub-sock "ss1 bar")

        (is (= @recvd1 "ss1 bar"))
        (is (= @recvd2 "ss2 foo"))))))

(deftest socket-pipeline
  (testing "Pipeline"
    (let [sockname  (str s-bind-ipc "pipeline")]
      (with-open [push-sock (nn/socket :push {:bind sockname})]
        (let [receiver  (future
                          (with-open [pull-sock (nn/socket :pull {:connect sockname})]
                            (let [received  (nn/recv! pull-sock)]
                              (nn/close! pull-sock)
                              received)))]
          (sleep 500)
          (nn/send! push-sock "message1")
          (is (= @receiver "message1")))))))


(deftest socket-pipeline
  (testing "Pair"
    (with-open [sock (nn/socket :pair)]
      (nn/bind! sock s-bind-tcp)
      (let [recvd1 (future
                     (with-open [socket (nn/socket :pair)]
                       (nn/connect! socket s-bind-tcp)
                       (nn/recv! socket)))]
        (sleep 500)
        (nn/send! sock "foo")
        (is (= @recvd1 "foo"))))))

(deftest socket-pipeline
  (testing "Bus"
    (with-open [sock (nn/socket :bus)]
      (nn/bind! sock s-bind-ipc)
      (let [recvd1   (future
                       (with-open [socket (nn/socket :bus)]
                         (nn/connect! socket s-bind-ipc)
                         (nn/recv! socket)))
            recvd2   (future
                       (with-open [socket (nn/socket :bus)]
                         (nn/connect! socket s-bind-ipc)
                         (nn/recv! socket)))]
        (sleep 500)
        (nn/send! sock "foo")
        (is (= @recvd1 "foo"))
        (is (= @recvd2 "foo"))))))
