package nanomsg.reqrep;

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
