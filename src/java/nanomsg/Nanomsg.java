package nanomsg;

import com.sun.jna.Native;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.util.HashMap;
import java.util.Map;


public final class Nanomsg {
  public static int NN_MSG = -1;

  /* Low level native interface wrapper */

  public static final int getErrorNumber() {
    return NativeLibrary.nn_errno();
  }

  public static final String getError() {
    final int currentError = Nanomsg.getErrorNumber();
    return NativeLibrary.nn_strerror(currentError);
  }

  public static final void terminate() {
    NativeLibrary.nn_term();
  }

  private static final Map<String, Integer> getSymbols() {
    HashMap<String, Integer> result = new HashMap<String, Integer>();

    int index = 0;
    while (true) {
      IntByReference valueRef = new IntByReference();
      Pointer ptr = NativeLibrary.nn_symbol(index, valueRef);

      if (ptr == null) {
        break;
      }

      result.put(ptr.getString(0), valueRef.getValue());
      index += 1;
    }

    return result;
  }

  public static final Map<String, Integer> nn_symbols = Nanomsg.getSymbols();

  public enum Domain {
    AF_SP,
    AF_SP_RAW;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum PollFlag {
    NN_POLLIN,
    NN_POLLOUT;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum SocketType {

    /* PubSub */
    NN_PUB,
    NN_SUB,

    /* ReqRep */
    NN_REQ,
    NN_REP,

    /* Pipeline */
    NN_PUSH,
    NN_PULL,

    /* Bus */
    NN_BUS,

    /* Pair */
    NN_PAIR;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum SocketOption {
    NN_SUB_UNSUBSCRIBE,
    NN_SUB_SUBSCRIBE,
    NN_SNDFD,
    NN_RCVFD,
    NN_LINGER,
    NN_SNDBUF,
    NN_RCVBUF,
    NN_RCVMAXSIZE,
    NN_SNDTIMEO,
    NN_RCVTIMEO,
    NN_RECONNECT_IVL,
    NN_RECONNECT_IVL_MAX,
    NN_SNDPRIO,
    NN_RCVPRIO,
    NN_IPV4ONLY,
    NN_SOCKET_NAME;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum SocketFlag {
    NN_DONTWAIT;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum OptionLevel {
    NN_SOL_SOCKET,
    NN_TCP;
    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum Error {
    ENOTSUP,
    EPROTONOSUPPORT,
    ENOBUFS,
    ENETDOWN,
    EADDRINUSE,
    EADDRNOTAVAIL,
    ECONNREFUSED,
    EINPROGRESS,
    ENOTSOCK,
    EAFNOSUPPORT,
    EPROTO,
    EAGAIN,
    EBADF,
    EINVAL,
    EMFILE,
    EFAULT,
    EACCES,
    // EACCESS,
    ENETRESET,
    ENETUNREACH,
    EHOSTUNREACH,
    ENOTCONN,
    EMSGSIZE,
    ETIMEDOUT,
    ECONNABORTED,
    ECONNRESET,
    ENOPROTOOPT,
    // EISCONN,
    // ESOCKTNOSUPPORT,
    ETERM,
    EFSM;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }
}
