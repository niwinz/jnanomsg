package nanomsg.pubsub;

import com.sun.jna.Memory;
import nanomsg.Nanomsg;
import nanomsg.NativeLibrary;
import nanomsg.Socket;
import nanomsg.exceptions.IOException;

import java.io.UnsupportedEncodingException;


public class SubSocket extends Socket {
    public SubSocket(int domain) {
        super(domain, Nanomsg.constants.NN_SUB);
    }

    public SubSocket() {
        this(Nanomsg.constants.AF_SP);
    }

    @Override
    public void subscribe(final String topic) throws IOException {
        try {
            subscribe(topic.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void subscribe(final byte[] patternBytes) throws IOException {
        final int socket = getNativeSocket();

        final Memory mem = new Memory(patternBytes.length);
        mem.write(0, patternBytes, 0, patternBytes.length);

        NativeLibrary.nn_setsockopt(socket, Nanomsg.constants.NN_SUB, Nanomsg.constants.NN_SUB_SUBSCRIBE,
                    mem, patternBytes.length);
    }

    @Override
    public void unsubscribe(final String topic) throws IOException {
        try {
            unsubscribe(topic.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }

    @Override public void unsubscribe(byte[] patternBytes) throws IOException {
        final int socket = getNativeSocket();

        final Memory mem = new Memory(patternBytes.length);
        mem.write(0, patternBytes, 0, patternBytes.length);

        NativeLibrary.nn_setsockopt(socket, Nanomsg.constants.NN_SUB, Nanomsg.constants.NN_SUB_UNSUBSCRIBE,
                    mem, patternBytes.length);
    }
}
