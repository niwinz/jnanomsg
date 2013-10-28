Clojure documentation
=====================


Quickstart
----------


Using clojure:

.. code-block:: clojure

    (require [nanomsg :as nn])

    ;; Publisher
    (let [sock (nn/socket :pub)]
      (nn/bind sock "ipc:///tmp/sock")
      (nn/send sock (str "msg: hello " i))
      (nn/close sock))

    ;; Subscriber
    (let [sock (nn/socket :sub)]
      (nn/connect sock "ipc:///tmp/sock")
      (println (nn/recv sock))
      (nn/close sock))

