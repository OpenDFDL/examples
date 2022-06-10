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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.daffodil.japi.Compiler;
import org.apache.daffodil.japi.Daffodil;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.InvalidUsageException;
import org.apache.daffodil.japi.ParseResult;
import org.apache.daffodil.japi.ProcessorFactory;
import org.apache.daffodil.japi.infoset.InfosetOutputter;
import org.apache.daffodil.japi.infoset.JsonInfosetOutputter;
import org.apache.daffodil.japi.io.InputSourceDataInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Transforms messages using the {@link Daffodil} DFDL processor.
 *
 * <p>The {@link Daffodil} object is a factory object to create a {@link Compiler}. The Compiler
 * provides a method to compile a provided DFDL schema into a {@link ProcessorFactory}, which
 * creates a {@link DataProcessor}.
 */
@Service
public class DfdlService {

  @Value("${debugger.usage:true}")
  boolean debuggerUsage;

  @Autowired
  private ProcessorDataService processorDataService;

  /**
   * Creates {@link DataProcessor} and parses an event message.
   */
  public String convertDataMessage(String message, DfdlDef dfdl)
      throws IOException, InvalidUsageException {

    Processor processor = new Processor(dfdl.name, dfdl.definition);
    DataProcessor dataProcessor = processorDataService.getDataProcessor(processor);

    // Result in a json format
    return jsonMessageResult(message, dataProcessor);
  }

  private String jsonMessageResult(String message, DataProcessor dataProcessor) {
    System.out.println("Result Message JSON: ");
    InputSourceDataInputStream messageInputStreamJson = getMessageInputStream(message);
    // Set the JSON outputter
    OutputStream outputStream = new ByteArrayOutputStream();
    JsonInfosetOutputter jsonOutputter = new JsonInfosetOutputter(outputStream, true);
    // Parsing
    parse(dataProcessor, messageInputStreamJson, jsonOutputter);
    System.out.println(outputStream);
    return outputStream.toString();
  }

  /**
   * Accepts input data to parse in the form of a InputSourceDataInputStream and an InfosetOutputter
   * to determine the output representation of the infoset (e.g. Json, XML)
   *
   * @param dataProcessor provides the necessary functions to parse and unparse data.
   * @return ParseResult that contains information about the parse, such as whether or not the
   * processing succeeded any diagnostic information.
   */
  private ParseResult parse(
      DataProcessor dataProcessor,
      InputSourceDataInputStream inputStream,
      InfosetOutputter infosetOutputter) {
    // The DataProcessor.parse method is thread-safe and may be called multiple times without the
    // need to create other data processors. However, InfosetOutputter's are not thread safe,
    // requiring a unique instance per thread. An InfosetOutputter should call
    // InfosetOutputter.reset before reuse (or a new one should be allocated).
    return dataProcessor.parse(inputStream, infosetOutputter);
  }

  private InputSourceDataInputStream getMessageInputStream(String message) {
    InputStream inputStream = new ByteArrayInputStream(messageToByteArray(message));
    return new InputSourceDataInputStream(inputStream);
  }

  /**
   * Accepts string message as a representation of Hexdump without any spaces or newlines to return
   * its equivalent byte array
   *
   * @param message string input to be converted.
   * @return byte[] that contains binary data for each hex value present in the message string.
   */
  private byte[] messageToByteArray(String message) {
    int messageLength = message.length();
    byte[] convertedMessage = new byte[messageLength / 2];
    for (int currentIndex = 0; currentIndex < messageLength; currentIndex += 2) {
      convertedMessage[currentIndex / 2] =
          (byte)
              ((Character.digit(message.charAt(currentIndex), 16) << 4)
                  + Character.digit(message.charAt(currentIndex + 1), 16));
    }
    return convertedMessage;
  }
}