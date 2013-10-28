.. jnanomsg documentation master file, created by
   sphinx-quickstart on Mon Oct 28 21:22:46 2013.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

jnanomsg
========

**nanomsg** is a socket library that provides several common communication patterns. It aims to make the networking layer fast,
scalable, and easy to use. Implemented in C, it works on a wide range of operating systems with no further dependencies.

**jnanomsg** is a clojure bindings to nanomsg. It is build using java with JNA libraries (also exposes a very powerful java api)
but it focused mainly on clojure.


Feature Support
---------------

**jnanomsg** at the moment only supports a limited set of protocols available on nanomsg v2.0 (alpha):

* REQREP (:req :rep) - allows to build clusters of stateless services to process user requests
* PUBSUB (:pub :sub) - distributes messages to large sets of interested subscribers

But, the rest are will be available in near future:

* PAIR (:pair) - simple one-to-one communication
* BUS (:bus) - simple many-to-many communication
* PIPELINE (:pipeline) - aggregates messages from multiple sources and load balances them among many destinations


Quickstart
----------

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


Contents
--------

.. toctree::
    :maxdepth: 2

    install.rst
    usage.rst

License
-------

Copyright 2013 Andrey Antukh <niwi@niwi.be>

Licensed under the Apache License, Version 2.0 (the "License")
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
