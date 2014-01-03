package nanomsg.exceptions;

public class EmptyResponseException extends IOException {
    public EmptyResponseException(String message) {
        super(message);
    }
}
