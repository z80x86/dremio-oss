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
import org.apache.calcite.util.ImmutableNullableList;

import com.dremio.service.namespace.NamespaceKey;

/**
 * SQL node tree for <code>ALTER TABLE table_identifier <ENABLE|DISABLE> APPROXIMATE STATS</code>
 */
public class SqlSetApprox extends SqlSystemCall {

  public static final SqlSpecialOperator OPERATOR =
      new SqlSpecialOperator("ENABLE_APPROXIMATE_STATS", SqlKind.OTHER) {
        @Override public SqlCall createCall(SqlLiteral functionQualifier,
            SqlParserPos pos, SqlNode... operands) {
          return new SqlSetApprox(pos, (SqlIdentifier) operands[0], (SqlLiteral) operands[1]);
        }
      };

  private SqlIdentifier table;
  private SqlLiteral enable;

  /** Creates a SqlSetApprox. */
  public SqlSetApprox(SqlParserPos pos, SqlIdentifier table, SqlLiteral enable) {
    super(pos);
    this.table = table;
    this.enable = enable;
  }

  @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    writer.keyword("ALTER");
    writer.keyword("TABLE");
    table.unparse(writer, leftPrec, rightPrec);
    if((Boolean) enable.getValue()) {
      writer.keyword("ENABLE");
    } else {
      writer.keyword("DISABLE");
    }

    writer.keyword("APPROXIMATE");
    writer.keyword("STATS");
  }

  @Override public void setOperand(int i, SqlNode operand) {
    switch (i) {
      case 0:
        table = (SqlIdentifier) operand;
        break;
      case 1:
        enable = (SqlLiteral) operand;
        break;
      default:
        throw new AssertionError(i);
    }
  }

  public boolean isEnable() {
    return (Boolean) enable.getValue();
  }

  public NamespaceKey getPath() {
    return new NamespaceKey(table.names);
  }

  @Override
  public SqlOperator getOperator() {
    return OPERATOR;
  }

  @Override
  public List<SqlNode> getOperandList() {
    return ImmutableNullableList.<SqlNode>of(table, enable);
  }

  public SqlIdentifier getTable() { return table; }
}

