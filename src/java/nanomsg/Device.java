package nanomsg;

import java.lang.Runnable;
import nanomsg.NativeLibrary;


public class Device implements Runnable {
  private final int sourceSocket;
  private final int destSocket;

  public Device(final int sourceSocket, final int destSocket) {
    this.sourceSocket = sourceSocket;
    this.destSocket = destSocket;
  }

  public void run() {
    NativeLibrary.nn_device(sourceSocket, destSocket);
  }
}
