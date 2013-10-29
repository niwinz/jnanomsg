package nanomsg.pubsub;

import java.nio.charset.Charset;

import nanomsg.jna.NanoMsg;
import nanomsg.RWSocket;
import nanomsg.Constants;

public class PubSocket extends RWSocket {
    public PubSocket(int domain) {
        super(domain, Constants.NN_PUB);
    }

    public PubSocket() {
        this(Constants.AF_SP);
    }
}
