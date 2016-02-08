package nanomsg.pubsub;

import com.sun.jna.Memory;
import nanomsg.Nanomsg;
import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketOption;
import nanomsg.Nanomsg.SocketType;
import nanomsg.NativeLibrary;
import nanomsg.AbstractSocket;
import nanomsg.exceptions.IOException;

import java.io.UnsupportedEncodingException;


public class SubSocket extends AbstractSocket {
  public SubSocket(Domain domain) {
    super(domain, Nanomsg.SocketType.NN_SUB);
  }

  public SubSocket() {
    this(Domain.AF_SP);
  }

  @Override
  public void subscribe(final byte[] topic) {
    final int socket = getNativeSocket();

    final Memory mem = new Memory(topic.length);
    mem.write(0, topic, 0, topic.length);

    NativeLibrary.nn_setsockopt(socket, SocketType.NN_SUB.value(), SocketOption.NN_SUB_SUBSCRIBE.value(), mem, topic.length);
  }

  @Override
  public void unsubscribe(byte[] topic) throws IOException {
    final int socket = getNativeSocket();

    final Memory mem = new Memory(topic.length);
    mem.write(0, topic, 0, topic.length);

    NativeLibrary.nn_setsockopt(socket, SocketType.NN_SUB.value(), SocketOption.NN_SUB_UNSUBSCRIBE.value(), mem, topic.length);
  }
}
