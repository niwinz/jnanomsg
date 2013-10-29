package nanomsg.reqrep;

import java.nio.charset.Charset;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.jna.NanoMsg;
import nanomsg.RWSocket;
import nanomsg.Constants;


public class ReqSocket extends RWSocket {
    public ReqSocket(int domain) {
        super(domain, Constants.NN_REQ);
    }

    public ReqSocket() {
        this(Constants.AF_SP);
    }
}
