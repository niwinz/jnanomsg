package nanomsg.pubsub;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.ffi.NanoMsgFfi;
import nanomsg.Socket;
import nanomsg.Constants;


public class SubSocket extends Socket {
    public SubSocket(int domain) {
        super(domain, Constants.NN_SUB);
    }

    public SubSocket() {
        this(Constants.AF_SP);
    }

    public void subscribe(String pattern) {
        NanoMsgFfi.nn_setsockopt(this.socket, Constants.NN_SUB, Constants.NN_SUB_SUBSCRIBE, pattern, pattern.length());
    }

    public String recv() throws RuntimeException {
        Pointer buff = Pointer.NULL;
        PointerByReference ptrBuff = new PointerByReference(buff);

        int rc = NanoMsgFfi.nn_recv(this.socket, ptrBuff, Constants.NN_MSG, 0);
        if (rc < 0) {
            throw new RuntimeException("error on rcv");
        }

        Pointer result = ptrBuff.getValue();
        return result.getString(0);
    }
}
