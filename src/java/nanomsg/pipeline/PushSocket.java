package nanomsg.pipeline;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class PushSocket extends RWSocket {
    public PushSocket(int domain) {
        super(domain, Nanomsg.constants.NN_PUSH);
    }

    public PushSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
