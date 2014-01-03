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
        final Charset encoding = Charset.forName("UTF-8");
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
    public synchronized void sendBytes(byte[] data, boolean blocking) {
        final int socket = getSocket();
        final int rc = NanoMsg.nn_send(socket, data, data.length, blocking ? 0 : Constants.NN_DONTWAIT);
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
    public String recvString(boolean blocking) {
        final byte[] received = this.recvBytes(blocking);
        final Charset encoding = Charset.forName("UTF-8");

        return new String(received, encoding);
    }

    /**
     * Receive string from socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public synchronized String recvString() {
        return this.recvString(true);
    }

    /**
     * Receive byte array from socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public synchronized byte[] recvBytes(boolean blocking) {
        final PointerByReference ptrBuff = new PointerByReference();

        final int socket = getSocket();
        final int received = NanoMsg.nn_recv(socket, ptrBuff, Constants.NN_MSG, blocking ? 0: Constants.NN_DONTWAIT);

        if (received < 0) {
            throw new RuntimeException(Constants.getError());
        }

        final Pointer result = ptrBuff.getValue();
        return result.getByteArray(0, received);
    }

    /**
     * Receive byte array from socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public byte[] recvBytes() {
        return this.recvBytes(true);
    }
}
