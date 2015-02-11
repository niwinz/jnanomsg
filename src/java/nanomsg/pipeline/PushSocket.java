package nanomsg.pipeline;

import nanomsg.Socket;
import nanomsg.Nanomsg;


public class PushSocket extends Socket {
  public PushSocket(int domain) {
    super(domain, Nanomsg.constants.NN_PUSH);
  }

  public PushSocket() {
    this(Nanomsg.constants.AF_SP);
  }
}
