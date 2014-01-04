package nanomsg.pipeline;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class PullSocket extends RWSocket {
    public PullSocket(int domain) {
        super(domain, Nanomsg.NN_PULL);
    }

    public PullSocket() {
        this(Nanomsg.AF_SP);
    }
}
