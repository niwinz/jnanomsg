package nanomsg.async.impl;

import nanomsg.Socket;
import nanomsg.async.AsyncOperation;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.IAsyncScheduler;
import nanomsg.exceptions.IOException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static nanomsg.Nanomsg.Error.EAGAIN;


public class ThreadPoolScheduler implements Runnable, IAsyncScheduler {
  private final LinkedBlockingQueue<IAsyncRunnable> queue = new LinkedBlockingQueue<IAsyncRunnable>();
  private final int concurrency = Runtime.getRuntime().availableProcessors();
  private final AtomicBoolean started = new AtomicBoolean(false);

  private static ThreadPoolScheduler instance = null;

  public synchronized static ThreadPoolScheduler getInstance() {
    if(instance == null) {
      instance = new ThreadPoolScheduler();
    }
    return instance;
  }

  private void startThreadGroup() {
    final ThreadGroup group = new ThreadGroup("nanomsg-scheduler");
    group.setDaemon(false);

    for (int i=0; i<concurrency; ++i) {
      final Thread t = new Thread(group, this);
      t.setDaemon(false);
      t.start();
    }
  }

  public void schedule(final Socket sock, final AsyncOperation op, final IAsyncRunnable handler) throws InterruptedException {
    /* Start scheduler if it not started */
    if (started.compareAndSet(false, true)) {
      startThreadGroup();
    }

    queue.put(handler);
  }

  @Override
  public String toString() {
    return "pending=" + queue.size() + ", pool started:" + started.get();
  }

  public void run() {
    while (true) {
      if (Thread.interrupted()) {
        return;
      }

      try {
        final IAsyncRunnable handler = queue.take();
        try {
          handler.run();
        } catch (IOException e) {
          final int errno = e.getErrno();
          if (errno == EAGAIN.value()) {
            queue.put(handler);
          }
        }
      } catch (InterruptedException e) {
        break;
      }
    }
  }
}
