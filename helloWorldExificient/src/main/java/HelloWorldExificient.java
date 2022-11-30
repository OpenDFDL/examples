/*
 * Copyright 2022 Owl Cyber Defense
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.exceptions.UnsupportedOption;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.EXISource;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.CodingMode;

import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.UnparseResult;
import org.apache.daffodil.japi.DaffodilParseXMLReader;
import org.apache.daffodil.japi.DaffodilUnparseContentHandler;
import org.apache.daffodil.japi.DaffodilUnparseErrorSAXException;
import org.apache.daffodil.japi.DaffodilXMLEntityResolver;
import org.apache.daffodil.japi.infoset.JDOMInfosetInputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;

/**
 * Demonstrates using the Daffodil DFDL processor to
 * <ul>
 * <li>compile a DFDL schema
 * <li>parse non-XML data into EXI,
 * <li>transform the data using XSLT
 * <li>unparse the transformed data back to non-EXI form.
 * </ul>
 */

class ParseUnparseException extends Exception {
    public ParseUnparseException(String errorMessage) {
        super(errorMessage);
    }
}

public class HelloWorldExificient {

    public static void main(String[] args) throws IOException, URISyntaxException {

    URL schemaFileURL = HelloWorldExificient.class.getResource("/helloWorld.dfdl.xsd");
    URL dataFileURL = HelloWorldExificient.class.getResource("/helloWorld.dat");
    URL xsltFileURL = HelloWorldExificient.class.getResource("/helloWorld.xslt");

    boolean fatalError = false;

    //
    // Print out original data as text and hex
    //
    String encoding = "iso-8859-1"; // an encoding where every byte value is
                    // a legal character.

    InputStream originalData = dataFileURL.openStream();
    byte[] ba = new byte[originalData.available()];
    originalData.read(ba, 0, originalData.available());
    originalData.close();

    ByteArrayInputStream baStream = new ByteArrayInputStream(ba);
    java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(baStream, encoding));
    String line;
    System.out.println("Original data as text in encoding " + encoding + ":");
    while ((line = r.readLine()) != null) {
        System.out.println(line);
    }

    // If your data format was binary, then you can print it out as hex
    // just to get a look at it.
    System.out.println("Original data as hex:");
    for (byte b : ba) {
        int bi = b; // b could be negative, but we want the hex to look like
            // it was unsigned.
        bi = bi & 0xFF;
        System.out.print(String.format("%02X ", bi));
    }
    System.out.println("");

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
        // didn't get a DataProcessor. Must be diagnostic of some sort.
        List<Diagnostic> diags = dp.getDiagnostics();
        for (Diagnostic d : diags) {
        System.err.println(d.getSomeMessage());
        }
        System.exit(1);
    }

    //
    // Setup Exificient
    //
    EXIFactory exiFactory = DefaultEXIFactory.newInstance();
    try {
        // Include information about how the EXI is encoded in the EXI file,
        // allowing for interop between different implementations of EXI.
        // This is also where you can set other options, such as
        // compression, entity reference handling, and XSI:nil handling
        exiFactory.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);
        // Enable compression
        exiFactory.setCodingMode(CodingMode.COMPRESSION);
    } catch (UnsupportedOption u) {
        System.err.println(u.getMessage());
        System.exit(1);
    }

    try {
        GrammarFactory grammarFactory = GrammarFactory.newInstance();
        Grammars grammar = grammarFactory.createGrammars(schemaFileURL.toString(), DaffodilXMLEntityResolver.getXMLEntityResolver());
        exiFactory.setGrammars(grammar);
    } catch (EXIException e) {
        System.err.println("Error creating EXI grammar for schema aware encoding: " + e.getMessage());
        System.exit(1);
    }

    //
    // Parse - parse data to EXI
    //
    System.out.println("**** Parsing data into EXI *****");
    InputStream parseIs = dataFileURL.openStream();
    ByteArrayOutputStream parseOs = new ByteArrayOutputStream();

    try {
        parseToEXI(dp, exiFactory, parseIs, parseOs);
    } catch (EXIException e) {
        System.err.println("Error creating EXIResult: " + e.getMessage());
        fatalError = true;
    } catch (ParseUnparseException p) {
        // We have already printed out the error messages in parseToEXI
        fatalError = true;
    } finally {
        parseOs.close();
        parseIs.close();
        if (fatalError)
            System.exit(1);
    }

    byte[] exiBytes = parseOs.toByteArray();
    System.out.println("Parsed EXI data as hex:");
    for (byte b : exiBytes) {
        int bi = b; // b could be negative, but we want the hex to look like
            // it was unsigned.
        bi = bi & 0xFF;
        System.out.print(String.format("%02X ", bi));
    }
    System.out.println("");

    //
    // XSLT - use it to transform the data
    //
    System.out.println("**** Transform with XSLT *****");

    // Setting up XSLT transform to consume SAX events from the EXI data
    // and to produce SAX events for the transform result, which will be
    // converted directly into schema-aware EXI, menaning that there is
    // never any XML text being created or used.
    ByteArrayInputStream transformIs = new ByteArrayInputStream(exiBytes);
    ByteArrayOutputStream transformOs = new ByteArrayOutputStream();
    try {
        transformEXI(exiFactory, transformIs, transformOs, xsltFileURL);
    } catch (EXIException e) {
        System.err.println("Error creating EXISource: " + e.getMessage());
        fatalError = true;
    } catch (TransformerException t) {
        System.err.println("Error transforming EXI infoset: " + t.getMessage());
        fatalError = true;
    } finally {
        transformIs.close();
        transformOs.close();
        if (fatalError)
            System.exit(1);
    }

    // If you need to also convert XML back into the native data format
    // you need to "unparse" the infoset back to data.
    //
    // So let's try unparsing
    //
    // We'll just store the result of unparsing into this
    // ByteArrayOutputStream.
    //
    System.out.println("**** Unparsing XML infoset back into data *****");

    //
    // Unparse back to native format
    //
    ByteArrayInputStream unparseIs = new ByteArrayInputStream(transformOs.toByteArray());
    ByteArrayOutputStream unparseOs = new ByteArrayOutputStream();
    try {
        unparseFromEXI(dp, exiFactory, unparseIs, unparseOs);
    } catch (EXIException e) {
        System.err.println("Error creating unparse EXISource: " + e.getMessage());
        fatalError = true;
    } catch (SAXException s) {
        System.err.println("SAX Error during unparse: " + s.getMessage());
        fatalError = true;
    } catch (ParseUnparseException u) {
        // We have already printed out the error messages in unparseFromEXI
        fatalError = true;
    } finally {
        unparseIs.close();
        unparseOs.close();
        if (fatalError)
            System.exit(1);
    }

    // if we get here, unparsing was successful.
    // The bytes that have been output are in bos
    ba = unparseOs.toByteArray();

    //
    // Display the resulting data, as text (iso-8859-1), and hex
    //
    ByteArrayInputStream data = new ByteArrayInputStream(ba);
    r = new java.io.BufferedReader(new java.io.InputStreamReader(data, encoding));
    System.out.println("Unparsed data as text in encoding " + encoding + ":");
    while ((line = r.readLine()) != null) {
        System.out.println(line);
    }

    // If your data format was binary, then you can print it out as hex
    // just to get a look at it.
    System.out.println("Unparsed data as hex:");
    for (byte b : ba) {
        int bi = b; // b could be negative, but we want the hex to look like
            // it was unsigned.
        bi = bi & 0xFF;
        System.out.print(String.format("%02X ", bi));
    }
    System.out.println("");
    }

    public static void parseToEXI(DataProcessor dp, EXIFactory exiFactory, InputStream is, ByteArrayOutputStream os)
        throws EXIException, IOException, ParseUnparseException {

        InputSourceDataInputStream dis = new InputSourceDataInputStream(is);
        EXIResult exiResult = new EXIResult(exiFactory); // throws EXIException
        exiResult.setOutputStream(os); // throws EXIException and IOException

        DaffodilParseXMLReader reader = dp.newXMLReaderInstance();
        reader.setContentHandler(exiResult.getHandler());

        //
        // Do the parse
        //
        reader.parse(dis);

        // Check for errors
        //
        ParseResult res = (ParseResult) reader.getProperty(DaffodilParseXMLReader.DAFFODIL_SAX_URN_PARSERESULT());
        boolean err = res.isError();
        if (err) {
            // didn't parse the data. Must be diagnostic of some sort.
            List<Diagnostic> diags = res.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            throw new ParseUnparseException("Error parsing input file");
        }
    }

    public static void transformEXI(EXIFactory exiFactory, ByteArrayInputStream is, ByteArrayOutputStream os, URL xslt)
        throws EXIException, TransformerException, IOException {

        TransformerFactory tf = TransformerFactory.newInstance();
        InputStream xsltFileURLStream = xslt.openStream(); // throws EXIException
        StreamSource xsltSource = new StreamSource();
        xsltSource.setInputStream(xsltFileURLStream);
        Transformer tr = tf.newTransformer(xsltSource); // throws TransformerException

        EXISource exiSource = new EXISource(exiFactory); // throws EXIException
        InputSource inputSource = new InputSource(is);
        exiSource.setInputSource(inputSource);
        EXIResult exiResult = new EXIResult(exiFactory); // throws EXIException
        exiResult.setOutputStream(os); // throws EXIException and IOException
        tr.transform(exiSource, exiResult); // throws TransformerException
    }

    public static void unparseFromEXI(DataProcessor dp, EXIFactory exiFactory, ByteArrayInputStream is, ByteArrayOutputStream os)
        throws EXIException, SAXException, IOException, ParseUnparseException {

        java.nio.channels.WritableByteChannel wbc = java.nio.channels.Channels.newChannel(os);
        DaffodilUnparseContentHandler unparseHandler = dp.newContentHandlerInstance(wbc);
        InputSource unparseInputSource = new InputSource(is);

        EXISource exiSource = new EXISource(exiFactory); // throws EXIException and IOException
        XMLReader exiReader = exiSource.getXMLReader();
        // Read the EXI content as a series of SAX events, which will
        // drive the unparsing, converting the data back to its
        // original format
        exiReader.setContentHandler(unparseHandler);
        try {
            exiReader.parse(unparseInputSource); // throws SAXException and IOException
        } catch (DaffodilUnparseErrorSAXException d) {
            // do nothing, the UnparseResult contains the error information
        }

        UnparseResult res2 = unparseHandler.getUnparseResult();
        boolean err = res2.isError();

        if (err) {
            // didn't unparse. Must be diagnostic of some sort.
            List<Diagnostic> diags = res2.getDiagnostics();
            for (Diagnostic d : diags) {
                System.err.println(d.getSomeMessage());
            }
            throw new ParseUnparseException("Error unparsing infoset");
        }
    }
}

