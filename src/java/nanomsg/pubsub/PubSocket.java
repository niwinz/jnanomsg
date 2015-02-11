package nanomsg.pubsub;

import nanomsg.Socket;
import nanomsg.Nanomsg;

public class PubSocket extends Socket {
  public PubSocket(int domain) {
    super(domain, Nanomsg.constants.NN_PUB);
  }

  public PubSocket() {
    this(Nanomsg.constants.AF_SP);
  }
}
