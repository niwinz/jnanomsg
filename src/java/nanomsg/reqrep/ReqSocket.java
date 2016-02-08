package nanomsg.reqrep;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.AbstractSocket;

public class ReqSocket
  extends AbstractSocket {

  public ReqSocket(Domain domain) {
    super(domain, SocketType.NN_REQ);
  }

  public ReqSocket() {
    this(Domain.AF_SP);
  }
}
