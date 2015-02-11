package nanomsg.pubsub;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import nanomsg.Nanomsg;
import nanomsg.NativeLibrary;
import nanomsg.Socket;
import nanomsg.exceptions.IOException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class SubSocket extends Socket implements ISubscriptionSocket {
  public SubSocket(int domain) {
    super(domain, Nanomsg.constants.NN_SUB);
  }

  public SubSocket() {
    this(Nanomsg.constants.AF_SP);
  }

  @Override
  public void subscribe(final String topic) throws IOException {
    try {
      subscribe(topic.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void subscribe(final byte[] patternBytes) throws IOException {
    final int socket = getNativeSocket();

    final ByteBuffer directBuffer = ByteBuffer.allocateDirect(patternBytes.length);
    directBuffer.put(patternBytes);
    Pointer pointer = Native.getDirectBufferPointer(directBuffer);

    NativeLibrary.nn_setsockopt(socket, Nanomsg.constants.NN_SUB, Nanomsg.constants.NN_SUB_SUBSCRIBE,
                                pointer, patternBytes.length);
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

    NativeLibrary.nn_setsockopt(socket, Nanomsg.constants.NN_SUB, Nanomsg.constants.NN_SUB_UNSUBSCRIBE,
                                mem, patternBytes.length);
  }
}
