package nanomsg.exceptions;

public class EAgainException extends IOException {
    public EAgainException(String message) {
        super(message);
    }
}
