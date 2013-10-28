package nanomsg.pubsub;

import java.nio.charset.Charset;

import nanomsg.jna.NanoMsg;
import nanomsg.Socket;
import nanomsg.Constants;

public class PubSocket extends Socket {
    public PubSocket(int domain) {
        super(domain, Constants.NN_PUB);
    }

    public PubSocket() {
        this(Constants.AF_SP);
    }

    public void sendString(String data) {
        Charset encoding = Charset.forName("UTF-8");
        this.sendBytes(data.getBytes(encoding));
    }

    public void sendBytes(byte[] data) {
        int rc = NanoMsg.nn_send(this.socket, data, data.length, 0);
        if (rc < 0) {
            System.out.println("Error");
        }
    }
}
