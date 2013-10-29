package nanomsg.pubsub;

import java.nio.charset.Charset;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.jna.NanoMsg;
import nanomsg.RWSocket;
import nanomsg.Constants;


public class SubSocket extends RWSocket {
    public SubSocket(int domain) {
        super(domain, Constants.NN_SUB);
    }

    public SubSocket() {
        this(Constants.AF_SP);
    }

    public void subscribe(String pattern) {
        NanoMsg.nn_setsockopt(this.socket, Constants.NN_SUB, Constants.NN_SUB_SUBSCRIBE, pattern, pattern.length());
    }
}
