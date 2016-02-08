package nanomsg.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;
import java.nio.ByteBuffer;

import nanomsg.Socket;
import nanomsg.Poller;
import nanomsg.exceptions.EAgainException;
import nanomsg.exceptions.IOException;

import static nanomsg.Nanomsg.Error.EAGAIN;


public class AsyncSocket {
  private final Socket socket;
  private final ExecutorService executor;

  /**
   * Given any Socket subclass create new AsyncSocket instance.
   *
   * @param socket a socket instance.
   * @param executor a executor service used for the event loop.
   */
  public AsyncSocket(final Socket socket, final ExecutorService executor) {
    this.socket = socket;
    this.executor = executor;
  }

  /**
   * Given any Socket subclass create new AsyncSocket instance.
   *
   * The ForkJoinPool.commonPool() will be used as executor.
   *
   * @param socket a socket instance.
   */
  public AsyncSocket(final Socket socket) {
    this.socket = socket;
    this.executor = ForkJoinPool.commonPool();
  }

  /**
   * Send a message.
   *
   * @param data byte buffer that represents a message.
   * @return a completable future that eventuall will be
   * resolved with the number of sended bytes.
   */
  CompletionStage<Integer> send(final String v) {
    final CompletableFuture<Integer> result = new CompletableFuture<Integer>();
    final AsyncSendTask task = new AsyncSendStringTask(v, socket, result, executor);
    executor.execute(task);
    return result;
  }

  /**
   * Send a message.
   *
   * @param data byte buffer that represents a message.
   * @return a completable future that eventuall will be
   * resolved with the number of sended bytes.
   */
  CompletionStage<Integer> send(final byte[] v) {
    final CompletableFuture<Integer> result = new CompletableFuture<Integer>();
    final AsyncSendTask task = new AsyncSendBytesTask(v, socket, result, executor);
    executor.execute(task);
    return result;
  }

  private static abstract class AsyncTask implements Runnable {
    protected final Poller poller;
    protected final ExecutorService executor;
    protected final CompletableFuture result;
    protected final Socket socket;

    protected boolean setup = false;

    public AsyncTask(final Socket socket,
                     final CompletableFuture result,
                     final ExecutorService executor) {
      this.poller = new Poller(1, 600);
      this.executor = executor;
      this.result = result;
      this.socket = socket;
    }

    protected void schedule() {
      this.executor.execute(this);
    }
  }

  private static abstract class AsyncSendTask extends AsyncTask {
    public AsyncSendTask(final Socket socket,
                         final CompletableFuture result,
                         final ExecutorService executor) {
      super(socket, result, executor);
    }

    protected abstract void doSend();

    public void run() {
      if (this.result.isDone()) {
        return;
      }

      if (!this.setup) {
        this.poller.register(this.socket, Poller.POLLOUT);
        this.setup = true;
      }

      try {
        int num = this.poller.poll(1000);
        if (num == 0) {
          this.schedule();
        } else {
          final boolean isWritable = this.poller.isWritable(this.socket);

          if (isWritable) {
            this.doSend();
          } else {
            this.schedule();
          }
        }
      } catch (Exception e) {
        this.result.completeExceptionally(e);
      }
    }
  }

  private static abstract class AsyncRecvTask extends AsyncTask {
    public AsyncRecvTask(final Socket socket,
                         final CompletableFuture result,
                         final ExecutorService executor) {
      super(socket, result, executor);
    }

    protected abstract void doReceive();

    public void run() {
      if (this.result.isDone()) {
        return;
      }

      if (!this.setup) {
        this.poller.register(this.socket, Poller.POLLIN);
        this.setup = true;
      }

      try {
        int num = this.poller.poll(1000);
        if (num == 0) {
          this.schedule();
        } else {
          final boolean isReadable = this.poller.isReadable(this.socket);

          if (isReadable) {
            this.doReceive();
          } else {
            this.schedule();
          }
        }
      } catch (Exception e) {
        this.result.completeExceptionally(e);
      }
    }
  }

  private static class AsyncSendStringTask
    extends AsyncSendTask {

    private final String data;

    public AsyncSendStringTask(final String data,
                               final Socket socket,
                               final CompletableFuture result,
                               final ExecutorService executor) {
      super(socket, result, executor);
      this.data = data;
    }

    protected void doSend() {
      final Integer result = this.socket.send(this.data);
      this.result.complete(result);
    }
  }

  private static class AsyncSendBytesTask
    extends AsyncSendTask {

    private final byte[] data;

    public AsyncSendBytesTask(final byte[] data,
                              final Socket socket,
                              final CompletableFuture result,
                              final ExecutorService executor) {
      super(socket, result, executor);
      this.data = data;
    }

    protected void doSend() {
      final Integer result = this.socket.send(this.data);
      this.result.complete(result);
    }
  }

  private static class AsyncRecvStringTask extends AsyncRecvTask {
    public AsyncRecvStringTask(final Socket socket,
                               final CompletableFuture result,
                               final ExecutorService executor) {
      super(socket, result, executor);
    }

    protected void doReceive() {
      String data = this.socket.recvString(false);
      this.result.complete(data);
    }
  }

  private static class AsyncRecvBytesTask extends AsyncRecvTask {
    public AsyncRecvBytesTask(final Socket socket,
                              final CompletableFuture result,
                              final ExecutorService executor) {
      super(socket, result, executor);
    }

    protected void doReceive() {
      byte[] data = this.socket.recvBytes(false);
      this.result.complete(data);
    }
  }

  private static class AsyncRecvBufferTask extends AsyncRecvTask {
    public AsyncRecvBufferTask(final Socket socket,
                              final CompletableFuture result,
                              final ExecutorService executor) {
      super(socket, result, executor);
    }

    protected void doReceive() {
      ByteBuffer data = this.socket.recv(false);
      this.result.complete(data);
    }
  }
}
