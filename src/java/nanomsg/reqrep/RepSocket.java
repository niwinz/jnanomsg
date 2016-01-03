package nanomsg.reqrep;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.Socket;


public class RepSocket extends Socket {
  public RepSocket(Domain domain) {
    super(domain, SocketType.NN_REP);
  }

  public RepSocket() {
    this(Domain.AF_SP);
  }
}
