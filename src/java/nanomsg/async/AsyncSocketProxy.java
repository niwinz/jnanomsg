package nanomsg.async;

import nanomsg.ISocket;
import nanomsg.Nanomsg;
import nanomsg.exceptions.IOException;
import nanomsg.exceptions.EAgainException;
import nanomsg.async.IAsyncCallback;
import nanomsg.async.IAsyncRunnable;
import nanomsg.async.PollService;


/**
 * Experimental socket proxy that enables async way to
 * send or receive data from socket.
 *
 * At the mooment it only implements send/receive
 * string througt socket.
 */
public class AsyncSocketProxy {
    private final ISocket socket;

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
        PollService.service.registerOnce(this.socket, Nanomsg.constants.NN_SNDFD, new IAsyncRunnable() {
            public void run()  {
                try {
                    socket.sendString(data, false);
                    callback.success(true);
                } catch (IOException e) {
                    System.out.println("!");
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
        PollService.service.registerOnce(this.socket, Nanomsg.constants.NN_RCVFD, new IAsyncRunnable() {
            public void run() {
                try {
                    final String received = socket.recvString(false);
                    callback.success(received);
                } catch (IOException e) {
                    callback.fail(e);
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
    public void sendBytes(final byte[] data, final IAsyncCallback<Boolean> callback) {
        PollService.service.registerOnce(this.socket, Nanomsg.constants.NN_SNDFD, new IAsyncRunnable() {
            public void run() {
                try {
                    socket.sendBytes(data, true);
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
    public void recvBytes(final IAsyncCallback<byte[]> callback) {
        PollService.service.registerOnce(this.socket, Nanomsg.constants.NN_RCVFD, new IAsyncRunnable() {
            public void run() {
                try {
                    final byte[] received = socket.recvBytes(true);
                    callback.success(received);
                } catch (IOException e) {
                    callback.fail(e);
                }
            }
        });
    }
}
