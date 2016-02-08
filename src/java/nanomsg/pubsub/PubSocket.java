package nanomsg.pubsub;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.AbstractSocket;

public class PubSocket extends AbstractSocket {
  public PubSocket(Domain domain) {
    super(domain, SocketType.NN_PUB);
  }

  public PubSocket() {
    this(Domain.AF_SP);
  }
}
