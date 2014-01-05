package nanomsg;
import java.nio.charset.Charset;

public class Message {
    private final byte[] messageData;

    public Message(final String data) {
        messageData = data.getBytes(Charset.forName("UTF-8"));
    }

    public Message(final byte[] data) {
        messageData = data;
    }

    public String toString() {
        return new String(messageData, Charset.forName("UTF-8"));
    }

    public String toString(String encoding) {
        return new String(messageData, Charset.forName(encoding));
    }

    public String toString(Charset encoding) {
        return new String(messageData, encoding);
    }

    public byte[] toBytes() {
        return messageData;
    }
}
