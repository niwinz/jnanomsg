Java documentation
==================

Quickstart
----------

Using java:

.. code-block:: java

    import nanomsg.pubsub.PubSocket;
    import nanomsg.pubsub.SubSocket;

    /* Publisher.java */
    PubSocket sock = new PubSocket();
    sock.bind("ipc:///tmp/sock");
    sock.send("msg: hello");
    sock.close();

    /* Subscriber.java */
    SubSocket sock = new SubSocket();
    sock.connect("ipc:///tmp/sock");
    sock.subscibe("msg");

    String receivedData = sock.recv();
    System.out.println(receivedData);

