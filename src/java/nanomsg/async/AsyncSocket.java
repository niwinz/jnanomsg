package nanomsg.async;

import nanomsg.ISocket;
import nanomsg.Nanomsg;

import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;

import nanomsg.async.IAsyncCallback;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.IAsyncScheduler;

import nanomsg.async.SimpleAsyncScheduler;


/**
 * Experimental socket proxy that enables async way to
 * send or receive data from socket.
 */
public class AsyncSocket {
    private final ISocket socket;
    private final IAsyncScheduler scheduler;

    /**
     * Given any socket that implements ISocket interface
     * create new AsyncSocket proxy for it.
     *
     * NOTE: this reuses a common scheduler.
     *
     * @param socket any socket that implements ISocket interface
     */
    public AsyncSocket(final ISocket socket) {
        this(socket, SimpleAsyncScheduler.instance);
    }

    /**
     * Given any socket that implements ISocket interface
     * and scheduler instance, create new AsyncSocket proxy
     * for it.
     *
     * @param socket any socket that implements ISocket interface
     */
    public AsyncSocket(final ISocket socket, IAsyncScheduler scheduler) {
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
    public void sendString(final String data, final IAsyncCallback<Boolean> callback) throws InterruptedException {
        scheduler.schedule(new IAsyncRunnable() {
            public void run() throws EAgainException {
                try {
                    socket.sendString(data);
                    callback.success(true);
                } catch (IOException e) {
                    if (e.getErrno() == Nanomsg.constants.EAGAIN) {
                        throw new EAgainException(e);
                    } else {
                        callback.fail(e);
                    }
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
    public void recvString(final IAsyncCallback<String> callback) throws InterruptedException {
        scheduler.schedule(new IAsyncRunnable() {
            public void run() throws EAgainException {
                try {
                    final String received = socket.recvString();
                    callback.success(received);
                } catch (IOException e) {
                    if (e.getErrno() == Nanomsg.constants.EAGAIN) {
                        throw new EAgainException(e);
                    } else {
                        callback.fail(e);
                    }
                }
            }
        });
    }

    /**
     * Given a string and callback, sends data using a proxied
     * socket using a common executor and execute a callback when it
     * are finished.
     *
     * @param data string to send.
     * @param callback IAsyncCallback interface object.
     */
    public void sendBytes(final byte[] data, final IAsyncCallback<Boolean> callback) throws InterruptedException {
        scheduler.schedule(new IAsyncRunnable() {
            public void run() throws EAgainException {
                try {
                    socket.sendBytes(data);
                    callback.success(true);
                } catch (IOException e) {
                    if (e.getErrno() == Nanomsg.constants.EAGAIN) {
                        throw new EAgainException(e);
                    } else {
                        callback.fail(e);
                    }
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
    public void recvBytes(final IAsyncCallback<byte[]> callback) throws InterruptedException {
        scheduler.schedule(new IAsyncRunnable() {
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
        });
    }
}
