(ns nanomsg
  (:refer-clojure :exclude [send])
  (:import (nanomsg.pubsub PubSocket SubSocket)
           (nanomsg Socket)))

(def ^:private supported-sockets {:pub PubSocket
                                  :sub SubSocket})
(defn socket
  "Create a new socket."
  [^clojure.lang.Keyword socktype]
  {:pre [(contains? supported-sockets socktype)]}
  (let [cls (-> socktype supported-sockets)]
    (.newInstance cls)))

(defn bind
  "Bind socket."
  [^Socket sock, ^String dir]
  (.bind sock dir)
  sock)

(defn connect
  "Connect socket to dir."
  [^Socket sock, ^String dir]
  (.connect sock dir)
  sock)

(defn subscribe
  "Subscribe to some string pattern."
  [^SubSocket sock, ^String pattern]
  {:pre [(instance? SubSocket sock)]}
  (.subscribe sock pattern))

(defn send
  "Send data throught socket."
  [^Socket sock, ^String data]
  (.send sock data))

(defn recv
  "Recv data throught socket."
  [^Socket sock]
  (.recv sock))
