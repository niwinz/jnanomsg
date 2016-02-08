package nanomsg.exceptions;

import nanomsg.exceptions.IOException;
import static nanomsg.Nanomsg.Error.EAGAIN;


public class EAgainException extends IOException {
  public EAgainException(final String message) {
    super(message);
    this.errno = EAGAIN.value();
  }

  public EAgainException(final String message, final int errno) {
    super(message);
    this.errno = errno;
  }

  public EAgainException(IOException cause) {
    super(cause);
    this.errno = cause.getErrno();
  }
}
