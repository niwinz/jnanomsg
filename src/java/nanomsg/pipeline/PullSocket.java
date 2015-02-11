package nanomsg.pipeline;

import nanomsg.Socket;
import nanomsg.Nanomsg;


public class PullSocket extends Socket {
  public PullSocket(int domain) {
    super(domain, Nanomsg.constants.NN_PULL);
  }

  public PullSocket() {
    this(Nanomsg.constants.AF_SP);
  }
}
