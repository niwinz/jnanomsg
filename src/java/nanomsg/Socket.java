package nanomsg;

import com.sun.jna.Pointer;
import nanomsg.NativeLibrary;
import nanomsg.exceptions.IOException;


public abstract class Socket {
    private final int domain;
    private final int protocol;
    private final int socket;

    private boolean closed = false;
    private boolean opened = false;

    public Socket(int domain, int protocol) {
        this.domain = domain;
        this.protocol = protocol;
        this.socket = NativeLibrary.nn_socket(domain, protocol);
        this.opened = true;
    }

    public void close() {
        if (this.opened && !this.closed) {
            this.closed = true;
            NativeLibrary.nn_close(this.socket);
        }
    }

    public int getSocket() {
        return this.socket;
    }

    public void bind(String dir) throws IOException {
        final int endpoint = NativeLibrary.nn_bind(this.socket, dir);

        if (endpoint < 0) {
            throw new IOException(Nanomsg.getError());
        }
    }

    public void connect(String dir) throws IOException {
        final int endpoint = NativeLibrary.nn_connect(this.socket, dir);

        if (endpoint < 0) {
            throw new IOException(Nanomsg.getError());
        }
    }
}
