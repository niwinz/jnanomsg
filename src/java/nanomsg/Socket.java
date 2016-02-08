package nanomsg;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import nanomsg.exceptions.IOException;


public interface Socket {
  void close() throws IOException;
  void bind(final String endpoint) throws IOException;
  void connect(final String endpoint) throws IOException;

  /**
   * Send a message with option to set blocking flag.
   *
   * @param data a `ByteBuffer` that represents a message.
   * @param blocking set blocking or non blocking flag.
   * @return number of sended bytes.
   */
  int send(final ByteBuffer data, final boolean blocking) throws IOException;

  /**
   * Send a message.
   *
   * This operation is blocking by default.
   *
   * @param data byte buffer that represents a message.
   * @return number of sended bytes.
   */
  int send(final ByteBuffer data) throws IOException;

  /**
   * Send a message with option to set blocking flag.
   *
   * @param data string value that represents a message.
   * @param blocking set blocking or non blocking flag.
   * @return number of sended bytes.
   */
  int send(final String data, final boolean blocking) throws IOException;

  /**
   * Send a message.
   *
   * This operation is blocking by default.
   *
   * @param data string value that represents a message.
   * @return number of sended bytes.
   */
  int send(final String data) throws IOException;

  /**
   * Send a message with option to set blocking flag.
   *
   * @param data a bytes array that represents a message.
   * @param blocking set blocking or non blocking flag.
   * @return number of sended bytes.
   */
  int send(final byte[] data, final boolean blocking) throws IOException;

  /**
   * Send a message.
   *
   * This operation is blocking by default.
   *
   * @param data a bytes array that represents a message.
   * @return number of sended bytes.
   */
  int send(final byte[] data) throws IOException;

  /**
   * Receive a message with option for set blocking flag.
   *
   * This method uses utf-8 encoding for converts a bytes array
   * to string.
   *
   * @param blocking set blocking or non blocking flag.
   * @return receved data as unicode string.
   */
  String recvString(boolean blocking) throws IOException;

  /**
   * Receive a message with option for set blocking flag.
   *
   * This method uses utf-8 encoding for converts a bytes array
   * to string. This operation is blocking by default.
   *
   * @return receved data as unicode string.
   */
  String recvString() throws IOException;

  /**
   * Receive a message with option for set blocking flag.
   *
   * @param blocking set blocking or non blocking flag.
   * @return receved data as bytes array
   */
  byte[] recvBytes(boolean blocking) throws IOException;

  /**
   * Receive a message.
   *
   * This operation is blocking by default.
   *
   * @return receved data as bytes array
   */
  byte[] recvBytes() throws IOException;

  /**
   * Receive a message with option for set blocking flag.
   *
   * @param blocking set blocking or non blocking flag.
   * @return Message instance.
   */
  ByteBuffer recv(boolean blocking) throws IOException;

  /**
   * Receive a message.
   *
   * This operation is blocking by default.
   *
   * @return receved data as ByteBuffer
   */
  ByteBuffer recv() throws IOException;

  /**
   * Subscribe to a particular topic.
   *
   * WARNING This method is only supported on SubSocket.
   *
   * @param topic the topic represented as string.
   */
  void subscribe(final String topic)
    throws IOException, UnsupportedEncodingException;

  /**
   * Subscribe to a particular topic.
   *
   * WARNING This method is only supported on SubSocket.
   *
   * @param topic the topic represented as byte array.
   */
  void subscribe(final byte[] topic) throws IOException;

  /**
   * Unsubscribe from a particular topic.
   *
   * WARNING This method is only supported on SubSocket.
   *
   * @param topic the topic represented as String.
   */
  void unsubscribe(final String topic)
    throws IOException, UnsupportedEncodingException;

  /**
   * Unsubscribe from a particular topic.
   *
   * WARNING This method is only supported on SubSocket.
   *
   * @param topic the topic represented as byte array.
   */
  void unsubscribe(final byte[] topic) throws IOException;

  int getNativeSocket();

  /**
   * Get read file descriptor.
   *
   * @return file descriptor.
   */
  int getRcvFd() throws IOException;

  /**
   * Get write file descriptor.
   *
   * @return file descriptor.
   */
  int getSndFd() throws IOException;

  /**
   * Set send timeout option to the socket.
   */
  void setSendTimeout(final int ms);

  /**
   * Set recv timeout option to the socket.
   */
  void setRecvTimeout(final int ms);
}
