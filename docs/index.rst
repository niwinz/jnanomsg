.. jnanomsg documentation master file, created by
   sphinx-quickstart on Mon Oct 28 21:22:46 2013.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

jnanomsg
========

nanomsg_ is a socket library that provides several common communication patterns. It aims to make the networking layer fast,
scalable, and easy to use. Implemented in C, it works on a wide range of operating systems with no further dependencies.

**jnanomsg** is a clojure and java bindings to nanomsg native library (using JNA). It exposes api for both languages, but is
mainly focused on clojure.

.. _nanomsg: http://nanomsg.org/


Feature Support
---------------

Transports
""""""""""

jnanomsg supports all transports supported by their backend (nanomsg): ipc, inproc, tcp


Protocols
"""""""""

jnanomsg intends to support all available protocols from nanomsg but at the moment only supports a few ones:

- `:pub` - this socket type is used to distribute messages to multiple destinations. Receive operation is not defined.
- `:sub` - this socket typee is used to receives messages from the publisher. Send operation is not defined on this socket.
- `:req` - this socket type is used to implement the client application that sends requests and receives replies.
- `:rep` - this socket type is used to implement the stateless worker that receives requests and sends replies.
- `:bus` - this socket type is used to send messages to all nodes in the topology.
- `:pair` - this socket type is uded to implement communication with exactly one peer.

**Note**: you can see more description of each protocol of main page of this documentation or going
directly to nanomsg page.


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
      (nn/subscribe sock "test")
      (println (nn/recv sock))
      (nn/close sock))


Contents
--------

.. toctree::
    :maxdepth: 3

    install.rst
    gettings-started-clojure.rst
    gettings-started-java.rst


License
-------

.. code-block:: text

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
