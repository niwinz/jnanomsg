package nanomsg;

import java.nio.charset.Charset;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import nanomsg.jna.NanoMsg;


public abstract class RWSocket extends Socket {
    public RWSocket(int domain, int protocol) {
        super(domain, protocol);
    }

    /**
     * Send string to socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public void sendString(String data, boolean blocking) {
        Charset encoding = Charset.forName("UTF-8");
        this.sendBytes(data.getBytes(encoding), blocking);
    }

    /**
     * Send string to socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public void sendString(String data) {
        this.sendString(data, true);
    }

    /**
     * Send byte array to socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public void sendBytes(byte[] data, boolean blocking) {
        int rc = NanoMsg.nn_send(this.socket, data, data.length, blocking ? 0 : Constants.NN_DONTWAIT);
        if (rc < 0) {
            System.out.println("Error");
        }
    }

    /**
     * Send byte array to socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public void sendBytes(byte[] data) {
        this.sendBytes(data, true);
    }

    /**
     * Receive string from socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public String recvString(boolean blocking) throws RuntimeException {
        byte[] received = this.recvBytes(blocking);
        Charset encoding = Charset.forName("UTF-8");

        return new String(received, encoding);
    }

    /**
     * Receive string from socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public String recvString() throws RuntimeException {
        return this.recvString(true);
    }

    /**
     * Receive byte array from socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public byte[] recvBytes(boolean blocking) throws RuntimeException {
        Pointer buff = Pointer.NULL;
        PointerByReference ptrBuff = new PointerByReference(buff);

        int received = NanoMsg.nn_recv(this.socket, ptrBuff, Constants.NN_MSG, blocking ? 0: Constants.NN_DONTWAIT);
        if (received < 0) {
            throw new RuntimeException("error on rcv");
        }

        Pointer result = ptrBuff.getValue();
        return result.getByteArray(0, received);
    }

    /**
     * Receive byte array from socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public byte[] recvBytes() throws RuntimeException {
        return this.recvBytes(true);
    }
}
