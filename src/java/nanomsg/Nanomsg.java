package nanomsg;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import nanomsg.NativeLibrary;


public final class Nanomsg {

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

  public static final Map<String, Integer> symbols = Nanomsg.getSymbols();

  public static class constants {
    public static final int ECONNREFUSED = Nanomsg.symbols.get("ECONNREFUSED");
    public static final int EAFNOSUPPORT = Nanomsg.symbols.get("EAFNOSUPPORT");
    public static final int ETERM = Nanomsg.symbols.get("ETERM");
    public static final int EFSM = Nanomsg.symbols.get("EFSM");
    public static final int EAGAIN = Nanomsg.symbols.get("EAGAIN");
    public static final int NN_DONTWAIT = Nanomsg.symbols.get("NN_DONTWAIT");
    public static final int AF_SP = Nanomsg.symbols.get("AF_SP");
    public static final int AF_SP_RAW = Nanomsg.symbols.get("AF_SP_RAW");

    public static final int NN_SOL_SOCKET = Nanomsg.symbols.get("NN_SOL_SOCKET");
    public static final int NN_SNDFD = Nanomsg.symbols.get("NN_SNDFD");
    public static final int NN_RCVFD = Nanomsg.symbols.get("NN_RCVFD");
    public static final int NN_SNDTIMEO = Nanomsg.symbols.get("NN_SNDTIMEO");
    public static final int NN_RCVTIMEO = Nanomsg.symbols.get("NN_RCVTIMEO");

    public static final int NN_MSG = -1;

    /* PubSub */
    public static final int NN_PUB = Nanomsg.symbols.get("NN_PUB");
    public static final int NN_SUB = Nanomsg.symbols.get("NN_SUB");
    public static final int NN_SUB_SUBSCRIBE = Nanomsg.symbols.get("NN_SUB_SUBSCRIBE");
    public static final int NN_SUB_UNSUBSCRIBE = Nanomsg.symbols.get("NN_SUB_UNSUBSCRIBE");

    /* ReqRep */
    public static final int NN_REQ = Nanomsg.symbols.get("NN_REQ");
    public static final int NN_REP = Nanomsg.symbols.get("NN_REP");
    public static final int NN_REQ_RESEND_IVL = Nanomsg.symbols.get("NN_REQ_RESEND_IVL");

    /* Pair */
    public static final int NN_PAIR = Nanomsg.symbols.get("NN_PAIR");

    /* Pipeline */
    public static final int NN_PUSH = Nanomsg.symbols.get("NN_PUSH");
    public static final int NN_PULL = Nanomsg.symbols.get("NN_PULL");

    /* Bus */
    public static final int NN_BUS = Nanomsg.symbols.get("NN_BUS");
  }
}
