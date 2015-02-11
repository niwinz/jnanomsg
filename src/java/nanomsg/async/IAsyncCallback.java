package nanomsg.async;

import java.lang.Throwable;

public interface IAsyncCallback<T> {
  public void success(T result);
  public void fail(Throwable t);
}
