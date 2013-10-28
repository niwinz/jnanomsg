package nanomsg;

import nanomsg.jna.NanoMsg;
import com.sun.jna.Pointer;

public abstract class Socket {
    protected int domain;
    protected int protocol;
    protected int socket;

    protected boolean closed = false;
    protected boolean opened = false;

    public Socket(int domain, int protocol) {
        this.domain = domain;
        this.protocol = protocol;
        this.socket = NanoMsg.nn_socket(domain, protocol);
        this.opened = true;
    }

    public void close() {
        if (this.opened && !this.closed) {
            this.closed = true;
            NanoMsg.nn_close(this.socket);
        }
    }

    public int getSocket() {
        return this.socket;
    }

    public void bind(String dir) throws RuntimeException {
        int endpoint = NanoMsg.nn_bind(this.socket, dir);

        if (endpoint < 0) {
            throw new RuntimeException("bind error");
        }
    }

    public void connect(String dir) throws RuntimeException {
        int endpoint = NanoMsg.nn_connect(this.socket, dir);

        if (endpoint < 0) {
            throw new RuntimeException("bind error");
        }
    }
}
