User Guide
==========

This part of the documentation covers the introduction to jnanomsg and basic usage of it (using examples).

Protocols
---------

jnanomsg intends to support all available protocols from nanomsg but at the moment only supports a few ones.

PubSub
""""""

- `:pub` - this socket type is used to distribute messages to multiple destinations. Receive operation is not defined.
- `:sub` - this socket typee is used to receives messages from the publisher. Send operation is not defined on this socket.

Note: With `:sub` sockets, only messages that the socket is subscribed to are received. When the socket is created there are
no subscriptions and thus no messages will be received.


ReqRep
""""""

- `:req` - this socket type is used to implement the client application that sends requests and receives replies.
- `:rep` - this socket type is used to implement the stateless worker that receives requests and sends replies.


**Note**: you can see more description of each protocol of main page of this documentation or going
directly to nanomsg page.


.. |br| raw:: html

   <br />
