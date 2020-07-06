import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestXSLTCSV {

    @Test
    public void testXSLTCSV1() throws Exception {
      ByteArrayOutputStream myOut = new ByteArrayOutputStream();
      System.setOut(new PrintStream(myOut));

      String[] args = {"src/test/resources/Dummy.xml", "src/main/resources/processCSV.xsl"};
      net.sf.saxon.Transform.main(args);

      String standardOutput = myOut.toString();

      System.err.println(standardOutput);

      assertTrue(standardOutput.contains("<data recordCount=\"8\">"));
      assertTrue(standardOutput.contains("<chevy model=\"E350\" year=\"1997\"/><chevy model=\"Venture Extended Edition\" year=\"1999\"/><chevy model=\"Venture Extended Edition\" year=\"2000\"/>")); 

    }

}
