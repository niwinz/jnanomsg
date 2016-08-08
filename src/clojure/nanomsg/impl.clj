(ns nanomsg.impl
  "Implementation details of nanomsg sockets."
  (:require [nanomsg.proto :as p])
  (:import nanomsg.Socket
           nanomsg.Nanomsg
           nanomsg.Device
           nanomsg.Poller
           nanomsg.AsyncSocket
           nanomsg.Nanomsg$SocketType
           nanomsg.Nanomsg$SocketFlag
           java.util.EnumSet
           java.nio.ByteBuffer
           clojure.lang.Keyword
           clojure.lang.IFn))

(def  +poll-flags-map+
  {:poll-in  Poller/POLLIN
   :poll-out Poller/POLLOUT})

(def +socket-flags-map+
  {:dont-wait nanomsg.Nanomsg$SocketFlag/NN_DONTWAIT})

(def +socket-types-map+
  {:pub Nanomsg$SocketType/NN_PUB
   :sub Nanomsg$SocketType/NN_SUB
   :req Nanomsg$SocketType/NN_REQ
   :rep Nanomsg$SocketType/NN_REP
   :bus Nanomsg$SocketType/NN_BUS
   :pair Nanomsg$SocketType/NN_PAIR
   :push Nanomsg$SocketType/NN_PUSH
   :pull Nanomsg$SocketType/NN_PULL})

(defn- interpret-socket-flags
  [flags]
  (cond
    (map? flags)
    (let [candidates (->> (into [] flags)
                          (filter (comp boolean second))
                          (map first))]
      (interpret-socket-flags candidates))

    (instance? Boolean flags)
    (if (false? flags)
      (interpret-socket-flags #{:dont-wait})
      (interpret-socket-flags #{}))

    (empty? flags)
    (EnumSet/noneOf Nanomsg$SocketFlag)

    :else
    (EnumSet/copyOf (keep +socket-flags-map+ flags))))

(defn- interpret-poller-flags
  [flags]
  (cond
    (map? flags)
    (let [candidates (->> (into [] flags)
                          (filter (comp boolean second))
                          (map first))]
      (interpret-poller-flags candidates))

    (empty? flags)
    (EnumSet/of (class Nanomsg$SocketFlag))

    :else
    (EnumSet/copyOf (keep +poll-flags-map+ flags))))

;; (defn- create-callback
;;   [opt]
;;   (cond
;;     (instance? clojure.lang.IFn opt)
;;     (reify IAsyncCallback
;;       (success [_ result]
;;         (opt result nil))
;;       (fail [_ throwable]
;;         (opt nil throwable)))

;;     (nil? opt)
;;     (reify IAsyncCallback
;;       (success [_ result])
;;       (fail [_ throwable]))))

;; (defn async-socket
;;   [^Socket socket]
;;   (let [^AsyncSocket asocket (AsyncSocket. socket)]
;;     (reify
;;       p/IAsyncSocket
;;       p/ISocket
;;       (bind [_ endpoint]
;;         (.bind socket endpoint))
;;       (connect [_ endpoint]
;;         (.connect socket endpoint))
;;       (subscribe [_ topic]
;;         (.subscribe socket topic))
;;       (unsubscribe [_ topic]
;;         (.unsubscribe socket topic))
;;       (send [_ data opt]
;;         (let [cb (create-callback opt)
;;               data (p/get-byte-buffer data)]
;;           (.send asocket data cb)))
;;       (recv [_ opt]
;;         (let [cb (create-callback opt)]
;;           (.recv asocket cb)))

;;       java.io.Closeable
;;       (close [_]
;;         (.close socket)))))

(extend-type Socket
  p/ISocket
  (-bind [socket endpoint]
    (.bind socket endpoint))
  (-connect [socket endpoint]
    (.connect socket endpoint))
  (-shutdown [socket endpoint]
    (.shutdown socket endpoint))
  (-subscribe [socket topic]
    (.subscribe socket topic))
  (-unsubscribe [socket topic]
    (.unsubscribe socket topic))
  (-recv [socket flags]
    (let [flags (interpret-socket-flags flags)]
      (.recv socket flags)))
  (-send [socket data flags]
    (let [data (p/-byte-buffer data)
          flags (interpret-socket-flags flags)]
      (.send socket data flags))))

(extend-type Poller
  p/IPoller
  (-register [p socket flags]
    (let [flags (interpret-poller-flags flags)]
      (.register p socket flags)))
  (-unregister [p socket]
    (.unregister p socket))
  (-poll [p ms]
    (.poll p ms))
  (-readable? [p socket]
    (.isReadable p socket))
  (-writable? [p socket]
    (.isWritable p socket)))

(extend-protocol p/ISocketData
  (Class/forName "[B")
  (-byte-buffer [b]
    (ByteBuffer/wrap b))

  java.nio.ByteBuffer
  (-byte-buffer [b] b)

  java.lang.String
  (-byte-buffer [s]
    (p/-byte-buffer (.getBytes s "UTF-8"))))
