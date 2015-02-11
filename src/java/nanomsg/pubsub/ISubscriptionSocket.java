package nanomsg.pubsub;

import nanomsg.exceptions.IOException;

/**
 * Common interface for socket that implement pub/sub paradigm.
 */
public interface ISubscriptionSocket {
  public void subscribe(final String data) throws IOException;
  public void subscribe(final byte[] data) throws IOException;
  public void unsubscribe(final String data) throws IOException;
  public void unsubscribe(final byte[] data) throws IOException;
}

