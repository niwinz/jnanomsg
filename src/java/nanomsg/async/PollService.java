package nanomsg.async;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.Queue;
import java.util.LinkedList;
import java.lang.Thread;

import nanomsg.async.IAsyncRunnable;
import nanomsg.exceptions.EAgainException;
import nanomsg.ISocket;


public class PollService implements Runnable {
    private final LinkedBlockingQueue<IAsyncRunnable> queue = new LinkedBlockingQueue<IAsyncRunnable>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    public static final PollService service = new PollService();

    public void registerOnce(final IAsyncRunnable handler) {
        /* Start scheduler if it not started */
        if (started.compareAndSet(false, true)) {
            final Thread t = new Thread(this, "nanomsg-poll-scheduler");
            t.start();
        }

        queue.offer(handler);
    }

    @Override
    public String toString() {
        return "pending=" + queue.size() + ", thread started:" + started.get();
    }

    public void run() {
        while (true) {
            try {
                final IAsyncRunnable handler = queue.take();
                try {
                    handler.run();
                } catch (EAgainException e) {
                    queue.offer(handler);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
