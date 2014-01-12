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
           (nanomsg ISocket Socket Nanomsg Device)
           (nanomsg.async IAsyncCallback AsyncSocketProxy))
  (:require [clojure.core.async :refer [chan put!]]))

(def ^:static ^:private supported-sockets
  {:pub PubSocket   :sub SubSocket
   :req ReqSocket   :rep RepSocket
   :bus BusSocket   :pair PairSocket
   :push PushSocket :pull PullSocket})

(defn- make-iasynccallback
  "Private function that create a new
  instance of IAsyncCallback."
  [ch]
  (reify IAsyncCallback
    (success [_ result]
      (put! ch result))
    (fail [_ throwable]
      (put! ch throwable))))

(defprotocol INNSocket
  (bind! [_ dir] "Bind to endpoint specified by dir parameter.")
  (connect! [_ dir] "Connect to endpoint specified by dir parameter.")
  (subscribe! [_ pattern] "Subscribe current socket to specified pattern.")
  (send! [_ data] [_ data blocking] "Send string using a socket.")
  (recv! [_] [_ blocking] "Receive string using a socket.")
  (send-bytes! [_ data] [_ data blocking] "Send bytes using socket.")
  (recv-bytes! [_] [_ blocking] "Receive bytes using socket.")
  (close! [_] "Close a socket."))

(defrecord Connection [s]
  INNSocket
  (bind! [_ endpoint]
    (.bind s endpoint))
  (connect! [_ endpoint]
    (.connect s endpoint))
  (subscribe! [_ pattern]
    (.subscribe s pattern))
  (send-bytes! [_ data]
    (.sendBytes s data))
  (recv-bytes! [_]
    (.recvBytes s))
  (send-bytes! [_ data blocking]
    (.sendBytes s data blocking))
  (recv-bytes! [_ blocking]
    (.recvBytes s blocking))
  (send! [_ data]
    (.sendString s data))
  (recv! [_]
    (.recvString s))
  (send! [_ data blocking]
    (.sendString s data blocking))
  (recv! [_ blocking]
    (.recvString s blocking))
  (close! [_]
    (.close s)))

(defrecord AsyncConnection [s px]
  INNSocket

  (bind! [_ endpoint]
    (.bind s endpoint))
  (connect! [_ endpoint]
    (.connect s endpoint))
  (subscribe! [_ pattern]
    (.subscribe s pattern))
  (send-bytes! [_ data]
    (let [ch (chan)
          cb (make-iasynccallback ch)]
      (.sendBytes px cb)
      ch))
  (recv-bytes! [_]
    (let [ch (chan)
          cb (make-iasynccallback ch)]
      (.recvBytes px cb)
      ch))
  (send! [_ data]
    (let [ch (chan)
          cb (make-iasynccallback ch)]
      (.sendString px data cb)
      ch))
  (recv! [_]
    (let [ch (chan)
          cb (make-iasynccallback ch)]
      (.recvString px cb)
      ch))
  (close! [_]
    (.close s))
  (send! [_ data blocking]
    (throw (UnsupportedOperationException. "Unsuporded arity for async connection.")))
  (recv! [_ blocking]
    (throw (UnsupportedOperationException. "Unsuporded arity for async connection.")))
  (send-bytes! [_ data blocking]
    (throw (UnsupportedOperationException. "Unsuporded arity for async connection.")))
  (recv-bytes! [_ blocking]
    (throw (UnsupportedOperationException. "Unsuporded arity for async connection.")))
  )

(defn socket
  "Geiven a socket type, create a new instance
  of corresponding socket."
  ([^clojure.lang.Keyword socktype] (socket socktype {}))
  ([^clojure.lang.Keyword socktype opts]
   {:pre [(supported-sockets socktype)]}
   (let [cls        (-> socktype supported-sockets)
         instance   (.newInstance cls)
         conn       (if (:async opts)
                      (->AsyncConnection instance (AsyncSocketProxy. instance))
                      (->Connection instance))]
     (cond
       (:bind opts) (bind! conn (:bind opts))
       (:connect opts) (connect! conn (:connect opts)))
     conn)))

(defn terminate!
  "Send terminate signal to all open and/or blocked
  sockets.
  This is usefull for multithreaded apps."
  []
  (Nanomsg/terminate))

(defn start-device
  "Given two sockets, start a device."
  [^Socket s1, ^Socket s2]
  (let [d (Device. s1 s2)]
    (.run d)))

(defn- resolve-symbols []
  (into {} (for [[k v] (Nanomsg/getSymbols)]
             [(keyword (.toLowerCase k)) v])))

(def ^{:doc "Get all symbols"}
  symbols (memoize resolve-symbols))
