/**
 * Copyright 2022 Google LLC
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dfdl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.Diagnostic;
import org.apache.daffodil.japi.InvalidUsageException;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.ValidationMode;
import org.apache.daffodil.japi.debugger.TraceDebuggerRunner;
import org.springframework.beans.factory.annotation.Value;

public class Processor {

  @Value("${debugger.usage:true}")
  boolean debuggerUsage;

  private String definition;
  private String name;

  public Processor(String name, String definition) {
    this.name = name;
    this.definition = definition;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  DataProcessor getDataProcessor() throws IOException, InvalidUsageException {
    System.out.println("Enter - Data Processor no in processors cached");
    // DFDL definition file
    File schemaFile = createSchemaFile(name, definition);

    // Compile the de DFDL definition or schema file
    Compiler dfdlCompiler = Daffodil.compiler();
    ProcessorFactory processorFactory = dfdlCompiler.compileFile(schemaFile);
    if (processorFactory.isError()) {
      // Error compiling the schema. Printing diagnostic for troubleshooting.
      List<Diagnostic> diags = processorFactory.getDiagnostics();
      for (Diagnostic d : diags) {
        System.err.println(d.getSomeMessage());
      }
      System.exit(1);
    }
    // "/" in the only support path.
    DataProcessor dataProcessor =
        processorFactory
            .onPath("/")
            .withValidationMode(ValidationMode.Off)
            .withDebuggerRunner(new TraceDebuggerRunner())
            .withDebugging(debuggerUsage);
    return dataProcessor;
  }

  private File createSchemaFile(String name, String definition) throws IOException {
    File schemaFile = new File(name);
    FileWriter definitionWriter = new FileWriter(schemaFile);
    definitionWriter.write(definition);
    definitionWriter.close();
    return schemaFile;
  }
}