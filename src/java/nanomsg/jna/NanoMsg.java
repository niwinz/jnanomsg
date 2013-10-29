package nanomsg.jna;

import com.sun.jna.*;
import com.sun.jna.ptr.*;

public class NanoMsg {
    public static native int nn_socket (int domain, int protocol);
    public static native int nn_close (int s);
    public static native int nn_bind (int s, String addr);
    public static native int nn_connect (int s, String addr);
    public static native int nn_send (int s, byte[] buff, int len, int flags);
    public static native int nn_recv (int s, PointerByReference buff, int len, int flags);
    public static native String nn_strerror (int errnum);
    public static native int nn_setsockopt (int s, int level, int option, String optval, int optvallen);
    public static native Pointer nn_symbol(int i, IntByReference value);
    public static native int nn_freemsg (Pointer msg);

    static {
        Native.register("nanomsg");
    }
}
