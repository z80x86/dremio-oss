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
package com.dremio.exec.planner.sql;


import static org.apache.calcite.util.Static.RESOURCE;

import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlValidatorCatalogReader;
import org.apache.calcite.sql.validate.SqlValidatorException;
import org.apache.calcite.sql.validate.SqlValidatorScope;

class SqlValidatorImpl extends org.apache.calcite.sql.validate.SqlValidatorImpl {

  private final FlattenOpCounter flattenCount;

  protected SqlValidatorImpl(
      FlattenOpCounter flattenCount,
      SqlOperatorTable opTab,
      SqlValidatorCatalogReader catalogReader,
      RelDataTypeFactory typeFactory,
      SqlConformance conformance) {
    super(opTab, catalogReader, typeFactory, conformance);
    this.flattenCount = flattenCount;
  }

  @Override
  public void validateJoin(SqlJoin join, SqlValidatorScope scope) {
    SqlNode condition = join.getCondition();
    checkIfFlattenIsPartOfJoinCondition(condition);
    super.validateJoin(join, scope);
  }

  private void checkIfFlattenIsPartOfJoinCondition(SqlNode node) {
    if (node instanceof SqlBasicCall) {
      SqlBasicCall call = (SqlBasicCall) node;
      SqlNode[] conditionOperands = call.getOperands();
      for (SqlNode operand : conditionOperands) {
        if (operand instanceof SqlBasicCall) {
          if (((SqlBasicCall) operand).getOperator().getName().equalsIgnoreCase("flatten")) {
            throwException(node.getParserPosition());
          }
        }
        checkIfFlattenIsPartOfJoinCondition(operand);
      }
    }
  }

  private void throwException(SqlParserPos parserPos) {
    throw new CalciteContextException("Failure parsing the query",
                                      new SqlValidatorException("Flatten is not supported as part of join condition", null),
                                      parserPos.getLineNum(), parserPos.getEndLineNum(),
                                      parserPos.getColumnNum(), parserPos.getEndColumnNum());
  }

  int nextFlattenIndex(){
    return flattenCount.nextFlattenIndex();
  }

  static class FlattenOpCounter {
    private int value;

    int nextFlattenIndex(){
      return value++;
    }
  }

  @Override
  public void validateAggregateParams(SqlCall aggCall, SqlNode filter, SqlValidatorScope scope) {
    if (filter != null) {
      Exception e = new SqlValidatorException("Dremio does not currently support aggregate functions with a filter clause", null);
      SqlParserPos pos = filter.getParserPosition();
      CalciteContextException ex = RESOURCE.validatorContextPoint(pos.getLineNum(), pos.getColumnNum()).ex(e);
      ex.setPosition(pos.getLineNum(), pos.getColumnNum());
      throw ex;
    }
    super.validateAggregateParams(aggCall, filter, scope);
  }
}
