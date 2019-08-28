/*
 * Copyright (C) 2017-2019 Dremio Corporation
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
package com.dremio.exec.store.dfs.implicit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.arrow.memory.OutOfMemoryException;
import org.apache.arrow.vector.ValueVector;

import com.dremio.common.AutoCloseables;
import com.dremio.common.exceptions.ExecutionSetupException;
import com.dremio.common.exceptions.UserException;
import com.dremio.exec.store.RecordReader;
import com.dremio.sabot.op.scan.OutputMutator;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class AdditionalColumnsRecordReader implements RecordReader {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AdditionalColumnsRecordReader.class);

  private final RecordReader inner;
  private final Populator[] populators;

  public AdditionalColumnsRecordReader(RecordReader inner, List<NameValuePair<?>> pairs) {
    super();
    this.inner = inner;
    this.populators = new Populator[pairs.size()];
    for(int i = 0; i < pairs.size(); i++){
      populators[i] = pairs.get(i).createPopulator();
    }
  }

  AdditionalColumnsRecordReader(RecordReader inner, Populator[] populators){
    this.inner = inner;
    this.populators  = populators;
  }

  @Override
  public void close() throws Exception {
    AutoCloseables.close(Iterables.concat(Arrays.asList(populators), Collections.singleton(inner)));
  }

  @Override
  public void setup(OutputMutator output) throws ExecutionSetupException {
    inner.setup(output);
    for(Populator p : populators){
      p.setup(output);
    }
  }

  @Override
  public void allocate(Map<String, ValueVector> vectorMap) throws OutOfMemoryException {
    inner.allocate(vectorMap);
    for(Populator p : populators){
      p.allocate();
    }
  }

  @Override
  public int next() {
    final int count = inner.next();
    try {
      for (Populator p : populators) {
        p.populate(count);
      }
    } catch (Throwable t) {
      throw userExceptionWithDiagnosticInfo(t, count);
    }
    return count;
  }

  private UserException userExceptionWithDiagnosticInfo(final Throwable t, final int count) {
    return UserException.dataReadError(t)
        .message("Failed to populate partition column values")
        .addContext("Partition value characteristics", populators != null ? Joiner.on(",").join(populators) : "null")
        .addContext("Number of rows trying to populate", count)
        .build(logger);
  }

  public interface Populator extends AutoCloseable {
    void setup(OutputMutator output);
    void populate(final int count);
    void allocate();
  }
}
