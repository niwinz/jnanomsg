package nanomsg.pipeline;

import nanomsg.RWSocket;
import nanomsg.Constants;


public class PullSocket extends RWSocket {
    public PullSocket(int domain) {
        super(domain, Constants.NN_PULL);
    }

    public PullSocket() {
        this(Constants.AF_SP);
    }
}
