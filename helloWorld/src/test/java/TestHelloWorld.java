import org.junit.Assert;
import org.junit.Test;

public class TestHelloWorld {

  @Test
  public void testMain() {
    try {
      HelloWorld.main(new String[0]);
    } catch (Exception e) {
      Assert.fail("Should not have thrown any exception");
    }
  }
}
