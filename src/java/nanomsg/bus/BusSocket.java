package nanomsg.bus;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class BusSocket extends RWSocket {
    public BusSocket(int domain) {
        super(domain, Nanomsg.constants.NN_BUS);
    }

    public BusSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
