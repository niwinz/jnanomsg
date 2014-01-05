package nanomsg.pair;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class PairSocket extends RWSocket {
    public PairSocket(int domain) {
        super(domain, Nanomsg.constants.NN_PAIR);
    }

    public PairSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
