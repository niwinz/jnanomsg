(ns nanomsg
  (:refer-clojure :exclude [send])
  (:import (nanomsg.pubsub PubSocket SubSocket)
           (nanomsg Socket Constants)))

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
  "Send string data"
  [^Socket sock, ^String data]
  {:pre [(string? data)]}
  (.sendString sock data))

(defn recv
  "Recv data as string"
  [^Socket sock]
  (.recvString sock))

(defn send-bytes
  "Send bytes data"
  [^Socket sock, data]
  (.sendBytes sock data))

(defn recv-bytes
  "Recv data as bytes"
  [^Socket sock]
  (.recvBytes sock))

(defn symbols
  "Get all symbols."
  []
  (into {} (for [[k v] (Constants/getSymbols)]
             [(keyword (.toLowerCase k)) v])))
