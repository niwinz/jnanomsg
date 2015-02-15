(ns nanomsg-tests
  (:require [nanomsg.core :as nn]
            ;; [nanomsg.async :as nna]
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

;; (deftest async-socket
;;   (testing "FooBar"
;;     (let [p (promise)
;;           r1 (future
;;                (let [sock (nn/socket :rep {:async true})]
;;                  (println 111)
;;                  (nn/bind! sock s-bind-tcp)
;;                  (nn/recv-str! sock (fn [data error]
;;                                   (println "Server: received data " data error)
;;                                   (nn/send! sock "pong"
;;                                             (fn [& args]
;;                                               (println "Server: echoed data")
;;                                               (nn/close! sock)))))))
;;           r2 (future
;;                (let [sock (nn/socket :req {:async true})]
;;                  (println 222)
;;                  (nn/connect! sock s-bind-tcp)
;;                  (nn/send! sock "foobar" (fn [& args]
;;                                            (println "Client: sended data " args)))
;;                  (nn/recv! sock (fn [data error]
;;                                   (println "Client: received data " data error)
;;                                   (deliver p [data error])
;;                                   (nn/close! sock)))))]
;;       (println @p)
;;       (is (= 1 2)))))


(deftest sync-sockets
  (testing "ReqRep"
    (let [p (promise)]
      (future
        (with-open [sock (nn/socket :rep)]
          (nn/bind! sock s-bind-tcp)
          (let [received (nn/recv! sock)]
            (nn/send! sock received))))
      (future
       (with-open [sock (nn/socket :req)]
          (nn/connect! sock s-bind-tcp)
          (nn/send! sock "foo")
          (deliver p (nn/recv! sock))))

      (println "Result:" @p)))

  ;; (testing "PubSub"
  ;;   (with-open [pub-sock (nn/socket :pub)]
  ;;     (nn/bind! pub-sock s-bind-ipc)
  ;;     (let [recvd1 (future
  ;;                    (with-open [sub-socket (nn/socket :sub)]
  ;;                      (nn/connect! sub-socket s-bind-ipc)
  ;;                      (nn/subscribe! sub-socket "ss1")
  ;;                      (nn/recv-str! sub-socket)))
  ;;           recvd2 (future
  ;;                    (with-open [sub-socket (nn/socket :sub)]
  ;;                      (nn/connect! sub-socket s-bind-ipc)
  ;;                      (nn/subscribe! sub-socket "ss2")
  ;;                      (nn/recv-str! sub-socket)))]

  ;;       (sleep 500)
  ;;       (nn/send! pub-sock "ss2 foo")
  ;;       (nn/send! pub-sock "ss1 bar")

  ;;       (is (= @recvd1 "ss1 bar"))
  ;;       (is (= @recvd2 "ss2 foo")))))

  ;; (testing "Pipeline"
  ;;   (let [sockname  (str s-bind-ipc "pipeline")]
  ;;     (with-open [push-sock (nn/socket :push {:bind sockname})]
  ;;       (let [receiver  (future
  ;;                         (with-open [pull-sock (nn/socket :pull {:connect sockname})]
  ;;                           (let [received  (nn/recv-str! pull-sock)]
  ;;                             (nn/close! pull-sock)
  ;;                             received)))]
  ;;         (sleep 500)
  ;;         (nn/send! push-sock "message1")
  ;;         (is (= @receiver "message1"))))))

  ;; (testing "Pair"
  ;;   (with-open [sock (nn/socket :pair)]
  ;;     (nn/bind! sock s-bind-tcp)
  ;;     (let [recvd1 (future
  ;;                    (with-open [socket (nn/socket :pair)]
  ;;                      (nn/connect! socket s-bind-tcp)
  ;;                      (nn/recv-str! socket)))]
  ;;       (sleep 500)
  ;;       (nn/send! sock "foo")
  ;;       (is (= @recvd1 "foo")))))

  ;; (testing "Bus"
  ;;   (with-open [sock (nn/socket :bus)]
  ;;     (nn/bind! sock s-bind-ipc)
  ;;     (let [recvd1   (future
  ;;                      (with-open [socket (nn/socket :bus)]
  ;;                        (nn/connect! socket s-bind-ipc)
  ;;                        (nn/recv-str! socket)))
  ;;           recvd2   (future
  ;;                      (with-open [socket (nn/socket :bus)]
  ;;                        (nn/connect! socket s-bind-ipc)
  ;;                        (nn/recv-str! socket)))]
  ;;       (sleep 500)
  ;;       (nn/send! sock "foo")
  ;;       (is (= @recvd1 "foo"))
  ;;       (is (= @recvd2 "foo")))))
)
