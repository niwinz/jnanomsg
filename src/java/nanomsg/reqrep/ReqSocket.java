package nanomsg.reqrep;

import nanomsg.Socket;
import nanomsg.Nanomsg;


public class ReqSocket extends Socket {
    public ReqSocket(int domain) {
        super(domain, Nanomsg.constants.NN_REQ);
    }

    public ReqSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
