package nanomsg;

import nanomsg.exceptions.IOException;

/**
 * Common interface that should implement all sockets.
 */
public interface ISocket {
  public int getNativeSocket();

  public void close() throws IOException;
  public void bind(final String dir) throws IOException;
  public void connect(final String dir) throws IOException;

  public int send(final String data, final boolean blocking) throws IOException;
  public int send(final String data) throws IOException;
  public int send(final byte[] data, final boolean blocking) throws IOException;
  public int send(final byte[] data) throws IOException;
  public int send(final IMessage data, final boolean blocking) throws IOException;
  public int send(final IMessage data) throws IOException;

  public String recvString(final boolean blocking) throws IOException;
  public String recvString() throws IOException;

  public byte[] recvBytes(final boolean blocking) throws IOException;
  public byte[] recvBytes() throws IOException;

  public IMessage recv(final boolean blocking) throws IOException;
  public IMessage recv() throws IOException;

  // public int getFd(final int flag) throws IOException;

  public void setSendTimeout(final int milis);
  public void setRecvTimeout(final int milis);
}
