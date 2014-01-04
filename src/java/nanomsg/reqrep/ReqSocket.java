package nanomsg.reqrep;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class ReqSocket extends RWSocket {
    public ReqSocket(int domain) {
        super(domain, Nanomsg.NN_REQ);
    }

    public ReqSocket() {
        this(Nanomsg.AF_SP);
    }
}
