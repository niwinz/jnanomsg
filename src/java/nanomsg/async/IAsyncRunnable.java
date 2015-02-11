package nanomsg.async;

import nanomsg.exceptions.EAgainException;

public interface IAsyncRunnable {
  public void run() throws EAgainException;
}
