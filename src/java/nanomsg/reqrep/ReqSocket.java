package nanomsg.reqrep;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.Socket;


public class ReqSocket extends Socket {
  public ReqSocket(Domain domain) {
    super(domain, SocketType.NN_REQ);
  }

  public ReqSocket() {
    this(Domain.AF_SP);
  }
}
