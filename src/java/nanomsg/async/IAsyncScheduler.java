package nanomsg.async;

public interface IAsyncScheduler {
    public void schedule(final IAsyncRunnable runnable) throws InterruptedException;
}
