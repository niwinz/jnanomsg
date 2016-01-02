package nanomsg.bus;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.Socket;


public class BusSocket extends Socket {
  public BusSocket(Domain domain) {
    super(domain, SocketType.NN_BUS);
  }

  public BusSocket() {
    this(Domain.AF_SP);
  }
}
