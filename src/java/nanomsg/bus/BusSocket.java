package nanomsg.reqrep;

import nanomsg.RWSocket;
import nanomsg.Constants;


public class BusSocket extends RWSocket {
    public BusSocket(int domain) {
        super(domain, Constants.NN_BUS);
    }

    public BusSocket() {
        this(Constants.AF_SP);
    }
}
