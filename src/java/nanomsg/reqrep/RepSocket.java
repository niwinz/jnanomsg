package nanomsg.reqrep;

import nanomsg.Socket;
import nanomsg.Nanomsg;


public class RepSocket extends Socket {
    public RepSocket(int domain) {
        super(domain, Nanomsg.constants.NN_REP);
    }

    public RepSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
