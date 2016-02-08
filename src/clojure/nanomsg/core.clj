(ns nanomsg.core
  "Nanomsg is a socket library that provides several
  common communication patterns. It aims to make the
  networking layer fast, scalable, and easy to use.

  It is implemented in C, it works on a wide range of
  operating systems with no further dependencies.

  This is a clojure idiomatic wrapper for native C
  nanomsg library.

  If you want send your more complex data types, create
  a new pair of send/recv functions that serializes and
  deserializers data before send and after receive."
  (:require [nanomsg.proto :as p]
            [nanomsg.impl :as impl])
  (:import nanomsg.Socket
           nanomsg.async.AsyncSocket
           nanomsg.Nanomsg
           nanomsg.Device
           nanomsg.Poller
           java.nio.ByteBuffer
           clojure.lang.Keyword))

(declare bind!)
(declare connect!)

(defn socket
  "Geiven a socket type, create a new instance
  of corresponding socket."
  ([type] (socket type nil))
  ([type opts]
   {:pre [(contains? impl/+socket-types-map+ type)]}
   (let [factory (get impl/+socket-types-map+ type)
         conn (factory)]
     (cond
       (:bind opts) (bind! conn (:bind opts))
       (:connect opts) (connect! conn (:connect opts)))
     conn)))

(defn bind!
  "Bind given socket to specified endpoint."
  [socket endpoint]
  (p/-bind socket endpoint))

(defn connect!
  "Connect given socket to specified endpoint."
  [socket endpoint]
  (p/-connect socket endpoint))

(defn subscribe!
  "Subscribe given socket to specified topic."
  [socket topic]
  (p/-subscribe socket topic))

(defn unsubscribe!
  "Unsubscribe given socket from specified topic."
  [socket topic]
  (p/-unsubscribe socket topic))

(defn send!
  "Send data through given socket."
  ([socket data]
   (send! socket data true))
  ([socket data blocking]
   (p/-send socket data blocking)))

(defn recv!
  "Receive data through given socket."
  ([socket]
   (recv! socket true))
  ([socket blocking]
   (p/-recv socket blocking)))

(defn recv-str!
  "Receive data through given socket."
  ([socket]
   (recv-str! socket true))
  ([socket blocking]
   (let [^ByteBuffer buffer (p/-recv socket blocking)
         ^bytes data (byte-array (.remaining buffer))]
     (doto buffer .mark (.get data) .reset)
     (String. data "UTF-8"))))

(defn close!
  "Close socket."
  [^java.io.Closeable socket]
  (.close socket))

(defn poller
  "Create a new poller instance."
  ([] (Poller.))
  ([n] (Poller. n))
  ([n ms] (Poller. n ms)))

(defn register!
  "Register a socket into the poller."
  ([p socket]
   (p/-register p socket #{:poll-in :poll-out}))
  ([p socket flags]
   (p/-register p socket flags)))

(defn unregister!
  "Unregister the socker from the poller."
  [p socket]
  (p/-unregister p socket))

(defn poll!
  "Poll a set of registered sockets for readability
  and/or writability."
  ([p] (p/-poll p Poller/TIMEOUT_DEFAULT))
  ([p ms] (p/-poll p ms)))

(defn readable?
  "Check if socket is readable."
  [p socket]
  (p/-readable? p socket))

(defn writable?
  "Check if socket is writable."
  [p socket]
  (p/-writable? p socket))

(defn terminate!
  "Send terminate signal to all open and/or blocked sockets.
  This is usefull for multithreaded apps."
  []
  (Nanomsg/terminate))

(defn start-device
  "Given two sockets, start a device."
  [^Socket s1, ^Socket s2]
  (let [d (Device. s1 s2)]
    (.run d)))

(defn- resolve-symbols
  []
  (into {} (for [[k v] (Nanomsg/nn_symbols)]
             [(keyword (.toLowerCase k)) v])))

(def ^{:doc "Get all symbols"}
  symbols (memoize resolve-symbols))
