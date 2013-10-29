package nanomsg.reqrep;

import nanomsg.RWSocket;
import nanomsg.Constants;


public class PairSocket extends RWSocket {
    public PairSocket(int domain) {
        super(domain, Constants.NN_PAIR);
    }

    public PairSocket() {
        this(Constants.AF_SP);
    }
}
