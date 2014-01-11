package nanomsg.pubsub;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.ptr.*;

import java.io.UnsupportedEncodingException;

import nanomsg.NativeLibrary;
import nanomsg.Socket;
import nanomsg.Nanomsg;
import nanomsg.exceptions.IOException;


public class SubSocket extends Socket {
    public SubSocket(int domain) {
        super(domain, Nanomsg.constants.NN_SUB);
    }

    public SubSocket() {
        this(Nanomsg.constants.AF_SP);
    }

    public void subscribe(final String data) throws IOException {
        final int socket = getNativeSocket();

        try {
            final byte[] patternBytes = data.getBytes("utf-8");
            final Memory mem = new Memory(patternBytes.length);
            mem.write(0, patternBytes, 0, patternBytes.length);

            NativeLibrary.nn_setsockopt(socket, Nanomsg.constants.NN_SUB, Nanomsg.constants.NN_SUB_SUBSCRIBE, mem, patternBytes.length);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }
}
