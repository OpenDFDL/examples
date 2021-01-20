
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.daffodil.japi.*;
import org.apache.daffodil.util.Misc;


import org.jdom2.output.XMLOutputter;
import org.junit.Test;

/**
 * Demonstrates using the Daffodil DFDL processor to
 * <ul>
 * <li>compile a DFDL schema
 * <li>parse non-XML data into XML in a streaming fashion.
 * </ul>
 */
public class HexWordsStreamingApp {

    @SuppressWarnings("SpellCheckingInspection")
    XMLOutputter xout = new XMLOutputter();
    // if you want pretty printing
    // xout.setFormat(Format.getPrettyFormat());

    URL schemaFileURL = HexWordsStreamingApp.class.getResource("/hexWords.dfdl.xsd");

    //
    // Setup for stream parsing.
    //
    MessageParser mp = new MessageParser(schemaFileURL, true);

    public HexWordsStreamingApp()
            throws IOException, URISyntaxException, MessageParser.CompileFailure {
        if (!mp.compilationWarnings.isEmpty()) {
            //
            // However the application wants to deal with
            // DFDL schema compilation warnings goes here.
            for (Diagnostic d : mp.compilationWarnings) {
                System.out.println(d.getSomeMessage());
            }
        }
    }

    /**
     * This method is a pretend application of Daffodil to parse streams of hexWords data.
     *
     * It calls parse repeatedly to pull each hexWord from the data stream.
     * @param is - the input stream of data
     * @throws IOException if the Schema URI cannot be opened.
     */
    public void application(InputStream is) throws IOException {
        //
        // Establish the input stream Daffodil will read messages from.
        // This would be redone on this same MessageParser object if, for example,
        // the data is coming from a TCP connection, and that connection gets broken.
        //
        mp.setInputStream(is);
        //
        // Do the parse calls, in a loop. Each call returns 1 message/object.
        //
        boolean isDone = false;
        while (!isDone) {

            System.out.println("**** Parse Attempt *****");

            //
            // parse one message from the stream
            //
            MessageParser.Result r = mp.parse();

            isDone = r.isEOD; // at end of data, exit the loop.

            if (r.diags != null) {
                if (!r.isProcessingError && r.isValidationError) {
                    //
                    // only a validation error. We can continue to parse
                    // afterwards if we want to, because we get an infoset.
                    //
                    // validation errors are not fatal to the parsing.
                    //
                    for (Diagnostic d : r.diags) {
                        System.out.println(d.getMessage());
                    }
                    isDone = false; // continue with more parses
                } else if (r.isProcessingError && !r.isEOD) {
                    //
                    // non-validation errors are fatal.
                    // We can't continue. But if we got errors but are at
                    // end of data, then we suppress these errors.
                    //
                    for (Diagnostic d : r.diags) {
                        System.out.println(d.getMessage());
                    }
                    isDone = true;
                } else {
                    //
                    // How the application deals with
                    // parse time warnings goes here.
                    //
                    // parse-time warnings are rare, but possible.
                    for (Diagnostic d : r.diags) {
                        System.out.println(d.getMessage());
                    }
                }
                // Note that none of the above cases apply
                // if we got errors, but r.isEOD (at end of data).
            }

            if (!r.isProcessingError) {
                //
                // How the application wants to behave when there is
                // no fatal error,  we successfully parsed it
                // Display the message.
                xout.output(r.message, System.out);
            }
            System.out.println("**************************");
        }
    }

    @Test
    public void test1() throws IOException {
        //
        // our example data stream is just these bytes.
        // In a real system this would open a file or socket or other
        // source of an InputStream.
        //
        InputStream is = new ByteArrayInputStream(Misc.
                hex2Bytes("A4 BB 5A AB CD 40 CA CD".replace(" ", "")));
        application(is);
    }

    @Test
    public void test2() throws IOException {
        //
        // our example data stream is just these bytes.
        // In a real system this would open a file or socket or other
        // source of an InputStream.
        //
        InputStream is = new ByteArrayInputStream(Misc.hex2Bytes("A4BB0A50ABCD40CACD05"));
        application(is);
    }

}
