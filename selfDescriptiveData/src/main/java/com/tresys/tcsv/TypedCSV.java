/*
 * Copyright 2019 Tresys Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tresys.tscv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.XSLTransformException;
import org.jdom2.transform.XSLTransformer;

import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.UnparseResult;
import org.apache.daffodil.japi.WithDiagnostics;
import org.apache.daffodil.japi.infoset.JDOMInfosetOutputter;
import org.apache.daffodil.japi.infoset.JDOMInfosetInputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;

/**
 * Demonstrates using Apache Daffodil to parse self-descriptive data
 */
public class TypedCSV {

	// Compiler used to compile DFDL schemas
	private static Compiler c;

	static {
		// Get a compiler instance and enable schema validation of schemas
		c = Daffodil.compiler();
		c.setValidateDFDLSchemas(true);
	}

	// Helper function to loop through any diagnostics from a Daffodil result
	// and print them to stderr
	private static void reportDiagnostics(WithDiagnostics hasDiags) {
		List<Diagnostic> diags = hasDiags.getDiagnostics();
		for (Diagnostic d : diags) {
			System.err.println(d.getSomeMessage());
		}
	}

	// Helper function to compile a DFDL schema into a DataProcessor. If any
	// errors occur, diagnostics are reported and the program will exit.
	private static DataProcessor compileSchema(URI schema) throws IOException {

		// Compile the source into a Daffodil ProcessorFactory
		ProcessorFactory pf = c.compileSource(schema);
		if (pf.isError()) {
			// Schema compilation failed
			reportDiagnostics(pf);
			System.exit(1);
		}

		// Build a Daffodil DataProcessor
		DataProcessor dp = pf.onPath("/");
		if (dp.isError()) {
			// Failed to build the DataProcessor
			reportDiagnostics(dp);
			System.exit(1);
		}

		return dp;
	}

	// Helper function to Write a JDOM Document to an output stream, with an
	// optional label printed first
	private static void outputDocument(String label, Document doc, OutputStream out) throws IOException {
		if (label != null) {
			out.write(("\n========== " + label + " ==========\n").getBytes());
		}
		XMLOutputter xo = new XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
		xo.output(doc, out);
	}

	private static void outputDocument(Document doc, OutputStream out) throws IOException {
		outputDocument(null, doc, out);
	}

	public static void main(String[] args) throws IOException, URISyntaxException, XSLTransformException {

		URL headerDFDLSchema = TypedCSV.class.getResource("/com/tresys/tcsv/xsd/tcsvheader.dfdl.xsd");
		URL headerXSLT = TypedCSV.class.getResource("/com/tresys/tcsv/xslt/transformHeader.xslt");
		URL dataXSLT = TypedCSV.class.getResource("/com/tresys/tcsv/xslt/transformData.xslt");
		URL inputData = TypedCSV.class.getResource("/data/data.tcsv");

		// Compile the header DFDL schema
		DataProcessor headerDP = compileSchema(headerDFDLSchema.toURI());

		// Print the original input data
		System.out.println("\n========== Input Data ==========");
		InputStream origIn = inputData.openStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = origIn.read(buffer)) != -1) {
			System.out.write(buffer, 0, len);
		}

		// Create a Daffodil input stream for the data. The
		// InputSourceDataInputStream saves the position where a parse ends so
		// that another parse can pick up where the previous one left off.
		InputStream fis = inputData.openStream();
		InputSourceDataInputStream dis = new InputSourceDataInputStream(fis);

		// Output the header infoset as a JDOM tree
		JDOMInfosetOutputter headerOutputter = new JDOMInfosetOutputter();

		// Parse just the header of the input data using the tcsvheader schema.
		// After the parse, the position of the InputSourceDataInputStream will be
		// right before the second row, since the header DFDL schema only parses
		// the first row and trailing line separator.
		ParseResult headerPR = headerDP.parse(dis, headerOutputter);
		if (headerPR.isError()) {
			// Failed to parse the header
			reportDiagnostics(headerPR);
			System.exit(1);
		}

		// Get the resulting header XML JDOM document
		Document headerDoc = headerOutputter.getResult();
		outputDocument("Parsed Header Infoset", headerDoc, System.out);

		// Transform the header XML to a new DFDL schema that describes the data
		XSLTransformer headerTR = new XSLTransformer(headerXSLT.openStream());
		Document dataSchemaDoc = headerTR.transform(headerDoc);
		outputDocument("Generated Data Schema", dataSchemaDoc, System.out);

		// Save the generated schema to a temporary location
		File tmpSchema = File.createTempFile("tcsv-", ".dfdl.xsd");
		FileOutputStream fos = new FileOutputStream(tmpSchema);
		outputDocument(null, dataSchemaDoc, fos);
		fos.close();

		// Compile the temporary schema
		URI dataDFDLSchema = tmpSchema.toURI();
		DataProcessor dataDP = compileSchema(dataDFDLSchema);
		tmpSchema.delete();

		// Output the data infoset as a JDOM Document
		JDOMInfosetOutputter dataOutputter = new JDOMInfosetOutputter();

		// Parse the rest of the data using the generated schema, starting
		// where the header parse left off
		ParseResult dataPR = dataDP.parse(dis, dataOutputter);
		if (dataPR.isError()) {
			// Failed to parse the data
			reportDiagnostics(dataPR);
			System.exit(1);
		}

		// Get the resulting JDOM document
		Document dataDoc = dataOutputter.getResult();
		outputDocument("Parsed Data Infoset", dataDoc, System.out);

		// Transform the data XML using an XSLT, performs filtering and sorting
		XSLTransformer dataTR = new XSLTransformer(dataXSLT.openStream());
		Document dataDocSorted = dataTR.transform(dataDoc);
		outputDocument("Transformed Data Infoset", dataDocSorted, System.out);

		System.out.println("\n========== Unparsed Data ==========");

		// Create an output stream for unparsing
		WritableByteChannel output = Channels.newChannel(System.out);

		// Unparse the header JDOM document
		JDOMInfosetInputter headerInputter = new JDOMInfosetInputter(headerDoc);
		UnparseResult headerUR = headerDP.unparse(headerInputter, output);
		if (headerUR.isError()) {
			// Failed to unparse the header
			reportDiagnostics(headerUR);
			System.exit(1);
		}

		// Unparse the data JDOM document
		JDOMInfosetInputter dataInputter = new JDOMInfosetInputter(dataDocSorted);
		UnparseResult dataUR = dataDP.unparse(dataInputter, output);
		if (dataUR.isError()) {
			// Failed to unparse the header
			reportDiagnostics(dataUR);
			System.exit(1);
		}

		System.out.println("\n");

	}

}
