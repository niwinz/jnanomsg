package nanomsg.pipeline;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class PushSocket extends RWSocket {
    public PushSocket(int domain) {
        super(domain, Nanomsg.NN_PUSH);
    }

    public PushSocket() {
        this(Nanomsg.AF_SP);
    }
}
