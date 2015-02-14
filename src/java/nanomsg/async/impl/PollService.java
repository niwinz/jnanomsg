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
import com.sun.jna.Native;

import nanomsg.ISocket;
import nanomsg.Nanomsg;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.impl.epoll.Epoll;

import nanomsg.exceptions.EAgainException;
import nanomsg.exceptions.IOException;



public class PollService implements Runnable {
    /* Maps used for store references tu socket; */
    private final Map<Integer, IAsyncRunnable> runnableMap = new ConcurrentHashMap<Integer, IAsyncRunnable>();

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final int epollFd = Epoll.epoll_create1(EPoll.EPOLL_CLOEXEC);
    public static final PollService service = new PollService();

    public void registerOnce(final ISocket sock, final int flag, final IAsyncRunnable runnable) {
        if (started.compareAndSet(false, true)) {
            final Thread t = new Thread(this, "nanomsg-poll-scheduler");
            t.start();
        }


        try {
            final int fd = sock.getFd(flag);
            runnableMap.put(fd, runnable);

            final int eventFlag = Epoll.EPOLLIN | Epoll.EPOLLOUT | Epoll.EPOLLHUP | Epoll.EPOLLONESHOT;

            /* Make new EpollEvent */
            final Epoll.EpollEvent event = new Epoll.EpollEvent.ByReference(fd, eventFlag);

            /* Put now created event into epoll */
            Epoll.epoll_ctl(epollFd, Epoll.EPOLL_CTL_ADD, fd, event.getPointer());

            System.out.println("registerOnce: fd:" + fd);
            System.out.println("registerOnce: total:" + runnableMap.size());
        } catch (IOException e) {
            System.out.println("registerOnce: error:" + e.toString());
        }
    }

    /* @Override */
    /* public String toString() { */
    /*     return "pending=" + queue.size() + ", thread started:" + started.get(); */
    /* } */

    private void processFd(final int fd) {
        final IAsyncRunnable runnable = runnableMap.get(fd);
        runnable.run();
    }

    public void run() {
        int readyCount;
        int err;
        final int MAX_EVENTS = 10;

        final int size_of_struct = new Epoll.EpollEvent().size();
        final Memory ptr = new Memory(MAX_EVENTS * size_of_struct);
        final Epoll.EpollEvent.ByReference event = new Epoll.EpollEvent.ByReference();

        System.out.println("Starting epoll.");

        while (true) {
            readyCount = Epoll.epoll_wait(epollFd, ptr, MAX_EVENTS, -1);
            System.out.println("Epoll loop:" + readyCount + "events.");

            if (readyCount <= 0) {
                continue;
            }

            err = Native.getLastError();
            /* if (err  { */
            /*     System.out.println("EINTR!!!"); */
            /*     continue; */
            /* } */

            for(int i = 0; i < readyCount; ++i) {
                event.reuse(ptr, size_of_struct * i);

                if (runnableMap.containsKey(event.data.fd)) {
                    processFd(event.data.fd);
                }

                Epoll.epoll_ctl(epollFd, Epoll.EPOLL_CTL_DEL, event.data.fd, event);
            }
        }
    }
}
