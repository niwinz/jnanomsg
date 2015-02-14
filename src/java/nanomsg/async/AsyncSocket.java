package nanomsg.async;

import nanomsg.Nanomsg;
import nanomsg.Socket;

import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;

import nanomsg.async.IAsyncCallback;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.IAsyncScheduler;

import nanomsg.async.SimpleAsyncScheduler;
import nanomsg.async.impl.PollScheduler;

/**
 * Experimental socket proxy that enables async way to
 * send or receive data from socket.
 */
public class AsyncSocket {
  private final Socket socket;
  private final IAsyncScheduler scheduler;

  /**
   * Given any Socket subclass create new
   * AsyncSocket instance.
   *
   * NOTE: this reuses a common scheduler.
   *
   * @param socket
   */
  public AsyncSocket(final Socket socket) {
    // this(socket, SimpleAsyncScheduler.instance);
    this(socket, PollScheduler.instance);
  }

  /**
   * Given any Socket subclass create new
   * AsyncSocket instance.
   *
   * @param socket
   * @param scheduler A scheduler implementation.
   */
  public AsyncSocket(final Socket socket, IAsyncScheduler scheduler) {
    this.socket = socket;
    this.scheduler = scheduler;

    this.socket.setSendTimeout(300);
    this.socket.setRecvTimeout(300);
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
