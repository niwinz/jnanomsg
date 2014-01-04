package nanomsg;

import java.nio.charset.Charset;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.NativeLibrary;
import nanomsg.Nanomsg;
import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EmptyResponseException;


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
    public int sendString(String data, boolean blocking) throws IOException, EmptyResponseException {
        final Charset encoding = Charset.forName("UTF-8");
        return this.sendBytes(data.getBytes(encoding), blocking);
    }

    /**
     * Send string to socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public int sendString(String data) throws IOException, EmptyResponseException {
        return this.sendString(data, true);
    }

    /**
     * Send byte array to socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public synchronized int sendBytes(byte[] data, boolean blocking) throws IOException, EmptyResponseException {
        final int socket = getSocket();
        final int rc = NativeLibrary.nn_send(socket, data, data.length, blocking ? 0 : Nanomsg.NN_DONTWAIT);
        if (!blocking && rc < 0) {
            final int errno = Nanomsg.getErrorNum();
            if (errno == Nanomsg.EAGAIN) {
                throw new EmptyResponseException("eagain");
            }
        }

        if (rc < 0) {
            throw new IOException(Nanomsg.getError());
        }

        return rc;
    }

    /**
     * Send byte array to socket.
     *
     * This operation is blocking by default.
     *
     * @return receved data as unicode string.
     */
    public int sendBytes(byte[] data) throws IOException, EmptyResponseException {
        return this.sendBytes(data, true);
    }

    /**
     * Receive string from socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public String recvString(boolean blocking) throws IOException, EmptyResponseException {
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
    public String recvString() throws IOException, EmptyResponseException {
        return this.recvString(true);
    }

    /**
     * Receive byte array from socket with option to set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public synchronized byte[] recvBytes(boolean blocking) throws IOException, EmptyResponseException {
        final PointerByReference ptrBuff = new PointerByReference();

        final int socket = getSocket();
        final int received = NativeLibrary.nn_recv(socket, ptrBuff, Nanomsg.NN_MSG, blocking ? 0: Nanomsg.NN_DONTWAIT);

        // Fast exit on nonblocking sockets and
        // EAGAIN is received.
        if (!blocking && received < 0) {
            final int errno = Nanomsg.getErrorNum();
            if (errno == Nanomsg.EAGAIN) {
                throw new EmptyResponseException("eagain");
            }
        }

        if (received < 0) {
            throw new IOException(Nanomsg.getError());
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
    public byte[] recvBytes() throws IOException, EmptyResponseException {
        return this.recvBytes(true);
    }
}
