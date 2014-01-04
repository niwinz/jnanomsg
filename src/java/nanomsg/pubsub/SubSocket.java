package nanomsg.pubsub;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.ptr.*;

import java.io.UnsupportedEncodingException;

import nanomsg.NativeLibrary;
import nanomsg.RWSocket;
import nanomsg.Nanomsg;
import nanomsg.exceptions.IOException;


public class SubSocket extends RWSocket {
    public SubSocket(int domain) {
        super(domain, Nanomsg.NN_SUB);
    }

    public SubSocket() {
        this(Nanomsg.AF_SP);
    }

    public void subscribe(final String data) throws IOException {
        final int socket = getSocket();

        try {
            final byte[] patternBytes = data.getBytes("utf-8");
            final Memory mem = new Memory(patternBytes.length);
            mem.write(0, patternBytes, 0, patternBytes.length);

            NativeLibrary.nn_setsockopt(socket, Nanomsg.NN_SUB, Nanomsg.NN_SUB_SUBSCRIBE, mem, patternBytes.length);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }
}
