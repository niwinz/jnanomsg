package nanomsg;

import java.nio.charset.Charset;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import nanomsg.jna.NanoMsg;


public abstract class RWSocket extends Socket {
    public RWSocket(int domain, int protocol) {
        super(domain, protocol);
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
