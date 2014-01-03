(ns nanomsg
  (:refer-clojure :exclude [send])
  (:import (nanomsg.pubsub PubSocket SubSocket)
           (nanomsg.reqrep ReqSocket RepSocket)
           (nanomsg.pair PairSocket)
           (nanomsg.bus BusSocket)
           (nanomsg RWSocket Constants)))

(def ^:static supported-sockets {:pub PubSocket :sub SubSocket
                                 :req ReqSocket :rep RepSocket
                                 :bus BusSocket :pair PairSocket})

(defn bind!
  "Given a socket and connection string,
  add a local endpoint to the socket."
  [^RWSocket sock, ^String dir]
  (.bind sock dir)
  sock)

(defn connect!
  "Given a socket and connection string,
  connect to remote socket."
  [^RWSocket sock, ^String dir]
  (.connect sock dir)
  sock)

(defn socket
  "Geiven a socket type, create a new instance
  of corresponding socket."
  ([^RWSocket socktype] (socket socktype {}))
  ([^RWSocket socktype opts]
   {:pre [(supported-sockets socktype)]}
   (let [cls      (-> socktype supported-sockets)
         instance (.newInstance cls)]
     (cond
       (:bind opts) (bind instance (:bind opts))
       (:connect opts) (connect instance (:connect opts)))
     instance)))

(defn subscribe!
  "Subscribe a current subscriber socket
  to specified string pattern."
  [^SubSocket sock, ^String pattern]
  {:pre [(instance? SubSocket sock)]}
  (.subscribe sock pattern))

(defn send!
  "Send string data"
  [^RWSocket sock, ^String data & {:keys [blocking] :or {blocking true}}]
  {:pre [(string? data)]}
  (.sendString sock data blocking))

(defn recv!
  "Recv data as string"
  [^RWSocket sock & {:keys [blocking] :or {blocking true}}]
  (.recvString sock blocking))

(defn send-bytes!
  "Send bytes data"
  [^RWSocket sock, data & {:keys [blocking] :or {blocking true}}]
  (.sendBytes sock data blocking))

(defn recv-bytes!
  "Recv data as bytes"
  [^RWSocket sock & {:keys [blocking] :or {blocking true}}]
  (.recvBytes sock blocking))

(defn close!
  "Close socket."
  [^RWSocket sock]
  (.close sock))

(defn- resolve-symbols
  []
  (into {} (for [[k v] (Constants/getSymbols)]
             [(keyword (.toLowerCase k)) v])))

(def ^{:doc "Get all symbols"}
  symbols (memoize resolve-symbols))
