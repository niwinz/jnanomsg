package nanomsg.pubsub;

import nanomsg.jna.NanoMsg;
import nanomsg.RWSocket;
import nanomsg.Constants;


public class SubSocket extends RWSocket {
    public SubSocket(int domain) {
        super(domain, Constants.NN_SUB);
    }

    public SubSocket() {
        this(Constants.AF_SP);
    }

    public void subscribe(String pattern) {
        final int socket = getSocket();
        NanoMsg.nn_setsockopt(socket, Constants.NN_SUB, Constants.NN_SUB_SUBSCRIBE, pattern, pattern.length());
    }
}
