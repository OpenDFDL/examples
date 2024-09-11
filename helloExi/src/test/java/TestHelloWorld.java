import org.junit.Test;
import org.junit.Assert;

public class TestHelloEXI {

  @Test
  public void testMain() {
    try{
       HelloEXI.main(new String[0]);
    }
    catch(Exception e){
      Assert.fail("Should not have thrown any exception");
    }
  }
}
