package nanomsg;

import java.nio.ByteBuffer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;


public class NativeLibrary {
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

  public static native String nn_strerror (int errnum);
  public static native Pointer nn_symbol (int i, IntByReference value);

  static {
    Native.register("nanomsg");
  }
}
