(ns nanomsg.impl
  "Implementation details of nanomsg sockets."
  (:require [nanomsg.proto :as p])
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
           nanomsg.async.AsyncSocket
           nanomsg.async.IAsyncCallback
           java.nio.ByteBuffer
           clojure.lang.Keyword
           clojure.lang.IFn))

(def ^{:dynamic true}
  *supported-sockets*
  {:pub #(PubSocket.)
   :sub #(SubSocket.)
   :req #(ReqSocket.)
   :rep #(RepSocket.)
   :bus #(BusSocket.)
   :pair #(PairSocket.)
   :push #(PushSocket.)
   :pull #(PullSocket.)})

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

(defn blocking-socket
  [^Socket socket]
  (reify
    p/IBlockingSocket
    p/ISocket
    (-bind [_ endpoint]
      (.bind socket endpoint))
    (-connect [_ endpoint]
      (.connect socket endpoint))
    (-subscribe [_ topic]
      (.subscribe socket topic))
    (-unsubscribe [_ topic]
      (.unsubscribe socket topic))
    (-recv [_ blocking]
      (.recv socket blocking))
    (-send [_ data blocking]
      (let [data (p/-byte-buffer data)]
        (.send socket data blocking)))

    java.io.Closeable
    (close [_]
      (.close socket))))

(extend-protocol p/ISocketData
  (Class/forName "[B")
  (-byte-buffer [b]
    (ByteBuffer/wrap b))

  java.nio.ByteBuffer
  (-byte-buffer [b] b)

  java.lang.String
  (-byte-buffer [s]
    (p/-byte-buffer (.getBytes s "UTF-8"))))
