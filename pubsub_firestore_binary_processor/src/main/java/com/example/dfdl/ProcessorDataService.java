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

import java.io.IOException;
import org.apache.daffodil.japi.DataProcessor;
import org.apache.daffodil.japi.InvalidUsageException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProcessorDataService {

  /** Returns a {@link DataProcessor} only if it doesn't find it the cache - processors. */
  @Cacheable(value = "processors", key = "#processor.name")
  public DataProcessor getDataProcessor(Processor processor)
      throws IOException, InvalidUsageException {
    return processor.getDataProcessor();
  }
}