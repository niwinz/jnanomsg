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

    public int sendString(final String data, final boolean blocking) throws IOException, EAgainException;
    public int sendBytes(final byte[] data, final boolean blocking) throws IOException, EAgainException;
    public String recvString(final boolean blocking) throws IOException, EAgainException;
    public byte[] recvBytes(final boolean blocking) throws IOException, EAgainException;
    public void subscribe(final String data) throws IOException;
    public int getFd(final int flag) throws IOException;
}
