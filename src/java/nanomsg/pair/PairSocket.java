package nanomsg.pair;

import nanomsg.Nanomsg.Domain;
import nanomsg.Nanomsg.SocketType;
import nanomsg.Socket;


public class PairSocket extends Socket {
  public PairSocket(Domain domain) {
    super(domain, SocketType.NN_PAIR);
  }

  public PairSocket() {
    this(Domain.AF_SP);
  }
}
