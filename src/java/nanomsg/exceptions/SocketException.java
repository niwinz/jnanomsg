package nanomsg.exceptions;

import java.util.EnumSet;

import static nanomsg.Nanomsg.getErrorNumber;
import static nanomsg.Nanomsg.getError;
import nanomsg.Nanomsg;
import nanomsg.Nanomsg.Error;

public class SocketException extends RuntimeException {
  protected Error error = null;

  public SocketException(final int rc) {
    super(Nanomsg.getError());
    final int errno = Nanomsg.getErrorNumber();

    for(Error err: EnumSet.allOf(Error.class)) {
      if (err.value() == errno) {
        this.error = err;
        break;
      }
    }
  }

  public SocketException(final String message, Throwable cause) {
    super(message, cause);
  }

  public SocketException(final String message) {
    super(message);
  }

  public SocketException(Throwable cause) {
    super(cause);
  }
}
