package nanomsg;

import nanomsg.Nanomsg.MethodOption;
import nanomsg.Nanomsg.SocketType;
import org.junit.Test;

import static nanomsg.Nanomsg.nn_symbols;
import static org.junit.Assert.assertEquals;

public class NanomsgTest {

  @Test
  public void canGetSymbols() {

    assertEquals(98, nn_symbols.size());
    assertEquals(SocketType.NN_PUB.value(), nn_symbols.get("NN_PUB"));
    assertEquals(MethodOption.NN_MSG.value(), new Integer(-1));
  }
}