package nanomsg;

import com.sun.jna.Native;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.util.HashMap;
import java.util.Map;

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

  public static final Map<String, Integer> nn_symbols = Nanomsg.getSymbols();

  public enum Domain {
    AF_SP,
    AF_SP_RAW;

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
    NN_REQ_RESEND_IVL,
    NN_SUB_SUBSCRIBE,
    NN_SNDTIMEO,
    NN_SNDFD,
    NN_RCVFD,
    NN_RCVTIMEO;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  public enum MethodOption {
    NN_SOL_SOCKET,
    NN_MSG,
    NN_DONTWAIT;

    public Integer value() {
      return name().equals("NN_MSG") ? -1 : nn_symbols.get(name());
    }
  }

  public enum Error {
    EAFNOSUPPORT,
    ETERM,
    EFSM,
    EAGAIN,
    ECONNREFUSED;

    public Integer value() {
      return nn_symbols.get(name());
    }
  }

  // /**
  //  * Set memory.
  //  * @param ptr pointer.
  //  * @param value timeout.
  //  */
  // public static void setSizeT(Memory ptr, long value) {
  //   if (Native.SIZE_T_SIZE == 8)
  //     ptr.setLong(0, value);
  //   else
  //     ptr.setInt(0, (int)value);
  // }

  // /**
  //  * Pointer with allocated memory.
  //  * @param value value.
  //  * @return Memory pointer.
  //  */
  // public static Memory newSizeT(long value) {
  //   Memory ptr = new Memory(Native.SIZE_T_SIZE);
  //   setSizeT(ptr, value);
  //   return ptr;
  // }
}
