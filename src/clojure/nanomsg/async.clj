(ns nanomsg.async
  "Core async channels on top of jnanomsg."
  (:import (nanomsg.async IAsyncCallback))
  (:require [nanomsg :as nn]
            [taoensso.nippy :as nippy]
            [clojure.core.async :as async]
            [clojure.core.async.impl.protocols :as protocols]))

(defn- put!-impl
  [sock value handler]
  (let [async-sock  (:px sock)
        callback    (reify IAsyncCallback
                      (success [_ result]
                        (let [cb (protocols/commit handler)]
                          (cb)))
                      (fail [_ throwable]
                        (let [cb (protocols/commit handler)]
                          (cb false))))
        data        (nippy/freeze value)]
    (.sendBytes async-sock data callback)
    nil))

(defn- take!-impl
  [sock handler]
  (let [async-sock  (:px sock)
        callback    (reify IAsyncCallback
                      (success [_ result]
                        (let [cb    (protocols/commit handler)
                              data  (nippy/thaw result)]
                          (cb data)))
                      (fail [_ throwable]
                        (let [cb (protocols/commit handler)]
                          (cb nil))))]
    (.recvBytes async-sock callback)
    nil))

(defn- close!-impl
  [sock closed]
  (swap! closed (fn [closed]
                  (if-not closed
                    (do
                      (nn/close! sock)
                      (not closed))
                    closed))))


(deftype NanomsgWriteChannel [sock closed]
  protocols/WritePort
  (put! [_ value fn1-handler]
    (if @closed
      (delay false)
      (put!-impl sock value fn1-handler)))

  protocols/Channel
  (close! [_]
    (close!-impl sock closed)))

(deftype NanomsgReadChannel [sock closed]
  protocols/ReadPort
  (take! [_ fn1-handler]
    (take!-impl sock fn1-handler))

  protocols/Channel
  (close! [_]
    (close!-impl sock closed)))

(defn chan
  "Channel constructor. Returns a core.async compatible
  channel that works over jnanomsg async socket interface.

  Example with connect:
    (let [c1 (channel :socktype :pull :connect \"tcp:///tmp/sock\")]
      (println \"Hello:\" (<!! c1)))

  Example with bind:
    (let [c1 (channel :socktype :push :bind \"tcp:///tmp/sock\")]
      (>!! c1 \"pepe\"))

  At this moment, janomsg channels only has support of
  small subset of socket types: `:push` and `:pull`."
  [& {:keys [bind connect socktype]}]
  {:pre [(boolean (and socktype (or bind connect)))]}
  (let [s   (nn/socket socktype {:async true})
        ch  (case socktype
              :push (NanomsgWriteChannel. s (atom false))
              :pull (NanomsgReadChannel. s (atom false)))]
    (cond
      bind (nn/bind! s bind)
      connect (nn/connect! s connect))
    ch))
