package nanomsg.reqrep;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class ReqSocket extends RWSocket {
    public ReqSocket(int domain) {
        super(domain, Nanomsg.constants.NN_REQ);
    }

    public ReqSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
