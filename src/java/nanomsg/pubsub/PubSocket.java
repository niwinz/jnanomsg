package nanomsg.pubsub;

import nanomsg.RWSocket;
import nanomsg.Nanomsg;

public class PubSocket extends RWSocket {
    public PubSocket(int domain) {
        super(domain, Nanomsg.constants.NN_PUB);
    }

    public PubSocket() {
        this(Nanomsg.constants.AF_SP);
    }
}
