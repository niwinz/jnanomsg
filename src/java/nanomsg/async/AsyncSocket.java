package nanomsg.async;

import nanomsg.Nanomsg;
import nanomsg.Socket;

import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;

import nanomsg.async.IAsyncCallback;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.IAsyncScheduler;

import nanomsg.async.impl.EPollScheduler;
import nanomsg.async.impl.ThreadPoolScheduler;

/**
 * Experimental socket proxy that enables async way to
 * send or receive data from socket.
 */
public class AsyncSocket {
  private Socket socket;
  private IAsyncScheduler scheduler;

  private void setSocket(final Socket socket) {
    this.socket = socket;
    this.socket.setSendTimeout(300);
    this.socket.setRecvTimeout(300);
  }

  private void setScheduler(final IAsyncScheduler scheduler) {
    if (scheduler == null) {
      final String osName = System.getProperty("os.name");
      if (osName.startsWith("Linux") || osName.startsWith("LINUX")) {
        this.scheduler = EPollScheduler.getInstance();
      } else {
        this.scheduler = ThreadPoolScheduler.getInstance();
      }
    } else {
      this.scheduler = scheduler;
    }
  }

  /**
   * Given any Socket subclass create new
   * AsyncSocket instance.
   *
   * NOTE: this reuses a common scheduler.
   *
   * @param socket
   */
  public AsyncSocket(final Socket socket) {
    this.setSocket(socket);
    this.setScheduler(null);
  }

  /**
   * Given any Socket subclass create new
   * AsyncSocket instance.
   *
   * @param socket
   * @param scheduler A scheduler implementation.
   */
  public AsyncSocket(final Socket socket, IAsyncScheduler scheduler) {
    this.setSocket(socket);
    this.setScheduler(scheduler);
  }

  /**
   * Given a string and callback, sends data using a proxied
   * socket using a common executor and execute a callback when it
   * are finished.
   *
   * @param data string to send.
   * @param callback IAsyncCallback interface object.
   */
  public void send(final String data, final IAsyncCallback<Boolean> callback) {
    final IAsyncRunnable runnable = new IAsyncRunnable() {
        public void run() throws EAgainException {
          try {
            socket.send(data);
            callback.success(true);
          } catch (IOException e) {
            if (e.getErrno() == Nanomsg.constants.EAGAIN) {
              throw new EAgainException(e);
            } else {
              callback.fail(e);
            }
          }
        }
      };

    try {
      scheduler.schedule(socket, AsyncOperation.WRITE, runnable);
    } catch (InterruptedException e) {
      callback.fail(e);
    }
  }

  /**
   * Given a callback, it try receive data from socket using
   * the common defined executor and execute callback whet
   * any data received.
   *
   * @param callback IAsyncCallback interface object.
   */
  public void recvString(final IAsyncCallback<String> callback) {
    final IAsyncRunnable runnable = new IAsyncRunnable() {
        public void run() throws EAgainException {
          try {
            final String received = socket.recvString();
            callback.success(received);
          } catch (IOException e) {
            if (e.getErrno() == Nanomsg.constants.EAGAIN) {
              throw e;
            } else {
              callback.fail(e);
            }
          }
        }
      };

    try {
      scheduler.schedule(socket, AsyncOperation.READ, runnable);
    } catch (InterruptedException e) {
      callback.fail(e);
    }
  }

  /**
   * Given a string and callback, sends data using a proxied
   * socket using a common executor and execute a callback when it
   * are finished.
   *
   * @param data string to send.
   * @param callback IAsyncCallback interface object.
   */
  public void send(final byte[] data, final IAsyncCallback<Boolean> callback) {
    final IAsyncRunnable runnable = new IAsyncRunnable() {
        public void run() throws EAgainException {
          try {
            socket.send(data);
            callback.success(true);
          } catch (IOException e) {
            if (e.getErrno() == Nanomsg.constants.EAGAIN) {
              throw new EAgainException(e);
            } else {
              callback.fail(e);
            }
          }
        }
      };

    try {
      scheduler.schedule(socket, AsyncOperation.WRITE, runnable);
    } catch (InterruptedException e) {
      callback.fail(e);
    }
  }

  /**
   * Given a callback, it try receive data from socket using
   * the common defined executor and execute callback whet
   * any data received.
   *
   * @param callback IAsyncCallback interface object.
   */
  public void recvBytes(final IAsyncCallback<byte[]> callback) {
    final IAsyncRunnable runnable = new IAsyncRunnable() {
        public void run() throws EAgainException {
          try {
            final byte[] received = socket.recvBytes();
            callback.success(received);
          } catch (IOException e) {
            if (e.getErrno() == Nanomsg.constants.EAGAIN) {
              throw new EAgainException(e);
            } else {
              callback.fail(e);
            }
          }
        }
      };

    try {
      scheduler.schedule(socket, AsyncOperation.READ, runnable);
    } catch (InterruptedException e) {
      callback.fail(e);
    }
  }
}
