package nanomsg.pubsub;

import java.nio.charset.Charset;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.jna.NanoMsg;
import nanomsg.Socket;
import nanomsg.Constants;


public class SubSocket extends Socket {
    public SubSocket(int domain) {
        super(domain, Constants.NN_SUB);
    }

    public SubSocket() {
        this(Constants.AF_SP);
    }

    public void subscribe(String pattern) {
        NanoMsg.nn_setsockopt(this.socket, Constants.NN_SUB, Constants.NN_SUB_SUBSCRIBE, pattern, pattern.length());
    }

    public String recvString() throws RuntimeException {
        byte[] received = this.recvBytes();
        Charset encoding = Charset.forName("UTF-8");

        return new String(received, encoding);
    }

    public byte[] recvBytes() throws RuntimeException {
        Pointer buff = Pointer.NULL;
        PointerByReference ptrBuff = new PointerByReference(buff);

        int received = NanoMsg.nn_recv(this.socket, ptrBuff, Constants.NN_MSG, 0);
        if (received < 0) {
            throw new RuntimeException("error on rcv");
        }

        Pointer result = ptrBuff.getValue();
        return result.getByteArray(0, received);
    }
}
