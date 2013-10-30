package nanomsg.reqrep;

import nanomsg.RWSocket;
import nanomsg.Constants;


public class RepSocket extends RWSocket {
    public RepSocket(int domain) {
        super(domain, Constants.NN_REP);
    }

    public RepSocket() {
        this(Constants.AF_SP);
    }
}
