package nanomsg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;


public class NativeLibrary {
  public static class NNPollEvent extends Structure {
    public int fd;
    public short events;
    public short revents;

    protected List getFieldOrder() {
      return Arrays.asList("fd", "events", "revents");
    }

    public NNPollEvent() {
      super(Structure.ALIGN_NONE);
    }

    public NNPollEvent(Pointer p) {
      super(p, Structure.ALIGN_NONE);
      setAlignType(Structure.ALIGN_NONE);
      read();
    }

    public NNPollEvent(int fd, short events, short revents) {
      super(Structure.ALIGN_NONE);
      this.fd = fd;
      this.events = events;
      this.revents = revents;
    }

    public void reuse(Pointer p, int offset) {
      useMemory(p, offset);
      read();
    }

    public static class ByReference
      extends NNPollEvent
      implements Structure.ByReference {

      public ByReference() {
        super();
      }

      public ByReference(int fd, short events, short revents) {
        super();
        this.fd = fd;
        this.events = events;
        this.revents = revents;
      }
    }

    public static class ByValue
      extends NNPollEvent
      implements Structure.ByValue {}
  }

  public static native int nn_socket (int domain, int protocol);
  public static native int nn_close (int s);
  public static native int nn_bind (int s, String addr);
  public static native int nn_connect (int s, String addr);
  public static native int nn_send (int s, byte[] buff, int len, int flags);
  public static native int nn_send (int s, ByteBuffer buff, int len, int flags);
  public static native int nn_recv (int s, PointerByReference buff, int len, int flags);
  public static native int nn_setsockopt (int s, int level, int option, Pointer optval, int optvallen);
  public static native int nn_getsockopt (int s, int level, int option, Pointer optval, Pointer optvallen);
  public static native int nn_freemsg (Pointer msg);
  public static native int nn_errno ();
  public static native int nn_device (int s1, int s2);
  public static native void nn_term();

  // int nn_poll (struct nn_pollfd *fds, int nfds, int timeout);
  public static native int nn_poll(Pointer fds, int maxevents, int timeout);

  public static native String nn_strerror (int errnum);
  public static native Pointer nn_symbol (int i, IntByReference value);

  static {
    Native.register("nanomsg");
  }
}
