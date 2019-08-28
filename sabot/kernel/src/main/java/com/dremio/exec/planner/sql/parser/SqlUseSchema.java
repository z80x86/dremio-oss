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
package com.dremio.exec.planner.sql.parser;

import java.util.Collections;
import java.util.List;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;

/**
 * Sql parser tree node to represent <code>USE SCHEMA</code> statement.
 */
public class SqlUseSchema extends SqlCall {

  public static final SqlSpecialOperator OPERATOR =
      new SqlSpecialOperator("USE_SCHEMA", SqlKind.OTHER){
    @Override
    public SqlCall createCall(SqlLiteral functionQualifier, SqlParserPos pos, SqlNode... operands) {
      return new SqlUseSchema(pos, (SqlIdentifier) operands[0]);
    }
  };

  private SqlIdentifier schema;

  public SqlUseSchema(SqlParserPos pos, SqlIdentifier schema) {
    super(pos);
    this.schema = schema;
    assert schema != null;
  }

  @Override
  public List<SqlNode> getOperandList() {
    return Collections.singletonList((SqlNode)schema);
  }

  @Override
  public SqlOperator getOperator() {
    return OPERATOR;
  }

  @Override
  public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    writer.keyword("USE");
    schema.unparse(writer, leftPrec, rightPrec);
  }

  public List<String> getSchema() {
    return schema.names;
  }
}
