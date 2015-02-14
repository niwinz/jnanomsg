package nanomsg.async;

import nanomsg.Socket;
import nanomsg.async.AsyncOperation;


public interface IAsyncScheduler {
    public void schedule(final Socket sock, final AsyncOperation op, final IAsyncRunnable runnable) throws InterruptedException;
}
