
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Namespace;
import org.jdom2.filter.ContentFilter;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.XSLTransformException;
import org.jdom2.transform.XSLTransformer;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.UnparseResult;
import org.apache.daffodil.japi.infoset.JDOMInfosetInputter;
import org.apache.daffodil.japi.infoset.JDOMInfosetOutputter;
import org.apache.daffodil.japi.infoset.JsonInfosetOutputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;

/**
 * Demonstrates using the Daffodil DFDL processor to
 * <ul>
 * <li>compile a DFDL schema
 * <li>parse non-XML data into XML,
 * <li>access the data it using XPath,
 * <li>transform the data using XSLT
 * <li>unparse the transformed data back to non-XML form.
 * </ul>
 */
public class HelloWorld {

    public static void main(String[] args) throws IOException, XSLTransformException, URISyntaxException {

        URL schemaFileURL = HelloWorld.class.getResource("/helloWorld.dfdl.xsd");
        URL dataFileURL = HelloWorld.class.getResource("/helloWorld.dat");
        URL xsltFileURL = HelloWorld.class.getResource("/helloWorld.xslt");

        //
        // First compile the DFDL Schema
        //
        Compiler c = Daffodil.compiler();
        ProcessorFactory pf = c.compileSource(schemaFileURL.toURI());

        List<Diagnostic> diags1 = pf.getDiagnostics();
        for (Diagnostic d : diags1) {
            System.err.println(d.getSomeMessage());
        }

        if (pf.isError()) {
            // didn't compile schema. Must be diagnostic of some sort. 
            List<Diagnostic> diags = pf.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(1);
        }

        List<Diagnostic> diags2 = pf.getDiagnostics();
        for (Diagnostic d : diags2) {
            System.err.println(d.getSomeMessage());
        }

        DataProcessor dp = pf.onPath("/");
        if (dp.isError()) {
            // didn't compile schema. Must be diagnostic of some sort.
            List<Diagnostic> diags = dp.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(1);
        }
        //
        // Parse - parse data to XML
        //
        System.out.println("**** Parsing data into XML *****");
        java.io.InputStream is = dataFileURL.openStream();
        InputSourceDataInputStream dis = new InputSourceDataInputStream(is);
        //
        // Setup JDOM outputter
        // 
        JDOMInfosetOutputter outputter = new JDOMInfosetOutputter();

        // Do the parse
        //
        ParseResult res = dp.parse(dis, outputter);

        // Check for errors
        //
        boolean err = res.isError();
        if (err) {
            // didn't parse the data. Must be diagnostic of some sort.
            List<Diagnostic> diags = res.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(2); 
        }
        
        Document doc = outputter.getResult();
        //
        // if we get here, we have a parsed infoset result!
        // Let's print the XML infoset.
        //
        // Note that if we had only wanted this text, we could have used
        // a different outputter to create XML text directly,
        // but below we're going to transform this JDOM tree.
        //
        XMLOutputter xo = new XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
        xo.output(doc, System.out);

        // If all you need to do is parse things to XML, then that's it.

        // Let's display it as JSON also for those that need or prefer JSON
        {
            System.out.println("**** Parsing data into JSON ****");
            java.io.InputStream is2 = dataFileURL.openStream();
            InputSourceDataInputStream dis2 = new InputSourceDataInputStream(is2);
            JsonInfosetOutputter jo = new JsonInfosetOutputter(System.out, true);
            ParseResult res2 = dp.parse(dis2, jo);
            boolean err2 = res2.isError();
            if (err2) {
                // didn't parse the data. Must be diagnostic of some sort.
                List<Diagnostic> diags = res.getDiagnostics();
                for (Diagnostic d : diags) {
                    System.err.println(d.getSomeMessage());
                }
                System.exit(2);
            }
        }
        //
        // XPATH - use it to access the data
        //
        System.out.println("**** Access with XPath *****");

        XPathExpression<Content> xexp = setupXPath("/hw:helloWorld/word[2]/text()");
        List<Content> clist = xexp.evaluate(doc);
        if (clist.size() == 0) {
            System.err.println("XPath produced nothing.");
            System.exit(3);
        }
        Content content = clist.get(0);
        String txt = content.getValue();
        System.out.println(String.format("XPath says we said hello to %s", txt));

        //
        // XSLT - use it to transform the data
        //
        System.out.println("**** Transform with XSLT *****");

        XSLTransformer tr = new XSLTransformer(xsltFileURL.openStream());
        Document doc2 = tr.transform(doc);
        xo.output(doc2, System.out); // display it so we see the change.

        //
        // Unparse back to native format
        //
        
        // If you need to also convert XML back into the native data format
        // you need to "unparse" the infoset back to data.
        //
        // Not all DFDL schemas are setup for unparsing. There are some things
        // you need for unparsing that just don't need to be present in the
        // schema if you only intend to do parsing.
        //
        // But let's assume your DFDL schema is one that is able to be used both
        // for parsing and unparsing data.
        //
        // So let's try unparsing
        //
        // We'll just store the result of unparsing into this
        // ByteArrayOutputStream.
        //
        System.out.println("**** Unparsing XML infoset back into data *****");

        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        java.nio.channels.WritableByteChannel wbc = java.nio.channels.Channels.newChannel(bos);
        JDOMInfosetInputter inputter = new JDOMInfosetInputter(doc2);
        UnparseResult res2 = dp.unparse(inputter, wbc);
        err = res2.isError();

        if (err) {
            // didn't unparse. Must be diagnostic of some sort.
            List<Diagnostic> diags = res2.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(3);
        }

        // if we get here, unparsing was successful.
        // The bytes that have been output are in bos
        byte[] ba = bos.toByteArray();

        //
        // Display the resulting data, as text (iso-8859-1), and hex
        //

        // If your data format was textual, then you can print it out as text
        // but we need to know what the text encoding was
        String encoding = "iso-8859-1"; // an encoding where every byte value is
                                        // a legal character.

        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(ba);
        java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(bis, encoding));
        String line;
        System.out.println("Data as text in encoding " + encoding);
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }

        // If your data format was binary, then you can print it out as hex
        // just to get a look at it.
        System.out.println("Data as hex");
        for (byte b : ba) {
            int bi = b; // b could be negative, but we want the hex to look like
                        // it was unsigned.
            bi = bi & 0xFF;
            System.out.print(String.format("%02X ", bi));
        }
        System.out.println("");

    }

    /**
     * Does the boilerplate stuff needed for xpath expression setup
     * 
     * @return the compiled XPathExpression object which can be evaluated to run
     *         it.
     */
    private static XPathExpression<Content> setupXPath(String xpath) {
        // Need this namespace definition since the schema defines the root
        // element in this namespace.
        //
        // A real application would hoist this boilerplate all out so it's done
        // once, not each time we need to evaluate an XPath expression.
        //
        Namespace[] nss = { Namespace.getNamespace("hw", "http://example.com/dfdl/helloworld/") };
        XPathFactory xfactory = XPathFactory.instance();
        ContentFilter cf = new ContentFilter(ContentFilter.TEXT);
        Map<String, Object> variables = Collections.emptyMap();
        XPathExpression<Content> xexp = xfactory.compile(xpath, cf, variables, nss);
        return xexp;
    }
}
