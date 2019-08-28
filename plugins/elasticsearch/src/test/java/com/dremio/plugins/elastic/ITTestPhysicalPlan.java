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
package com.dremio.plugins.elastic;

import static com.dremio.plugins.elastic.ElasticsearchType.BOOLEAN;
import static com.dremio.plugins.elastic.ElasticsearchType.FLOAT;
import static com.dremio.plugins.elastic.ElasticsearchType.INTEGER;
import static com.dremio.plugins.elastic.ElasticsearchType.TEXT;

import org.junit.Test;

import com.dremio.exec.proto.UserBitShared;

public class ITTestPhysicalPlan extends ElasticBaseTestQuery {

  private static final String QUERYPLAN = "./json/physicalplan-test.json";

  @Test
  public void testQuery() throws Exception {

    ElasticsearchCluster.ColumnData[] data = new ElasticsearchCluster.ColumnData[]{
            new ElasticsearchCluster.ColumnData("business_id", TEXT, new Object[][]{
                    {"12345"},
                    {"abcde"},
                    {"7890"}
            }),
            new ElasticsearchCluster.ColumnData("full_address", TEXT, new Object[][]{
                    {"12345 A Street, Cambridge, MA"},
                    {"987 B Street, San Francisco, CA"},
                    {"987 B Street, San Diego, CA"}
            }),
            new ElasticsearchCluster.ColumnData("city", TEXT, new Object[][]{
                    {"Cambridge"},
                    {"SanFrancisco"},
                    {"SanDiego"}
            }),
            new ElasticsearchCluster.ColumnData("state", TEXT, new Object[][]{
                    {"MA"},
                    {"CA"},
                    {"CA"}
            }),
            new ElasticsearchCluster.ColumnData("review_count", INTEGER, new Object[][]{
                    {11},
                    {22},
                    {33}
            }),
            new ElasticsearchCluster.ColumnData("stars", FLOAT, new Object[][]{
                    {4.5},
                    {3.5},
                    {4.9}
            }),
            new ElasticsearchCluster.ColumnData("open", BOOLEAN, new Object[][]{
                    {true},
                    {true},
                    {true}
            }),
            new ElasticsearchCluster.ColumnData("name", TEXT, new Object[][]{
                    {"Store in Cambridge"},
                    {"Store in San Francisco"},
                    {"Store in San Diego"}
            })

    };

    elastic.load(schema, table, data);

    //testPhysicalFromFile(QUERYPLAN);

    String sqlQuery = "explain plan for select state, city, review_count from elasticsearch." + schema + "." + table;
    testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);
    sqlQuery = "select state, city, review_count from elasticsearch." + schema + "." + table + "";
    testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);

    //sqlQuery = "select sum(distinct(review_count)) from elasticsearch." + schema + "." + table + " group by full_address";
    //testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);
        /*
        String sqlQuery = "select sum(review_count) from elasticsearch." + schema + "." + table + " where stars >= 4 or review_count < 100";
        testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);
        sqlQuery = "select sum(review_count) from elasticsearch." + schema + "." + table + " where stars >= 4 and review_count < 100";
        testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);
        sqlQuery = "select sum(review_count) from elasticsearch." + schema + "." + table + " where (stars >= 4 and review_count < 100) or review_count >= 100";
        testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);
        sqlQuery = "select sum(review_count) from elasticsearch." + schema + "." + table + " where (stars >= 4 and review_count < 100) or business_id = '12345'";
        testRunAndPrint(UserBitShared.QueryType.SQL, sqlQuery);
    */
  }
}
