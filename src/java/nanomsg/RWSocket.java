package nanomsg;

import java.nio.charset.Charset;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.NativeLibrary;
import nanomsg.Nanomsg;
import nanomsg.Message;
import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;


public abstract class RWSocket extends Socket {
    public RWSocket(int domain, int protocol) {
        super(domain, protocol);
    }

    /**
     * Send string to socket with option to set blocking flag.
     *
     * @param data string value that represents a message.
     * @param blocking set blocking or non blocking flag.
     * @return number of sended bytes.
     */
    public int sendString(String data, boolean blocking) throws IOException, EAgainException {
        final Charset encoding = Charset.forName("UTF-8");
        return this.sendBytes(data.getBytes(encoding), blocking);
    }

    /**
     * Send string to socket.
     *
     * This operation is blocking by default.
     *
     * @param data string value that represents a message.
     * @return number of sended bytes.
     */
    public int sendString(String data) throws IOException, EAgainException {
        return this.sendString(data, true);
    }

    /**
     * Send bytes array to socket with option to set blocking flag.
     *
     * @param data a bytes array that represents a message.
     * @param blocking set blocking or non blocking flag.
     * @return number of sended bytes.
     */
    public synchronized int sendBytes(byte[] data, boolean blocking) throws IOException, EAgainException {
        final int socket = getSocket();
        final int rc = NativeLibrary.nn_send(socket, data, data.length, blocking ? 0 : Nanomsg.constants.NN_DONTWAIT);
        if (!blocking && rc < 0) {
            final int errno = Nanomsg.getErrorNumber();
            if (errno == Nanomsg.constants.EAGAIN) {
                throw new EAgainException("eagain");
            }
        }

        if (rc < 0) {
            throw new IOException(Nanomsg.getError());
        }

        return rc;
    }

    /**
     * Send bytes array to socket.
     *
     * This operation is blocking by default.
     *
     * @param data a bytes array that represents a message.
     * @return number of sended bytes.
     */
    public int sendBytes(byte[] data) throws IOException, EAgainException {
        return this.sendBytes(data, true);
    }

    /**
     * Receive message from socket as string with option for set blocking flag.
     *
     * This method uses utf-8 encoding for converts a bytes array
     * to string.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as unicode string.
     */
    public String recvString(boolean blocking) throws IOException, EAgainException {
        final byte[] received = this.recvBytes(blocking);
        final Charset encoding = Charset.forName("UTF-8");
        return new String(received, encoding);
    }

    /**
     * Receive message from socket as string.
     *
     * This method uses utf-8 encoding for converts a bytes array
     * to string.
     *
     * @return receved data as unicode string.
     */
    public String recvString() throws IOException, EAgainException {
        return this.recvString(true);
    }

    /**
     * Receive message from socket as bytes array with option for set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return receved data as bytes array
     */
    public synchronized byte[] recvBytes(boolean blocking) throws IOException, EAgainException {
        final PointerByReference ptrBuff = new PointerByReference();

        final int socket = getSocket();
        final int received = NativeLibrary.nn_recv(socket, ptrBuff, Nanomsg.constants.NN_MSG, blocking ? 0: Nanomsg.constants.NN_DONTWAIT);

        // Fast exit on nonblocking sockets and
        // EAGAIN is received.
        if (!blocking && received < 0) {
            final int errno = Nanomsg.getErrorNumber();
            if (errno == Nanomsg.constants.EAGAIN) {
                throw new EAgainException("eagain");
            }
        }

        if (received < 0) {
            throw new IOException(Nanomsg.getError());
        }

        final Pointer result = ptrBuff.getValue();
        final byte[] bytesResult = result.getByteArray(0, received);

        NativeLibrary.nn_freemsg(result);
        return bytesResult;
    }

    /**
     * Receive message from socket as bytes array.
     *
     * This operation is blocking by default.
     *
     * @return receved data as bytes array
     */
    public byte[] recvBytes() throws IOException, EAgainException {
        return this.recvBytes(true);
    }

    /**
     * High level function for send a message.
     *
     * This operation is blocking by default.
     *
     * @return number of sended bytes.
     */
    public int send(Message msg) throws IOException {
        return sendBytes(msg.toBytes());
    }

    /**
     * High level function for send a message with option for set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return number of sended bytes.
     */
    public int send(Message msg, boolean blocking) throws IOException, EAgainException {
        return sendBytes(msg.toBytes(), blocking);
    }

    /**
     * High level function for receive message.
     *
     * This operation is blocking by default.
     *
     * @return Message instance.
     */
    public Message recv() throws IOException {
        return new Message(recvBytes());
    }

    /**
     * High level function for receive message with option for set blocking flag.
     *
     * @param blocking set blocking or non blocking flag.
     * @return Message instance.
     */
    public Message recv(boolean blocking) throws IOException, EAgainException {
        return new Message(recvBytes(blocking));
    }
}
