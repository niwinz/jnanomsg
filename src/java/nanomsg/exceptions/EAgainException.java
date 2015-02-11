package nanomsg.exceptions;

import nanomsg.exceptions.IOException;

public class EAgainException extends IOException {
  public EAgainException(IOException cause) {
    super(cause);
    this.errno = cause.getErrno();
  }
}
