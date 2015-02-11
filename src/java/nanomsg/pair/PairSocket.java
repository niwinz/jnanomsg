package nanomsg.pair;

import nanomsg.Socket;
import nanomsg.Nanomsg;


public class PairSocket extends Socket {
  public PairSocket(int domain) {
    super(domain, Nanomsg.constants.NN_PAIR);
  }

  public PairSocket() {
    this(Nanomsg.constants.AF_SP);
  }
}
