package nanomsg.pubsub;

import com.sun.jna.Memory;
import nanomsg.Nanomsg;
import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketOption;
import nanomsg.Nanomsg.SocketType;
import nanomsg.NativeLibrary;
import nanomsg.Socket;
import nanomsg.exceptions.IOException;

import java.io.UnsupportedEncodingException;


public class SubSocket extends Socket implements ISubscriptionSocket {
  public SubSocket(Domain domain) {
    super(domain, Nanomsg.SocketType.NN_SUB);
  }

  public SubSocket() {
    this(Domain.AF_SP);
  }

  @Override
  public void subscribe(final String topic) throws IOException {
    try {
      if (topic.isEmpty())
      {
          subscribe(new byte[1], 0);
      }
      else 
      {
        subscribe(topic.getBytes("utf-8"));
      }
    } catch (UnsupportedEncodingException e) {
      throw new IOException(e);
    }
  }

  private void subscribe(final byte[] patternBytes, int length)
  {
    final int socket = getNativeSocket();
          
    final Memory mem = new Memory(patternBytes.length);
    mem.write(0, patternBytes, 0, patternBytes.length);
          
    NativeLibrary.nn_setsockopt(socket, SocketType.NN_SUB.value(), SocketOption.NN_SUB_SUBSCRIBE.value(),
                                      mem, length);
          // NativeLibrary.nn_setsockopt(socket, NN_SUB.value(), NN_SUB_SUBSCRIBE.value(),
          // null, 0);
  }
    
  @Override
  public void subscribe(final byte[] patternBytes) throws IOException {
    if(patternBytes.length <1)
        subscribe(new byte[1], 0);
    subscribe(patternBytes, patternBytes.length);
  }

  @Override
  public void unsubscribe(final String topic) throws IOException {
    try {
      unsubscribe(topic.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      throw new IOException(e);
    }
  }

  @Override public void unsubscribe(byte[] patternBytes) throws IOException {
    final int socket = getNativeSocket();

    final Memory mem = new Memory(patternBytes.length);
    mem.write(0, patternBytes, 0, patternBytes.length);

    NativeLibrary.nn_setsockopt(socket, SocketType.NN_SUB.value(), SocketOption.NN_SUB_UNSUBSCRIBE.value(),
                                mem, patternBytes.length);
  }
}
