
import java.io.File;
import java.io.IOException;
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

import edu.illinois.ncsa.daffodil.japi.Daffodil;
import edu.illinois.ncsa.daffodil.japi.DataProcessor;
import edu.illinois.ncsa.daffodil.japi.Diagnostic;
import edu.illinois.ncsa.daffodil.japi.ParseResult;
import edu.illinois.ncsa.daffodil.japi.ProcessorFactory;
import edu.illinois.ncsa.daffodil.japi.UnparseResult;

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

	public static void main(String[] args) throws IOException, XSLTransformException {

		String rootDir = "./";
		String testDir = rootDir
				+ "src/test/resources/";
		String schemaFilePath = testDir + "helloWorld.dfdl.xsd";
		String dataFilePath = testDir + "helloWorld.dat";

		//
		// First compile the DFDL Schema
		//
		edu.illinois.ncsa.daffodil.japi.Compiler c = Daffodil.compiler();
		c.setValidateDFDLSchemas(true); // makes sure the DFDL schema is valid
										// itself.
		File schemaFile = new File(schemaFilePath);
		ProcessorFactory pf = c.compileFile(schemaFile);
		if (pf.isError()) {
			// didn't compile schema. Must be diagnostic of some sort. 
			List<Diagnostic> diags = pf.getDiagnostics();
			for (Diagnostic d : diags) {
				System.err.println(d.getSomeMessage());
			}
			System.exit(1);
		}
		DataProcessor dp = pf.onPath("/");

		//
		// Parse - parse data to XML
		//
		System.out.println("**** Parsing data into XML *****");
		java.io.File file = new File(dataFilePath);
		java.io.FileInputStream fis = new java.io.FileInputStream(file);
		java.nio.channels.ReadableByteChannel rbc = java.nio.channels.Channels.newChannel(fis);
		ParseResult res = dp.parse(rbc);
		boolean err = res.isError();
		if (err) {
			// didn't parse the data. Must be diagnostic of some sort.
			List<Diagnostic> diags = res.getDiagnostics();
			for (Diagnostic d : diags) {
				System.err.println(d.getSomeMessage());
			}
			System.exit(2); 
		}
		//
		// if we get here, we have a parsed infoset result!
		// Let's print the XML infoset.
		//
		Document doc = res.result();
		XMLOutputter xo = new XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
		xo.output(doc, System.out);

		// If all you need to do is parse things to XML, then that's it.

		//
		// XPATH - use it to access the data
		//
		System.out.println("**** Access with XPath *****");

		XPathExpression<Content> xexp = setupXPath("/tns:helloWorld/word[2]/text()");
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

		String xsltFilePath = testDir + "helloWorld.xslt";

		XSLTransformer tr = new XSLTransformer(xsltFilePath);
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
		UnparseResult res2 = dp.unparse(wbc, doc2);
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
		Namespace[] nss = { Namespace.getNamespace("tns", "http://example.com/dfdl/helloworld/") };
		XPathFactory xfactory = XPathFactory.instance();
		ContentFilter cf = new ContentFilter(ContentFilter.TEXT);
		Map<String, Object> variables = Collections.emptyMap();
		XPathExpression<Content> xexp = xfactory.compile(xpath, cf, variables, nss);
		return xexp;
	}
}
