package nanomsg.pipeline;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;


public class PullSocket extends RWSocket {
    public PullSocket(int domain) {
        super(domain, Nanomsg.constants.NN_PULL);
    }

    public PullSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
