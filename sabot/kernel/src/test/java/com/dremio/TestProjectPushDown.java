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

package com.dremio;

import org.junit.Ignore;
import org.junit.Test;

// Test the optimizer plan in terms of project pushdown.
// When a query refers to a subset of columns in a table, optimizer should push the list
// of refereed columns to the SCAN operator, so that SCAN operator would only retrieve
// the column values in the subset of columns.

public class TestProjectPushDown extends PlanTestBase {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
      .getLogger(TestProjectPushDown.class);

  @Test
  public void testGroupBy() throws Exception {
    String expectedColNames = expectedColumnsString("marital_status");
    testPhysicalPlan(
        "select marital_status, COUNT(1) as cnt from cp.\"employee.json\" group by marital_status",
        expectedColNames);
  }

  @Test
  public void testOrderBy() throws Exception {
    String expectedColNames = expectedColumnsString("employee_id", "full_name", "first_name", "last_name");
    testPhysicalPlan("select employee_id , full_name, first_name , last_name "
        + "from cp.\"employee.json\" order by first_name, last_name",
        expectedColNames);
  }

  @Test
  public void testExprInSelect() throws Exception {
    String expectedColNames = expectedColumnsString("employee_id", "full_name", "first_name", "last_name");
    testPhysicalPlan(
        "select employee_id + 100, full_name, first_name , last_name "
            + "from cp.\"employee.json\" order by first_name, last_name",
            expectedColNames);
  }

  @Test
  public void testExprInWhere() throws Exception {
    String expectedColNames = expectedColumnsString("employee_id", "full_name", "first_name", "last_name");
    testPhysicalPlan(
        "select employee_id + 100, full_name, first_name , last_name "
            + "from cp.\"employee.json\" where employee_id + 500 < 1000 ",
            expectedColNames);
  }

  @Test
  public void testJoin() throws Exception {
    String expectedColNames1 = expectedColumnsString("N_NAME", "N_REGIONKEY");
    String expectedColNames2 = expectedColumnsString("R_REGIONKEY", "R_NAME");

    testPhysicalPlan("SELECT\n" + "  nations.N_NAME,\n" + "  regions.R_NAME\n"
        + "FROM\n"
        + "  dfs.\"[WORKING_PATH]/../../sample-data/nation.parquet\" nations\n"
        + "JOIN\n"
        + "  dfs.\"[WORKING_PATH]/../../sample-data/region.parquet\" regions\n"
        + "  on nations.N_REGIONKEY = regions.R_REGIONKEY", expectedColNames1,
        expectedColNames2);
  }

  @Test
  @Ignore  // InfoSchema do not support project pushdown currently.
  public void testFromInfoSchema() throws Exception {
    String expectedColNames = " \"columns\" : [ \"`CATALOG_DESCRIPTION`\" ]";
    testPhysicalPlan(
        "select count(CATALOG_DESCRIPTION) from INFORMATION_SCHEMA.CATALOGS",
        expectedColNames);
  }

  @Test
  public void testTPCH1() throws Exception {
    String expectedColNames = expectedColumnsString("l_quantity", "l_extendedprice", "l_discount", "l_tax", "l_returnflag", "l_linestatus", "l_shipdate");
    testPhysicalPlanFromFile("queries/tpch/01.sql", expectedColNames);
  }

  @Test
  public void testTPCH3() throws Exception {
    String expectedColNames1 = expectedColumnsString("c_custkey", "c_mktsegment");
    String expectedColNames2 = expectedColumnsString("o_orderkey", "o_custkey", "o_orderdate", "o_shippriority");
    String expectedColNames3 = expectedColumnsString("l_orderkey", "l_extendedprice", "l_discount", "l_shipdate");
    testPhysicalPlanFromFile("queries/tpch/03.sql", expectedColNames1, expectedColNames2, expectedColNames3);
  }

  private final String[] MORE_TABLES = new String[] { "project/pushdown/fields.json"};

  @Test
  public void testProjectPushDown() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t";
    final String projection = "t.trans_id, t.user_info.cust_id, t.marketing_info.keywords[0]";
    final String expected = "columns=[`trans_id`, `user_info`.`cust_id`, `marketing_info`.`keywords`[0]]";

    for (String table: MORE_TABLES) {
      testPushDown(new PushDownTestInstance(pushDownSqlPattern, expected, projection, table));
    }
  }

  @Ignore("DX-11163")
  @Test
  public void testProjectPastFilterPushDown() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t where %s";
    final String projection = "t.trans_id, t.user_info.cust_id, t.marketing_info.keywords[0]";
    final String filter = "t.another_field = 10 and t.columns[0] = 100 and t.columns[1] = t.other.columns[2]";
    final String expected = "columns=[`trans_id`, `another_field`, `user_info`.`cust_id`, `marketing_info`.`keywords`[0], `columns`[0], `columns`[1], `other`.`columns`[2]]";

    for (String table: MORE_TABLES) {
      testPushDown(new PushDownTestInstance(pushDownSqlPattern, expected, projection, table, filter));
    }
  }

  @Ignore("DX-11163")
  @Test
  public void testProjectPastJoinPushDown() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t0, cp.\"%s\" t1 where %s";
    final String projection = "t0.fcolumns[0], t0.fmy.field, t0.freally.nested.field[0], t1.scolumns[0], t1.smy.field, t1.sreally.nested.field[0]";
    final String filter = "t0.fname = t1.sname and t0.fcolumns[1]=10 and t1.scolumns[1]=100";
    final String firstExpected = "columns=[`fname`, `fcolumns`[0], `fmy`.`field`, `freally`.`nested`.`field`[0], `fcolumns`[1]]";
    final String secondExpected = "columns=[`sname`, `scolumns`[0], `smy`.`field`, `sreally`.`nested`.`field`[0], `scolumns`[1]]";

    for (String table: MORE_TABLES) {
      testPushDown(new PushDownTestInstance(pushDownSqlPattern, new String[]{firstExpected, secondExpected},
          projection, table, table, filter));
    }
  }

  @Ignore("DX-11163")
  @Test
  public void testProjectPastFilterPastJoinPushDown() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t0, cp.\"%s\" t1 where %s";
    final String projection = "t0.fcolumns[0], t0.fmy.field, t0.freally.nested.field[0], t1.scolumns[0], t1.smy.field, t1.sreally.nested.field[0]";
    final String filter = "t0.fname = t1.sname and t0.fcolumns[1] + t1.scolumns[1]=100";
    final String firstExpected = "columns=[`fname`, `fcolumns`[0], `fmy`.`field`, `freally`.`nested`.`field`[0], `fcolumns`[1]]";
    final String secondExpected = "columns=[`sname`, `scolumns`[0], `smy`.`field`, `sreally`.`nested`.`field`[0], `scolumns`[1]]";

    for (String table: MORE_TABLES) {
      testPushDown(new PushDownTestInstance(pushDownSqlPattern, new String[]{firstExpected, secondExpected},
          projection, table, table, filter));
    }
  }

  @Ignore("DX-11163")
  @Test
  public void testProjectPastFilterPastJoinPushDownWhenItemsAreWithinNestedOperators() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t0, cp.\"%s\" t1 where %s";
    final String projection = "concat(t0.fcolumns[0], concat(t1.scolumns[0], t0.fmy.field, t0.freally.nested.field[0], t1.smy.field, t1.sreally.nested.field[0]))";
    final String filter = "t0.fname = t1.sname and t0.fcolumns[1] + t1.scolumns[1]=100";
    final String firstExpected = "columns=[`fname`, `fcolumns`[0], `fmy`.`field`, `freally`.`nested`.`field`[0], `fcolumns`[1]]";
    final String secondExpected = "columns=[`sname`, `scolumns`[0], `smy`.`field`, `sreally`.`nested`.`field`[0], `scolumns`[1]]";

    for (String table: MORE_TABLES) {
      testPushDown(new PushDownTestInstance(pushDownSqlPattern, new String[]{firstExpected, secondExpected},
          projection, table, table, filter));
    }
  }

  @Ignore("DX-11163")
  @Test
  public void testProjectPastFilterPastJoinPastJoinPushDown() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t0, cp.\"%s\" t1, cp.\"%s\" t2 where %s";
    final String projection = "t0.fcolumns[0], t0.fmy.field, t0.freally.nested.field[0], t1.scolumns[0], t1.smy.field, t1.sreally.nested.field[0], t2.tcolumns[0], t2.tmy.field, t2.treally.nested.field[0]";
    final String filter = "t0.fname = t1.sname and t1.slastname = t2.tlastname and t0.fcolumns[1] + t1.scolumns[1] + t2.tcolumns[1]=100";
    final String firstExpected = "columns=[`fname`, `fcolumns`[0], `fmy`.`field`, `freally`.`nested`.`field`[0], `fcolumns`[1]]";
    final String secondExpected = "columns=[`sname`, `slastname`, `scolumns`[0], `smy`.`field`, `sreally`.`nested`.`field`[0], `scolumns`[1]]";
    final String thirdExpected = "columns=[`tlastname`, `tcolumns`[0], `tmy`.`field`, `treally`.`nested`.`field`[0], `tcolumns`[1]]";

    for (String table: MORE_TABLES) {
      testPushDown(new PushDownTestInstance(pushDownSqlPattern,
          new String[]{firstExpected, secondExpected, thirdExpected}, projection, table, table, table, filter));
    }
  }

  @Ignore("DX-11163")
  @Test
  public void testProjectPastJoinPastFilterPastJoinPushDown() throws Exception {
    final String pushDownSqlPattern = "select %s from cp.\"%s\" t0, cp.\"%s\" t1, cp.\"%s\" t2 where %s";
    final String projection = "t0.fcolumns[0], t0.fmy.field, t0.freally.nested.field[0], t1.scolumns[0], t1.smy.field, t1.sreally.nested.field[0], t2.tcolumns[0], t2.tmy.field, t2.treally.nested.field[0]";
    final String filter = "t0.fname = t1.sname and t1.slastname = t2.tlastname and t0.fcolumns[1] + t1.scolumns[1] = 100";
    final String firstExpected = "columns=[`fname`, `fcolumns`[0], `fmy`.`field`, `freally`.`nested`.`field`[0], `fcolumns`[1]]";
    final String secondExpected = "columns=[`sname`, `slastname`, `scolumns`[0], `smy`.`field`, `sreally`.`nested`.`field`[0], `scolumns`[1]]";
    final String thirdExpected = "columns=[`tlastname`, `tcolumns`[0], `tmy`.`field`, `treally`.`nested`.`field`[0]]";

    for (String table: MORE_TABLES) {
    testPushDown(new PushDownTestInstance(pushDownSqlPattern,
        new String[]{firstExpected, secondExpected, thirdExpected}, projection, table, table, table, filter));
    }

  }

  @Test
  public void testEmptyColProjectInTextScan() throws Exception {
    final String sql = "SELECT count(*) cnt from cp.\"store/text/data/d1/regions.csv\"";
    final String expected = expectedColumnsString();
    // Verify plan
    testPushDown(new PushDownTestInstance(sql, new String[] {expected}));

    // Verify execution result.
    testBuilder()
        .sqlQuery(sql)
        .unOrdered()
        .baselineColumns("cnt")
        .baselineValues((long) 5)
        .build()
        .run();
  }

  @Test
  public void testEmptyColProjectInJsonScan() throws Exception {
    final String sql = "SELECT count(*) cnt from cp.\"employee.json\"";
    final String expected = expectedColumnsString();

    testPushDown(new PushDownTestInstance(sql, new String[] {expected}));

    // Verify execution result.
    testBuilder()
        .sqlQuery(sql)
        .unOrdered()
        .baselineColumns("cnt")
        .baselineValues((long) 1155)
        .build()
        .run();
  }

  @Test
  public void testEmptyColProjectInParquetScan() throws Exception {
    final String sql = "SELECT 1 + 1 as val from cp.\"tpch/region.parquet\"";
    final String expected = expectedColumnsString();

    testPushDown(new PushDownTestInstance(sql, new String[] {expected}));

    // Verify execution result.
    testBuilder()
        .sqlQuery(sql)
        .unOrdered()
        .baselineColumns("val")
        .baselineValues(2)
        .baselineValues(2)
        .baselineValues(2)
        .baselineValues(2)
        .baselineValues(2)
        .build()
        .run();
  }

  @Ignore("DX-11163")
  @Test
  public void testSimpleProjectPastJoinPastFilterPastJoinPushDown() throws Exception {
//    String sql = "select * " +
//        "from cp.\"%s\" t0, cp.\"%s\" t1, cp.\"%s\" t2 " +
//        "where t0.fname = t1.sname and t1.slastname = t2.tlastname and t0.fcolumns[0] + t1.scolumns = 100";

    final String firstExpected = "columns=[`a`, `fa`, `fcolumns`[0]]";
    final String secondExpected = expectedColumnsString("a", "b", "c", "sa");
    final String thirdExpected = expectedColumnsString("d", "ta");

    String sql = "select t0.fa, t1.sa, t2.ta " +
        " from cp.\"%s\" t0, cp.\"%s\" t1, cp.\"%s\" t2 " +
        " where t0.a=t1.b and t1.c=t2.d and t0.fcolumns[0] + t1.a = 100";
    for (String table: MORE_TABLES) {
    testPushDown(new PushDownTestInstance(sql,
        new String[]{firstExpected, secondExpected, thirdExpected}, table,table,table));
    }
  }

  protected void testPushDown(PushDownTestInstance test) throws Exception {
    testPhysicalPlan(test.getSql(), test.getExpected());
  }

  private void testPhysicalPlanFromFile(String fileName, String... expectedSubstrs)
      throws Exception {
    String query = getFile(fileName);
    String[] queries = query.split(";");
    for (String q : queries) {
      if (q.trim().isEmpty()) {
        continue;
      }
      testPhysicalPlan(q, expectedSubstrs);
    }
  }

  protected static class PushDownTestInstance {
    private final String sqlPattern;
    private final String[] expected;
    private final Object[] params;

    public PushDownTestInstance(String sqlPattern, String expected, Object... params) {
      this(sqlPattern, new String[]{expected}, params);
    }

    public PushDownTestInstance(String sqlPattern, String[] expected, Object... params) {
      this.sqlPattern = sqlPattern;
      this.expected = expected;
      this.params = params;
    }

    public String[] getExpected() {
      return expected;
    }

    public String getSql() {
      return String.format(sqlPattern, params);
    }
  }

}
