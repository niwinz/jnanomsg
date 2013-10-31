Getting started using clojure
=============================

This part of the documentation covers the introduction to jnanomsg and basic usage of it (using examples).

Import namespace
----------------

All available functions for work with nanomsag with clojure are available on `nanomsg` namespace.

This is a recommended way to import nanomsg ns on repl:

.. code-block:: clojure

    (require [nanomsg :as nn])

Or on ns macro:

.. code-block:: clojure

    (ns yours.samplens
      (:require [nanomsg :as nn]))

Using Pub/Sub sockets
---------------------

This protocol has two socket types: `:pub` and `:sub`. The first socket type works as published and is used
to broadcast messages to subscribers (the second socket type).

Subscriber sockets always need subscribe to some topic, in other case no messages will be received.

Simple way to declare publisher socket:

.. code-block:: clojure

    (let [sock (nn/socket :pub)]
      (nn/bind sock "ipc:///tmp/sock")
      (dotimes [i 5]
        (nn/send sock "test msg"))
      (nn/close sock))


And this is a simple subscriber client code:

.. code-block:: clojure

    (let [sock (nn/socket :sub)]
      (nn/connect sock "ipc:///tmp/sock")
      (nn/subscribe sock "test")
      (dotimes [i 5]
        (println (nn/recv sock)))
      (nn/close sock))


**Note**: With `:sub` sockets, only messages that the socket is subscribed to are received. When the socket is created there are
no subscriptions and thus no messages will be received.


Using Req/Rep sockets
---------------------

This protocol, like pub/sub has two socket types: `:req` and `:rep`. The first socket type (`:req`) works as client that
sends messages and receives replies. The second socket type (`:rep`) works as stateless server that receives requests
messages and send replies.

Both sockets implements read and write methods.

This is a simple way to declare `:rep` sockets (echo server example):

.. code-block:: clojure

    (let [sock (nn/socket :rep)]
      (nn/bind sock "tcp://*:6789")
      (loop []
        (nn/send sock (nn/recv sock))
        (recur)))


And this is a simple client:

.. code-block:: clojure

    (let [sock (nn/socket :req)]
      (nn/bind sock "tcp://localhost:6789")
      (dotimes [i 5]
        (nn/send sock (str "msg:" 1))
        (println "Received:" (nn/recv sock))))
