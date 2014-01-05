package nanomsg;
import java.nio.charset.Charset;

public class Message {
    private byte[] messageData;

    public Message(String data) {
        messageData = data.getBytes(Charset.forName("UTF-8"));
    }

    public Message(byte[] data) {
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
