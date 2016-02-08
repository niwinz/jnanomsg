package nanomsg.pipeline;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.AbstractSocket;


public class PushSocket extends AbstractSocket {
  public PushSocket(Domain domain) {
    super(domain, SocketType.NN_PUSH);
  }

  public PushSocket() {
    this(Domain.AF_SP);
  }
}
