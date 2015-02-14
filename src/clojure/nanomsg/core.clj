(ns nanomsg.core
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
  (:require [nanomsg.proto :as proto]
            [nanomsg.impl :as impl])
  (:import nanomsg.Socket
           nanomsg.async.AsyncSocket
           nanomsg.Nanomsg
           nanomsg.Device
           clojure.lang.Keyword))

(defn bind!
  "Bind given socket to specified endpoint."
  [socket ^String endpoint]
  (proto/bind socket endpoint))

(defn connect!
  "Connect given socket to specified endpoint."
  [socket ^String endpoint]
  (proto/connect socket endpoint))

(defn subscribe!
  "Subscribe given socket to specified topic."
  [socket topic]
  (proto/subscribe socket topic))

(defn unsubscribe!
  "Unsubscribe given socket from specified topic."
  [socket topic]
  (proto/unsubscribe socket topic))

(defn send!
  "Send data through given socket."
  ([socket data]
   (send! socket data nil))
  ([socket data opt]
   (proto/send socket data opt)))

(defn recv!
  "Receive data through given socket."
  ([socket]
   (recv! socket nil))
  ([socket opt]
   (proto/recv socket opt)))

(defn recv-str!
  "Receive data through given socket."
  ([socket]
   (recv-str! socket nil))
  ([socket opt]
   (let [received (proto/recv socket opt)]
     (String. received "UTF-8"))))

(defn close!
  "Close socket."
  [^java.io.Closeable socket]
  (.close socket))

(defn socket
  "Geiven a socket type, create a new instance
  of corresponding socket."
  ([^Keyword socktype] (socket socktype {}))
  ([^Keyword socktype opts]
   {:pre [(contains? impl/*supported-sockets* socktype)]}
   (let [factory (get impl/*supported-sockets* socktype)
         socket (factory)
         conn (if (:async opts)
                (impl/async-socket socket (AsyncSocket. socket))
                (impl/blocking-socket socket))]
     (cond
       (:bind opts) (bind! conn (:bind opts))
       (:connect opts) (connect! conn (:connect opts)))
     conn)))

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
  (into {} (for [[k v] (Nanomsg/symbols)]
             [(keyword (.toLowerCase k)) v])))

(def ^{:doc "Get all symbols"}
  symbols (memoize resolve-symbols))
