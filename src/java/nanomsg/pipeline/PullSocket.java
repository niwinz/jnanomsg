package nanomsg.pipeline;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.AbstractSocket;

public class PullSocket
  extends AbstractSocket {

  public PullSocket(Domain domain) {
    super(domain, SocketType.NN_PULL);
  }

  public PullSocket() {
    this(Domain.AF_SP);
  }
}
