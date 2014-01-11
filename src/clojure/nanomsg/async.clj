(ns nanomsg.async
  "Async interface to nanomsg using core.async."
  (:import (nanomsg.async IAsyncCallback AsyncSocketProxy)
           (nanomsg ISocket))
  (:require [clojure.core.async :refer [chan put!]]))

(defn recv!
  "Receive data from socket as string."
  [^ISocket sock]
  (let [channel         (chan)
        async-callback  (reify IAsyncCallback
                          (success [_ result]
                            (put! channel result))
                          (fail [_ throwable]
                            (put! channel throwable)))
        async-proxy     (AsyncSocketProxy. sock)]
    (.recvString async-proxy async-callback)
    channel))
