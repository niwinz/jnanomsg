(ns nanomsg.async
  "Async interface to nanomsg using core.async.

  NOTE: not works because of core.async bug:
    http://dev.clojure.org/jira/browse/ASYNC-48
  "
  (:refer-clojure :exclude [send])
  (:import (nanomsg RWSocket)
           (nanomsg.exceptions EmptyResponseException))
  (:require [clojure.core.async :refer [go <! timeout]]
            [nanomsg :as nn]))

(defn recv
  "Receive data from socket as string."
  [^RWSocket sock]
  (go
    (loop []
      (try
        (nn/recv sock {:blocking false})
        (catch EmptyResponseException e
          (<! (timeout 100))
          (recur))))))

(defn send
  "Send data to socket as string."
  [^RWSocket sock, ^String data]
  (go
    (loop []
      (try
        (nn/send sock data {:blocking false})
        (catch EmptyResponseException e
          (<! (timeout 100))
          (recur))))))
