(ns nanomsg.async
  "Core async channels on top of jnanomsg."
  (:import (nanomsg.async IAsyncCallback))
  (:require [nanomsg :as nn]
            [clojure.core.async :as async]
            [clojure.core.async.impl.protocols :as protocols]))

(defn- put!-impl
  [sock value handler]
  (let [callback  (reify IAsyncCallback
                    (success [_ result]
                      (let [cb (protocols/commit handler)]
                        (cb)))
                    (fail [_ throwable]
                      (let [cb (protocols/commit handler)]
                        (cb throwable))))]
    (.sendString sock value callback)
    nil))

(defn- take!-impl
  [sock handler]
  (let [callback  (reify IAsyncCallback
                    (success [_ result]
                      (let [cb (protocols/commit handler)]
                        (cb result)))
                    (fail [_ throwable]
                      (let [cb (protocols/commit handler)]
                        (cb throwable))))]
    (.recvString sock callback)
    nil))

(deftype NanomsgWriteChannel [sock]
  protocols/WritePort
  (put! [_ value fn1-handler]
    (put!-impl sock value fn1-handler)))

(deftype NanomsgReadChannel [sock]
  protocols/ReadPort
  (take! [_ fn1-handler]
    (take!-impl sock fn1-handler)))

(deftype NanomsgReadWriteChannel [sock]
  protocols/ReadPort
  (take! [_ fn1-handler]
    (take!-impl sock fn1-handler))
  protocols/WritePort
  (put! [_ value fn1-handler]
    (put!-impl sock value fn1-handler)))

(defn channel
  "Channel constructor. Returns a core.async compatible
  channel that works over jnanomsg async socket interface.

  Example with connect:

    (let [c1 (channel :socktype :pull :connect \"tcp:///tmp/sock\")]
      (println \"Hello:\" (<!! c1)))


  Example with bind:

    (let [c1 (channel :socktype :push :bind \"tcp:///tmp/sock\")]
      (>!! c1 \"pepe\"))

  At this moment, janomsg channels only allows send strings
  over channels (temporary) and has support of small subset
  of socket types: `:push`, `:pull`, `:sub`, `:pub`, `:req` and `:rep`."
  [& {:keys [bind connect socktype subscribe]}]
  {:pre [(boolean (and socktype (or bind connect)))]}
  (let [s   (nn/socket socktype {:async true})
        px  (:px s)
        ch  (case socktype
              :push (NanomsgWriteChannel. px)
              :pull (NanomsgReadChannel. px)
              :pub  (NanomsgWriteChannel. px)
              :sub  (NanomsgReadChannel. px)
              :req  (NanomsgReadWriteChannel. px)
              :rep  (NanomsgReadWriteChannel. px))]
    (cond
      bind (nn/bind! s bind)
      connect (nn/connect! s connect))
    (when subscribe
      (nn/subscribe! s subscribe))
    ch))
