package nanomsg.async;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Queue;
import java.util.LinkedList;
import java.lang.Thread;

import nanomsg.ISocket;
import nanomsg.Nanomsg;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.IAsyncScheduler;
import nanomsg.exceptions.IOException;


public class SimpleAsyncScheduler implements Runnable, IAsyncScheduler {
    private final LinkedBlockingQueue<IAsyncRunnable> queue = new LinkedBlockingQueue<IAsyncRunnable>();
    private final int concurrency = Runtime.getRuntime().availableProcessors();
    private final AtomicBoolean started = new AtomicBoolean(false);

    public static final IAsyncScheduler instance = new SimpleAsyncScheduler();

    private void startThreadGroup() {
        final ThreadGroup group = new ThreadGroup("nanomsg-scheduler");
        group.setDaemon(false);

        for (int i=0; i<concurrency; ++i) {
            final Thread t = new Thread(group, this);
            t.setDaemon(false);
            t.start();
        }
    }

    public void schedule(final IAsyncRunnable handler) throws InterruptedException {
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
            try {
                final IAsyncRunnable handler = queue.take();
                try {
                    handler.run();
                } catch (IOException e) {
                    final int errno = e.getErrno();
                    if (errno == Nanomsg.constants.EAGAIN) {
                        queue.put(handler);
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
