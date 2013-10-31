Getting started with Java
=========================

This part of the documentation covers the introduction to jnanomsg and basic usage of it, using two examples.




Using Pub/Sub sockets
---------------------

This protocol has two socket types: `nanomsg.pubsub.PubSocket` and `nanomsg.pubsub.SubSocket`. The first socket type works as published and is used
to broadcast messages to subscribers (the second socket type).

Subscriber sockets always need subscribe to some topic, in other case no messages will be received.

Simple way to declare publisher socket using java:

.. code-block:: java

    import nanomsg.pubsub.PubSocket;

    public class Publisher {
        public static void main(String[] args) {
            PubSocket sock = new PubSocket();
            sock.bind("ipc:///tmp/sock");

            for(int i=0; i<5; i++) {
                sock.sendString("test msg");
            }

            sock.close()
        }
    }


And this is a example of subscriber client:

.. code-block:: java

    import nanomsg.pubsub.SubSocket;

    public class Subscriber {
        public static void main(String[] args) {
            SubSocket sock = new SubSocket();
            sock.connect("ipc:///tmp/sock");
            sock.subscribe("test");

            for(int i=0; i<5; i++) {
                System.out.println(sock.recvString());
            }

            sock.close()
        }
    }


**Note**: With `nanomsg.pubsub.SubSocket` sockets, only messages that the socket is subscribed to are received. When the socket is created there are
no subscriptions and thus no messages will be received.


Using Req/Rep sockets
---------------------

This protocol, like pub/sub has two socket types: `nanomsg.reqrep.ReqSocket` and `nanomsg.reqrep.RepSocket`. The first socket type (`:req`) works as client that
sends messages and receives replies. The second socket type (`:rep`) works as stateless server that receives requests
messages and send replies.

Both sockets implements read and write methods.

This is a simple way to declare `nanomsg.reqrep.RepSocket` sockets (echo server example):

.. code-block:: java

    import nanomsg.reqrep.RepSocket;

    public class EchoServer {
        public static void main(String[] args) {
            RepSocket sock = new RepSocket();
            sock.bind("tcp://*:6789");

            while (true) {
                byte[] receivedData = sock.recvBytes();
                sock.sendBytes(receivedData);
            }

            sock.close()
        }
    }


And this is a simple client:

.. code-block:: java

    import nanomsg.reqrep.ReqSocket;

    public class EchoClient {
        public static void main(String[] args) {
            ReqSocket sock = new ReqSocket();
            sock.connect("tcp://localhost:6789");

            for (int i=0; i<5; i++) {
                sock.sendString("Hello!" + 1);
                System.out.println("Received:" + sock.recvString());
            }

            sock.close()
        }
    }


Api documentation
-----------------

The java library exposes many clasess distributed in packagas for implement all supported socket types. You can
see javadoc_ for obtain a more detail about which packages/classes are available.

.. _javadoc: https://github.com/niwibe/jnanomsg
