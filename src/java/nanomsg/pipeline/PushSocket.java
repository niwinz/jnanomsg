package nanomsg.pipeline;

import nanomsg.RWSocket;
import nanomsg.Constants;


public class PushSocket extends RWSocket {
    public PushSocket(int domain) {
        super(domain, Constants.NN_PUSH);
    }

    public PushSocket() {
        this(Constants.AF_SP);
    }
}
