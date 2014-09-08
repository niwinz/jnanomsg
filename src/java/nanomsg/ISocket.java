package nanomsg;

import nanomsg.exceptions.IOException;

/**
 * Common interface that should implement all sockets.
 */
public interface ISocket {
    public void close() throws IOException;
    public int getNativeSocket();
    public void bind(final String dir) throws IOException;
    public void connect(final String dir) throws IOException;
    public void subscribe(final String data) throws IOException;
    public void subscribe(final byte[] data) throws IOException;

    public int sendString(final String data, final boolean blocking) throws IOException, IOException;
    public int sendString(final String data) throws IOException, IOException;
    public int sendBytes(final byte[] data, final boolean blocking) throws IOException, IOException;
    public int sendBytes(final byte[] data) throws IOException, IOException;

    public String recvString(final boolean blocking) throws IOException, IOException;
    public String recvString() throws IOException, IOException;

    public byte[] recvBytes(final boolean blocking) throws IOException, IOException;
    public byte[] recvBytes() throws IOException, IOException;

    public int getFd(final int flag) throws IOException;

    public void setSendTimeout(final int milis);
    public void setRecvTimeout(final int milis);
}
