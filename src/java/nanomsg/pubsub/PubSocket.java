package nanomsg.pubsub;

import nanomsg.ffi.NanoMsgFfi;
import nanomsg.Socket;
import nanomsg.Constants;

public class PubSocket extends Socket {
    public PubSocket(int domain) {
        super(domain, Constants.NN_PUB);
    }

    public PubSocket() {
        this(Constants.AF_SP);
    }

    public void send(String data) {
        byte[] byteData = data.getBytes();
        int length = byteData.length;

        int rc = NanoMsgFfi.nn_send(this.socket, byteData, length, 0);
        if (rc < 0) {
            System.out.println("Error");
        }
    }
}
