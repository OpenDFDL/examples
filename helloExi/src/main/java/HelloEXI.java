
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.UnparseResult;
import org.apache.daffodil.japi.DaffodilParseXMLReader;
import org.apache.daffodil.japi.DaffodilUnparseContentHandler;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;
import org.apache.daffodil.japi.DaffodilUnparseErrorSAXException;

import com.agiledelta.efx.EFXProperty;
import com.agiledelta.efx.EFXFactory;
import com.agiledelta.efx.EFXException;
import com.agiledelta.efx.sax.EFXSAXSerializer;

/**
 * Demonstrates using the Daffodil DFDL processor to
 * <ul>
 * <li>compile a DFDL schema
 * <li>parse non-XML data into EXI using Agile Delta and
 * <li>unparse the data back to non-XML form.
 * </ul>
 */
public class HelloEXI {

    public static void main(String[] args) throws IOException, URISyntaxException, EFXException {

        URL schemaFileURL = HelloEXI.class.getResource("/helloWorld.dfdl.xsd");
        URL dataFileURL = HelloEXI.class.getResource("/helloWorld.dat");
        String exiFilePath = "src/main/resources/helloWorld.exi";
        String unpFilePath = "src/main/resources/helloWorld.exi.dat";

        //
        // First compile the DFDL Schema
        //
        Compiler c = Daffodil.compiler();
        ProcessorFactory pf = c.compileSource(schemaFileURL.toURI());
        if (pf.isError()) {
            // didn't compile schema. Must be diagnostic of some sort. 
            List<Diagnostic> diags = pf.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(1);
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
        // Parse - parse data to EXI
        //
        System.out.println("**** Parsing data into EXI *****");
        InputStream is = dataFileURL.openStream();
	OutputStream os = new FileOutputStream(exiFilePath);
        InputSourceDataInputStream dis = new InputSourceDataInputStream(is);

	//
	// Setup Agile Delta EXI content handler
	//
	EFXFactory factory = EFXFactory.newInstance();
	//SchemaResolver resolver = new SchemaResolver() {
	//	public SchemaSource getSchema(String schemaID, String schemaPath, String namespaceURI) throws java.io.IOException {
	//	}
	//};
	////factory.setProperty(EFXProperty.SCHEMA_RESOLVER, resolver);
	//factory.setSchema(schemaId);
	factory.setProperty(EFXProperty.HEADER, true);
	EFXSAXSerializer exiContentHandler = new EFXSAXSerializer(factory, os);
	DaffodilParseXMLReader reader = dp.newXMLReaderInstance();
	reader.setContentHandler(exiContentHandler);
	reader.parse(dis);
	os.close();

	//
        // Check for errors
        //
	ParseResult pr = (ParseResult) reader.getProperty("urn:ogf:dfdl:2013:imp:daffodil.apache.org:2018:sax:ParseResult");
        boolean err = pr.isError();
        if (err) {
            // didn't parse the data. Must be diagnostic of some sort.
            List<Diagnostic> diags = pr.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(2); 
        }

	is.close();
	os.close();
        
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

	is = new FileInputStream(exiFilePath);
        FileOutputStream fos = new FileOutputStream(unpFilePath);
        java.nio.channels.WritableByteChannel wbc = java.nio.channels.Channels.newChannel(fos);
	XMLReader xmlReader = factory.createXMLReader();
	DaffodilUnparseContentHandler handler = dp.newContentHandlerInstance(wbc);
        xmlReader.setContentHandler(handler);
	try {
		xmlReader.parse(new InputSource(is));
	} catch (SAXException ex) {
		// Do nothing, error is detailed in unparse result
	} finally {
		is.close();
		wbc.close();
		fos.close();
	}

	UnparseResult ur = handler.getUnparseResult();
        err = ur.isError();

        if (err) {
            // didn't unparse. Must be diagnostic of some sort.
            List<Diagnostic> diags = ur.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            System.exit(3);
        }
    }
}
