package nanomsg.async.impl;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Queue;
import java.util.Map;
import java.util.LinkedList;
import java.lang.Thread;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;

import nanomsg.Socket;
import nanomsg.Nanomsg;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.IAsyncScheduler;
import nanomsg.async.AsyncOperation;
import nanomsg.async.impl.epoll.Epoll;

import nanomsg.exceptions.EAgainException;
import nanomsg.exceptions.IOException;

public class PollScheduler implements Runnable, IAsyncScheduler {
  /* Maps used for store references tu socket; */
  private final Map<Integer, IAsyncRunnable> runnableMap = new ConcurrentHashMap<Integer, IAsyncRunnable>();

  private final AtomicBoolean started = new AtomicBoolean(false);
  private final int epollFd = Epoll.epoll_create1(Epoll.EPOLL_CLOEXEC);
  public static final PollScheduler instance = new PollScheduler();

  public void register(final int fd, final int flags, final IAsyncRunnable runnable) {
      // Register a file description in a runnables map
      runnableMap.put(fd, runnable);

      // Create new epoll event instance
      final Epoll.EpollEvent.ByReference eevent = new Epoll.EpollEvent.ByReference(fd, flags);

      // Register the file descriptor and epoll event into epoll inststance.
      Epoll.epoll_ctl(epollFd, Epoll.EPOLL_CTL_ADD, fd, eevent);

      // Temporal debug
      System.out.println("registerOnce: fd:" + fd);
      System.out.println("registerOnce: total:" + runnableMap.size());
  }

  public void registerRead(final Socket sock, final IAsyncRunnable runnable) {
    register(sock.getRcvFd(), Epoll.EPOLLIN | Epoll.EPOLLONESHOT, runnable);
  }

  public void registerWrite(final Socket sock, final IAsyncRunnable runnable) {
    register(sock.getSndFd(), Epoll.EPOLLOUT| Epoll.EPOLLONESHOT, runnable);
  }

  public void schedule(final Socket sock, final AsyncOperation op, final IAsyncRunnable handler) throws InterruptedException {
    if (started.compareAndSet(false, true)) {
      final Thread t = new Thread(this, "nanomsg-poll-scheduler");
      t.start();
    }

    if (epollFd < 0) {
      throw new RuntimeException("Failed intialize epoll instance.");
    }

    if (op == AsyncOperation.READ) {
        this.registerRead(sock, handler);
    } else if (op == AsyncOperation.WRITE) {
        this.registerWrite(sock, handler);
    } else {
      throw new RuntimeException("Operation not supported.");
    }
  }

  private void processFd(final Epoll.EpollEvent.ByReference event) {
    final int fd = event.data.fd;
    final IAsyncRunnable runnable = runnableMap.get(fd);

    try {
      runnable.run();
    } catch (IOException e) {
      final int errno = e.getErrno();

      if (errno == Nanomsg.constants.EAGAIN) {
        System.out.println("EAGAIN error for fd=" + fd);
        this.register(fd, event.events, runnable);
      } else {
        System.out.println("Error on runing the async runnable: " + errno);
      }
    }
  }

  public void run() {
    int readyCount;
    int err;

    final int MAX_EVENTS = 1;
    final int size_of_struct = new Epoll.EpollEvent().size();
    final Memory ptr = new Memory(MAX_EVENTS * size_of_struct);
    final Epoll.EpollEvent.ByReference event = new Epoll.EpollEvent.ByReference();

    // Debug only
    // System.out.println("Starting epoll.");

    while (true) {
      readyCount = Epoll.epoll_wait(epollFd, ptr, MAX_EVENTS, -1);
      System.out.println("Epoll loop: found " + readyCount + " events.");

      if (readyCount <= 0) {
        continue;
      }

      err = Native.getLastError();
      // Properly handle nanomsg errors
      System.out.println("nn_err: " + err);

      for(int i = 0; i < readyCount; ++i) {
        event.reuse(ptr, size_of_struct * i);

        // System.out.println("Type EPOLLIN: " + (event.events & Epoll.EPOLLIN));
        // System.out.println("Type EPOLLOUT: " + (event.events & Epoll.EPOLLOUT));
        // System.out.println("Type EPOLLHUP: " + (event.events & Epoll.EPOLLHUP));

        if (runnableMap.containsKey(event.data.fd)) {
          Epoll.epoll_ctl(epollFd, Epoll.EPOLL_CTL_DEL, event.data.fd, event);
          processFd(event);
        }

      }
    }
  }
}
