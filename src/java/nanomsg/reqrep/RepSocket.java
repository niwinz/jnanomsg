package nanomsg.reqrep;

import java.nio.charset.Charset;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.jna.NanoMsg;
import nanomsg.RWSocket;
import nanomsg.Constants;


public class RepSocket extends RWSocket {
    public RepSocket(int domain) {
        super(domain, Constants.NN_REQ);
    }

    public RepSocket() {
        this(Constants.AF_SP);
    }
}
