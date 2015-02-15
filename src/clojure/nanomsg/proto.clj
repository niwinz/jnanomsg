(ns nanomsg.proto
  "Protocol definitions for nanomsg sockets."
  (:refer-clojure :exclude [send]))

(defprotocol ISocket
  "Common socket protocol."
  (bind [_ dir])
  (connect [_ dir])
  (subscribe [_ pattern])
  (unsubscribe [_ pattern])
  (send [_ data opt])
  (recv [_ opt]))

(defprotocol IBlockingSocket
  "Mark for blocking socket.")

(defprotocol IAsyncSocket
  "Mark for async socket.")

(defprotocol ISocketData
  "Common interface for data that can be
  sended through nanomsg socket."
  (get-byte-buffer [_] "Get a byte array representation."))
