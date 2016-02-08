package nanomsg;

import java.lang.UnsupportedOperationException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.exceptions.IOException;
import static nanomsg.Nanomsg.*;

public abstract class AbstractSocket implements Socket {
  protected final int socket;
  protected boolean closed = false;
  protected boolean opened = false;

  public AbstractSocket(final Domain domain, final SocketType protocol) {
    this.socket = NativeLibrary.nn_socket(domain.value(), protocol.value());
    this.opened = true;

    this.setSendTimeout(600);
    this.setRecvTimeout(600);
  }

  public synchronized void close() throws IOException {
    if (this.opened && !this.closed) {
      this.closed = true;
      final int rc = NativeLibrary.nn_close(this.socket);

      if (rc < 0) {
        final int errno = getErrorNumber();
        final String msg = getError();
        throw new IOException(msg, errno);
      }
    }
  }

  public int getNativeSocket() {
    return this.socket;
  }

  public synchronized void bind(final String dir) throws IOException {
    final int rc = NativeLibrary.nn_bind(this.socket, dir);

    if (rc < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }
  }

  public synchronized void connect(final String dir) throws IOException {
    final int rc = NativeLibrary.nn_connect(this.socket, dir);

    if (rc < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }
  }

  public int send(final String data, final boolean blocking) throws IOException {
    final Charset encoding = Charset.forName("UTF-8");
    return this.send(data.getBytes(encoding), blocking);
  }

  public int send(final String data) throws IOException {
    return this.send(data, true);
  }

  public synchronized int send(final byte[] data, final boolean blocking)
    throws IOException {

    final int socket = getNativeSocket();
    final int flags = blocking ? 0 : MethodOption.NN_DONTWAIT.value();
    final int rc = NativeLibrary.nn_send(socket, data, data.length, flags);

    if (rc < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }

    return rc;
  }

  public int send(final byte[] data) throws IOException {
    return this.send(data, true);
  }

  public String recvString(final boolean blocking) throws IOException {
    final byte[] received = this.recvBytes(blocking);
    final Charset encoding = Charset.forName("UTF-8");
    return new String(received, encoding);
  }

  public String recvString() throws IOException {
    return this.recvString(true);
  }

  public synchronized byte[] recvBytes(boolean blocking) throws IOException {
    final PointerByReference ptrBuff = new PointerByReference();

    final int socket = getNativeSocket();
    final int flags = blocking ? 0: MethodOption.NN_DONTWAIT.value();
    final int received = NativeLibrary.nn_recv(socket, ptrBuff, MethodOption.NN_MSG.value(), flags);

    if (received < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }

    final Pointer result = ptrBuff.getValue();
    final byte[] bytesResult = result.getByteArray(0, received);

    // NativeLibrary.nn_freemsg(result);
    return bytesResult;
  }

  public byte[] recvBytes() throws IOException {
    return this.recvBytes(true);
  }

  public ByteBuffer recv(final boolean blocking) throws IOException {
    final PointerByReference ptrBuff = new PointerByReference();

    final int socket = getNativeSocket();
    final int flags = blocking ? 0: MethodOption.NN_DONTWAIT.value();
    final int received = NativeLibrary.nn_recv(socket, ptrBuff, MethodOption.NN_MSG.value(), flags);

    if (received < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }

    final Pointer result = ptrBuff.getValue();
    final ByteBuffer buffer = result.getByteBuffer(0, received);

    // NativeLibrary.nn_freemsg(result);
    return buffer;
  }

  public ByteBuffer recv() throws IOException {
    return this.recv(true);
  }

  public synchronized int send(final ByteBuffer data, final boolean blocking) throws IOException {
    final int socket = getNativeSocket();
    final int flags = blocking ? 0 : MethodOption.NN_DONTWAIT.value();
    final int rc = NativeLibrary.nn_send(socket, data, data.limit(), flags);

    if (rc < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }

    return rc;
  }

  public synchronized int send(final ByteBuffer data) throws IOException {
    return this.send(data, true);
  }

  public void subscribe(final String topic)
    throws IOException, UnsupportedEncodingException {

    this.subscribe(topic.getBytes("UTF-8"));
  }

  public void subscribe(final byte[] topic) throws IOException {
    throw new UnsupportedOperationException("You can use subscribe on this socket.");
  }

  public void unsubscribe(final String topic)
    throws IOException, UnsupportedEncodingException {
    this.unsubscribe(topic.getBytes("UTF-8"));
  }

  public void unsubscribe(final byte[] topic) throws IOException {
    throw new UnsupportedOperationException("You can use unsubscribe on this socket.");
  }

  public int getRcvFd() throws IOException {
    return getFd(SocketOption.NN_RCVFD);
  }

  public int getSndFd() throws IOException {
    return getFd(SocketOption.NN_SNDFD);
  }

  /**
   * Get file descriptor.
   *
   * @return file descriptor.
   */
  private synchronized int getFd(SocketOption opt) throws IOException {
    assert opt == SocketOption.NN_RCVFD || opt == SocketOption.NN_SNDFD;

    final int flag = opt.value();
    final IntByReference fd = new IntByReference();
    final IntByReference size_t = new IntByReference(Native.SIZE_T_SIZE);

    final int rc = NativeLibrary.nn_getsockopt(this.socket,
                                               MethodOption.NN_SOL_SOCKET.value(),
                                               flag, fd.getPointer(),
                                               size_t.getPointer());
    if (rc < 0) {
      final int errno = getErrorNumber();
      final String msg = getError();
      throw new IOException(msg, errno);
    }

    return fd.getValue();
  }

  public synchronized void setSendTimeout(final int milis) {
    final int socket = getNativeSocket();
    IntByReference timeout = new IntByReference(milis);
    NativeLibrary.nn_setsockopt(socket, MethodOption.NN_SOL_SOCKET.value(),
                                SocketOption.NN_SNDTIMEO.value(), timeout.getPointer() , 4);
  }

  public synchronized void setRecvTimeout(final int milis) {
    final int socket = getNativeSocket();

    IntByReference timeout = new IntByReference(milis);
    NativeLibrary.nn_setsockopt(socket, MethodOption.NN_SOL_SOCKET.value(),
                                SocketOption.NN_RCVTIMEO.value(), timeout.getPointer(), 4);
  }

}
