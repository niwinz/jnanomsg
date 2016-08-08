package nanomsg.pubsub;

import com.sun.jna.Memory;
import nanomsg.Nanomsg;
import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketOption;
import nanomsg.Nanomsg.SocketType;
import nanomsg.NativeLibrary;
import nanomsg.AbstractSocket;
import nanomsg.exceptions.SocketException;


public class SubSocket extends AbstractSocket {
  public SubSocket(Domain domain) {
    super(domain, Nanomsg.SocketType.NN_SUB);
  }

  public SubSocket() {
    this(Domain.AF_SP);
  }

  @Override
  public void subscribe(final byte[] topic) throws SocketException {
    this.setSocketOpt(SocketOption.NN_SUB_SUBSCRIBE, topic);
  }

  @Override
  public void unsubscribe(final byte[] topic) throws SocketException {
    this.setSocketOpt(SocketOption.NN_SUB_UNSUBSCRIBE, topic);
  }
}
