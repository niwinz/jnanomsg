package nanomsg.exceptions;

public class IOException extends RuntimeException {
  protected int errno = -1;

  public IOException(final String message) {
    super(message);
  }

  public IOException(final String message, final int errno) {
    super(message);
    this.errno = errno;
  }

  public IOException(Throwable cause) {
    super(cause);
  }

  public int getErrno() {
    return this.errno;
  }
}
