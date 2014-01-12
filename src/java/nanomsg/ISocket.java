package nanomsg;

import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;

/**
 * Common interface that should implement all sockets.
 */
public interface ISocket {
    public void close();
    public int getNativeSocket();
    public void bind(final String dir) throws IOException;
    public void connect(final String dir) throws IOException;

    public int sendString(String data, boolean blocking) throws IOException, EAgainException;
    public int sendBytes(byte[] data, boolean blocking) throws IOException, EAgainException;
    public String recvString(boolean blocking) throws IOException, EAgainException;
    public byte[] recvBytes(boolean blocking) throws IOException, EAgainException;
    public void subscribe(final String data) throws IOException;
}
