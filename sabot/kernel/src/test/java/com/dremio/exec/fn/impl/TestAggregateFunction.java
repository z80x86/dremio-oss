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
package com.dremio.exec.fn.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.arrow.vector.ValueVector;
import org.junit.Ignore;
import org.junit.Test;

import com.dremio.BaseTestQuery;
import com.dremio.common.util.FileUtils;
import com.dremio.exec.proto.UserBitShared.QueryType;
import com.dremio.exec.record.RecordBatchLoader;
import com.dremio.exec.record.VectorWrapper;
import com.dremio.sabot.rpc.user.QueryDataBatch;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

@Ignore("DX-3872")
public class TestAggregateFunction extends BaseTestQuery {

  public void runTest(Object[] values, String planPath, String dataPath) throws Throwable {

    List<QueryDataBatch> results = client.runQuery(QueryType.PHYSICAL,
        Files.toString(FileUtils.getResourceAsFile(planPath), Charsets.UTF_8).replace("#{TEST_FILE}", dataPath));
    try (RecordBatchLoader batchLoader = new RecordBatchLoader(nodes[0].getContext().getAllocator())) {
      QueryDataBatch batch = results.get(1);
      assertTrue(batchLoader.load(batch.getHeader().getDef(), batch.getData()));

      int i = 0;
      for (VectorWrapper<?> v : batchLoader) {
        ValueVector vv = v.getValueVector();
        assertEquals(values[i++], (vv.getObject(0)));
      }

    }

    for (QueryDataBatch b : results) {
      b.release();
    }
  }

  @Test
  public void testSortDate() throws Throwable {
    String planPath = "/functions/test_stddev_variance.json";
    String dataPath = "/simple_stddev_variance_input.json";
    Double expectedValues[] = {2.0d, 2.138089935299395d, 2.138089935299395d, 4.0d, 4.571428571428571d, 4.571428571428571d};

    runTest(expectedValues, planPath, dataPath);
  }

  @Test
  public void testCovarianceCorrelation() throws Throwable {
    String planPath = "/functions/test_covariance.json";
    String dataPath = "/covariance_input.json";
    Double expectedValues[] = {4.571428571428571d, 4.857142857142857d, -6.000000000000002d, 4.0d , 4.25d, -5.250000000000002d, 1.0d, 0.9274260335029677d, -1.0000000000000004d};

    runTest(expectedValues, planPath, dataPath);
  }

}
