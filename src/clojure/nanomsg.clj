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
  (:import nanomsg.pubsub.PubSocket
           nanomsg.pubsub.SubSocket
           nanomsg.reqrep.ReqSocket
           nanomsg.reqrep.RepSocket
           nanomsg.pipeline.PushSocket
           nanomsg.pipeline.PullSocket
           nanomsg.pair.PairSocket
           nanomsg.bus.BusSocket
           nanomsg.Socket
           nanomsg.Nanomsg
           nanomsg.Device
           nanomsg.async.IAsyncCallback
           nanomsg.async.AsyncSocket
           clojure.lang.Keyword
           clojure.lang.IFn)
  (:require [clojure.core.async :refer [chan put!]]))

(def ^{:dynamic true
       :private true}
  *supported-sockets*
  {:pub #(PubSocket.)
   :sub #(SubSocket.)
   :req #(ReqSocket.)
   :rep #(RepSocket.)
   :bus #(BusSocket.)
   :pair #(PairSocket.)
   :push #(PushSocket.)
   :pull #(PullSocket.)})

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
  (subscribe! [_ pattern] "Subscribe current socket to specified topic.")
  (unsubscribe! [_ pattern] "Unsubscribe current socket from specified topic.")
  (send! [_ data] [_ data blocking] "Send string using a socket.")
  (recv! [_] [_ blocking] "Receive string using a socket.")
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
  (unsubscribe! [_ pattern]
    (.unsubscribe s pattern))
  (recv-bytes! [_]
    (.recvBytes s))
  (recv-bytes! [_ blocking]
    (.recvBytes s blocking))
  (recv! [_]
    (.recvString s))
  (send! [_ data]
    (.send s data))
  (send! [_ data blocking]
    (.send s data blocking))
  (recv! [_ blocking]
    (.recvString s blocking))
  (close! [_]
    (.close s))

  java.io.Closeable
  (close [_]
    (.close s)))

(defrecord AsyncConnection [s px]
  INNSocket
  (bind! [_ endpoint]
    (.bind s endpoint))
  (connect! [_ endpoint]
    (.connect s endpoint))
  (subscribe! [_ pattern]
    (.subscribe s pattern))
  (unsubscribe! [_ pattern]
    (.unsubscribe s pattern))
  (recv-bytes! [_]
    (let [ch (chan)
          cb (make-iasynccallback ch)]
      (.recvBytes px cb)
      ch))
  (send! [_ data]
    (let [ch (chan)
          cb (make-iasynccallback ch)]
      (.send px data cb)
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
  (recv-bytes! [_ blocking]
    (throw (UnsupportedOperationException. "Unsuporded arity for async connection."))))

(defn socket
  "Geiven a socket type, create a new instance
  of corresponding socket."
  ([^clojure.lang.Keyword socktype] (socket socktype {}))
  ([^clojure.lang.Keyword socktype opts]
   {:pre [(supported-sockets socktype)]}
   (let [cls        (-> socktype supported-sockets)
         instance   (.newInstance cls)
         conn       (if (:async opts)
                      (->AsyncConnection instance (AsyncSocket. instance))
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
  (into {} (for [[k v] (Nanomsg/symbols)]
             [(keyword (.toLowerCase k)) v])))

(def ^{:doc "Get all symbols"}
  symbols (memoize resolve-symbols))
