package nanomsg.async;
import nanomsg.Socket;

public interface IAsyncScheduler {
    public void schedule(final Socket sock, final IAsyncRunnable runnable) throws InterruptedException;
}
