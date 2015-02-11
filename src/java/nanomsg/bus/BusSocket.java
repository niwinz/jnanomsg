package nanomsg.bus;

import nanomsg.Socket;
import nanomsg.Nanomsg;


public class BusSocket extends Socket {
  public BusSocket(int domain) {
    super(domain, Nanomsg.constants.NN_BUS);
  }

  public BusSocket() {
    this(Nanomsg.constants.AF_SP);
  }
}
