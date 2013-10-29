package nanomsg;

import java.util.HashMap;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import nanomsg.jna.NanoMsg;


public class Constants {
    public static final int NN_HAUSNUMERO = 156384712;
    public static final int ECONNREFUSED = NN_HAUSNUMERO + 7;
    public static final int EAFNOSUPPORT = NN_HAUSNUMERO + 10;
    public static final int ETERM = NN_HAUSNUMERO + 53;
    public static final int EFSM = NN_HAUSNUMERO + 54;
    public static final int AF_SP = 1;
    public static final int AF_SP_RAW = 2;
    public static final int NN_MSG = -1;

    /* PubSub */
    public static final int NN_PROTO_PUBSUB = 2;
    public static final int NN_PUB = NN_PROTO_PUBSUB * 16 + 0;
    public static final int NN_SUB = NN_PROTO_PUBSUB * 16 + 1;
    public static final int NN_SUB_SUBSCRIBE = 1;
    public static final int NN_SUB_UNSUBSCRIBE = 2;

    /* ReqRep */
    public static final int NN_PROTO_REQREP = 3;
    public static final int NN_REQ = NN_PROTO_REQREP * 16 + 0;
    public static final int NN_REP = NN_PROTO_REQREP * 16 + 1;
    public static final int NN_REQ_RESEND_IVL = 1;

    public static final HashMap<String, Integer> getSymbols() throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        try {
            int index = 0;
            while (true) {
                IntByReference valueRef = new IntByReference();
                Pointer ptr = NanoMsg.nn_symbol(index, valueRef);

                if (ptr.equals(Pointer.NULL)) {
                    break;
                }

                result.put(ptr.getString(0), valueRef.getValue());
                index += 1;
            }

        } catch (NullPointerException e) {
            /* Do nothing because it raided when stops. */
        }

        return result;
    }
}
