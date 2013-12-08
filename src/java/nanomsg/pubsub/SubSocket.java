package nanomsg.pubsub;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.ptr.*;

import java.io.UnsupportedEncodingException;

import nanomsg.jna.NanoMsg;
import nanomsg.RWSocket;
import nanomsg.Constants;


public class SubSocket extends RWSocket {
    public SubSocket(int domain) {
        super(domain, Constants.NN_SUB);
    }

    public SubSocket() {
        this(Constants.AF_SP);
    }

    public void subscribe(final String data) {
        final int socket = getSocket();

        try {
            final byte[] patternBytes = data.getBytes("utf-8");
            final Memory mem = new Memory(patternBytes.length);
            mem.write(0, patternBytes, 0, patternBytes.length);

            NanoMsg.nn_setsockopt(socket, Constants.NN_SUB, Constants.NN_SUB_SUBSCRIBE, mem, patternBytes.length);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
