package nanomsg.pipeline;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.Socket;


public class PushSocket extends Socket {
  public PushSocket(Domain domain) {
    super(domain, SocketType.NN_PUSH);
  }

  public PushSocket() {
    this(Domain.AF_SP);
  }
}
