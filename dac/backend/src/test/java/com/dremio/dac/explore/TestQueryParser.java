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
package com.dremio.dac.explore;

import java.util.Arrays;
import java.util.List;

import org.apache.calcite.sql.SqlIdentifier;
import org.junit.Assert;
import org.junit.Test;

import com.dremio.dac.server.BaseTestServer;
import com.dremio.exec.server.SabotContext;
import com.dremio.service.jobs.SqlQuery;
import com.dremio.service.jobs.metadata.QueryMetadata;

/**
 * verify we understand queries
 */
public class TestQueryParser extends BaseTestServer {

  private static String table = "\"cp\".\"tpch/supplier.parquet\"";

  public void validateAncestors(String sql, String... ancestors) {
    QueryMetadata metadata = QueryParser.extract(new SqlQuery(sql, null, DEFAULT_USERNAME), l(SabotContext.class));
    List<SqlIdentifier> actualAncestors = metadata.getAncestors().get();
    String message = "expected: " + Arrays.toString(ancestors) + " actual: " + actualAncestors;
    Assert.assertEquals(message, ancestors.length, actualAncestors.size());
    for (int i = 0; i < ancestors.length; i++) {
      String expectedAncestor = ancestors[i];
      String actualAncestor = actualAncestors.get(i).toString();
      Assert.assertEquals(message, expectedAncestor, actualAncestor);
    }
  }

  @Test
  public void testStar() {
    validateAncestors("select * from " + table, "cp.tpch/supplier.parquet");
  }

  @Test
  public void testFields() {
    validateAncestors("select s_suppkey, s_name, s_address from " + table, "cp.tpch/supplier.parquet");
  }

  @Test
  public void testFieldsAs() {
    validateAncestors("select s_suppkey, s_name as mycol from " + table, "cp.tpch/supplier.parquet");
  }

  @Test
  public void testFlatten() {
    validateAncestors("select flatten(b), a as mycol from cp.\"json/nested.json\"", "cp.json/nested.json");
  }

  @Test
  public void testOrder() {
    validateAncestors("select a from cp.\"json/nested.json\" order by a", "cp.json/nested.json");
  }

  @Test
  public void testMultipleOrder() {
    validateAncestors("select a, b from cp.\"json/nested.json\" order by b desc, a asc", "cp.json/nested.json");
  }

  @Test
  public void testOrderAlias() {
    validateAncestors("select a as b from cp.\"json/nested.json\" order by b", "cp.json/nested.json");
  }

  @Test
  public void testJoin() {
    validateAncestors("select foo.a from cp.\"json/nested.json\" foo, \"cp\".\"tpch/supplier.parquet\" bar where foo.a = bar.s_suppkey",
        "cp.json/nested.json", "cp.tpch/supplier.parquet");
    validateAncestors("select foo.a from cp.\"json/nested.json\" foo, \"cp\".\"tpch/supplier.parquet\" bar, \"cp\".\"tpch/customer.parquet\" baz where foo.a = bar.s_suppkey and bar.s_suppkey = baz.c_custkey",
        "cp.json/nested.json", "cp.tpch/supplier.parquet", "cp.tpch/customer.parquet");
  }

  @Test
  public void testsubQuery() {
    validateAncestors("select foo.a from (select * from cp.\"json/nested.json\") foo, \"cp\".\"tpch/supplier.parquet\" bar where foo.a = bar.s_suppkey",
        "cp.json/nested.json", "cp.tpch/supplier.parquet");
  }

}
