package nanomsg.async;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

import nanomsg.ISocket;
import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;

import nanomsg.async.IAsyncCallback;

/**
 * Experimental socket proxy that enables async way to
 * send or receive data from socket.
 *
 * At the mooment it only implements send/receive
 * string througt socket.
 */
public class AsyncSocketProxy {
    private final ISocket socket;
    public static final ForkJoinPool executor;

    static {
        final int parallelism = Runtime.getRuntime().availableProcessors() * 2;
        executor = new ForkJoinPool(parallelism, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    }

    /**
     * Given any socket that implements ISocket interface
     * create new AsyncSocketProxy proxy for it.
     *
     * @param socket any socket that implements ISocket interface
     */
    public AsyncSocketProxy(final ISocket socket) {
        this.socket = socket;
    }

    /**
     * Given a string and callback, sends data using a proxied
     * socket using a common executor and execute a callback when it
     * are finished.
     *
     * @param data string to send.
     * @param callback IAsyncCallback interface object.
     */
    public void sendString(final String data, final IAsyncCallback<Boolean> callback) {
        final ISocket socket = this.socket;
        final ForkJoinPool executor = AsyncSocketProxy.executor;

        executor.execute(new Runnable() {
            public void run() {
                try {
                    socket.sendString(data, true);
                    callback.success(true);
                } catch (IOException e) {
                    callback.fail(e);
                }
            }
        });
    }

    /**
     * Given a callback, it try receive data from socket using
     * the common defined executor and execute callback whet
     * any data received.
     *
     * @param callback IAsyncCallback interface object.
     */
    public void recvString(final IAsyncCallback<String> callback) {
        final ISocket socket = this.socket;
        final ForkJoinPool executor = AsyncSocketProxy.executor;

        executor.execute(new Runnable() {
            public void run() {
                try {
                    final String received = socket.recvString(true);
                    callback.success(received);
                } catch (IOException e) {
                    callback.fail(e);
                }
            }
        });
    }
}
