(ns nanomsg
  "Nanomsg is a socket library that provides several
  common communication patterns. It aims to make the
  networking layer fast, scalable, and easy to use.

  It is implemented in C, it works on a wide range of
  operating systems with no further dependencies.

  This is a clojure idiomatic wrapper for native C
  libnanomsg library.

  If you want send your more complex data types, create
  a new pair of send/recv functions that serializes and
  deserializers data before send and after receive."
  (:import (nanomsg.pubsub PubSocket SubSocket)
           (nanomsg.reqrep ReqSocket RepSocket)
           (nanomsg.pipeline PushSocket PullSocket)
           (nanomsg.pair PairSocket)
           (nanomsg.bus BusSocket)
           (nanomsg RWSocket Socket Nanomsg Device)))

(def ^:static ^:private supported-sockets
  {:pub PubSocket
   :sub SubSocket
   :req ReqSocket
   :rep RepSocket
   :bus BusSocket
   :pair PairSocket
   :push PushSocket
   :pull PullSocket})

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
  ([^clojure.lang.Keyword socktype] (socket socktype {}))
  ([^clojure.lang.Keyword socktype opts]
   {:pre [(supported-sockets socktype)]}
   (let [cls      (-> socktype supported-sockets)
         instance (.newInstance cls)]
     (cond
       (:bind opts) (bind! instance (:bind opts))
       (:connect opts) (connect! instance (:connect opts)))
     instance)))

(defn subscribe!
  "Subscribe a current subscriber socket
  to specified string pattern."
  [^SubSocket sock, ^String pattern]
  {:pre [(instance? SubSocket sock)]}
  (.subscribe sock pattern))

(defn send-bytes!
  "Given a socket and message as bytes array,
  send it throught the socket.

  NOTE: This function is a low level interface and
  theorically does not has overhead over
  raw socket usage."
  [^RWSocket sock, ^bytes msg & {:keys [blocking] :or {blocking true}}]
  (.sendBytes sock msg blocking))

(defn recv-bytes!
  "Given a socket, receive a message as byte array.

  This function, uses a low level access for
  receiving a message, that removes a overhead
  of creating a message object."
  [^RWSocket sock & {:keys [blocking] :or {blocking true}}]
  (.recvBytes sock blocking))

(defn send!
  "Given a socket and message as string, send
  it throught the socket.

  This function uses a low level method for sending
  messages that removes a overhead of creating
  nanomsg.Message objects.

  utf-8 encoding is unique overhead that
  has this function over using a raw socket."
  [^RWSocket sock, ^String msg & {:keys [blocking] :or {blocking true}}]
  (.sendString sock msg blocking))

(defn recv!
  "Given a socket, receive a message as string.

  This function, uses a low level access for
  receiving a message, that removes a overhead
  of creating a message object.

  utf-8 decoding is a unique overhead
  that has this function over using a raw socket."
  [^RWSocket sock & {:keys [blocking] :or {blocking true}}]
  (.recvString sock blocking))

(defn close!
  "Close socket."
  [^RWSocket sock]
  (.close sock))

(defn start-device
  "Given two sockets, start a device."
  [^Socket s1, ^Socket s2]
  (let [d (Device. s1 s2)]
    (.run d)))

(defn- resolve-symbols
  []
  (into {} (for [[k v] (Nanomsg/getSymbols)]
             [(keyword (.toLowerCase k)) v])))

(def ^{:doc "Get all symbols"}
  symbols (memoize resolve-symbols))
