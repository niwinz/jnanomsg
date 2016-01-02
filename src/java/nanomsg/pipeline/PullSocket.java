package nanomsg.pipeline;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.Socket;

public class PullSocket extends Socket {
  public PullSocket(Domain domain) {
    super(domain, SocketType.NN_PULL);
  }

  public PullSocket() {
    this(Domain.AF_SP);
  }
}
