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
package com.dremio.common.expression.visitors;

import com.dremio.common.expression.BooleanOperator;
import com.dremio.common.expression.CastExpression;
import com.dremio.common.expression.ConvertExpression;
import com.dremio.common.expression.ErrorCollector;
import com.dremio.common.expression.FunctionCall;
import com.dremio.common.expression.FunctionHolderExpression;
import com.dremio.common.expression.IfExpression;
import com.dremio.common.expression.InputReference;
import com.dremio.common.expression.LogicalExpression;
import com.dremio.common.expression.NullExpression;
import com.dremio.common.expression.SchemaPath;
import com.dremio.common.expression.TypedNullConstant;
import com.dremio.common.expression.IfExpression.IfCondition;
import com.dremio.common.expression.ValueExpressions.BooleanExpression;
import com.dremio.common.expression.ValueExpressions.DateExpression;
import com.dremio.common.expression.ValueExpressions.DecimalExpression;
import com.dremio.common.expression.ValueExpressions.DoubleExpression;
import com.dremio.common.expression.ValueExpressions.FloatExpression;
import com.dremio.common.expression.ValueExpressions.IntExpression;
import com.dremio.common.expression.ValueExpressions.IntervalDayExpression;
import com.dremio.common.expression.ValueExpressions.IntervalYearExpression;
import com.dremio.common.expression.ValueExpressions.LongExpression;
import com.dremio.common.expression.ValueExpressions.QuotedString;
import com.dremio.common.expression.ValueExpressions.TimeExpression;
import com.dremio.common.expression.ValueExpressions.TimeStampExpression;

final class ConstantChecker implements ExprVisitor<Boolean, ErrorCollector, RuntimeException> {


  private final static ConstantChecker INSTANCE = new ConstantChecker();

  private ConstantChecker() {}

  public static void checkConstants(LogicalExpression e, ErrorCollector errors) {
    e.accept(INSTANCE, errors);
  }

  @Override
  public Boolean visitFunctionCall(FunctionCall call, ErrorCollector errors) {
    throw new UnsupportedOperationException("FunctionCall is not expected here. "
        + "It should have been converted to FunctionHolderExpression in materialization");
  }

  @Override
  public Boolean visitFunctionHolderExpression(FunctionHolderExpression holder, ErrorCollector errors) {
    boolean allArgsAreConstant = true;
    for (int i = 0; i < holder.args.size(); i++) {
      boolean thisArgIsConstant = holder.args.get(i).accept(this, errors);
      if (!thisArgIsConstant) {
        allArgsAreConstant = false;
        if (holder.argConstantOnly(i)) {
          errors.addGeneralError(String.format("Function %s expects constant input for argument number %d", holder.getName(), i));
        }
      }
    }
    return allArgsAreConstant;
  }

  @Override
  public Boolean visitBooleanOperator(BooleanOperator op, ErrorCollector errors) {
    for (LogicalExpression e : op.args) {
      if (!e.accept(this, errors)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Boolean visitIfExpression(IfExpression ifExpr, ErrorCollector errors) {
    IfCondition c = ifExpr.ifCondition;
    if (!c.condition.accept(this, errors) || !c.expression.accept(this, errors)) {
      return false;
    }

    if (!ifExpr.elseExpression.accept(this, errors)) {
      return false;
    }
    return true;
  }

  @Override
  public Boolean visitSchemaPath(SchemaPath path, ErrorCollector errors) {
    return false;
  }

  @Override
  public Boolean visitIntConstant(IntExpression intExpr, ErrorCollector errors) {
    return true;
  }

  @Override
  public Boolean visitFloatConstant(FloatExpression fExpr, ErrorCollector errors) {
    return true;
  }

  @Override
  public Boolean visitLongConstant(LongExpression intExpr, ErrorCollector errors) {
    return true;
  }

  @Override
  public Boolean visitDateConstant(DateExpression intExpr, ErrorCollector errors) {
      return true;
  }

  @Override
  public Boolean visitInputReference(InputReference input, ErrorCollector errors) {
      return input.getReference().accept(this, errors);
  }

  @Override
  public Boolean visitTimeConstant(TimeExpression intExpr, ErrorCollector errors) {
      return true;
  }

  @Override
  public Boolean visitTimeStampConstant(TimeStampExpression intExpr, ErrorCollector errors) {
      return true;
  }

  @Override
  public Boolean visitIntervalYearConstant(IntervalYearExpression intExpr, ErrorCollector errors) {
      return true;
  }

  @Override
  public Boolean visitIntervalDayConstant(IntervalDayExpression intExpr, ErrorCollector errors) {
      return true;
  }

  @Override
  public Boolean visitDecimalConstant(DecimalExpression decExpr, ErrorCollector errors) {
    return false;
  }

  @Override
  public Boolean visitDoubleConstant(DoubleExpression dExpr, ErrorCollector errors) {
    return true;
  }

  @Override
  public Boolean visitBooleanConstant(BooleanExpression e, ErrorCollector errors) {
    return true;
  }

  @Override
  public Boolean visitQuotedStringConstant(QuotedString e, ErrorCollector errors) {
    return true;
  }

  @Override
  public Boolean visitUnknown(LogicalExpression e, ErrorCollector errors) {
    return false;
  }

  @Override
  public Boolean visitCastExpression(CastExpression e, ErrorCollector value) throws RuntimeException {
    return e.getInput().accept(this, value);
  }

  @Override
  public Boolean visitConvertExpression(ConvertExpression e, ErrorCollector value) throws RuntimeException {
    return e.getInput().accept(this, value);
  }

  @Override
  public Boolean visitNullConstant(TypedNullConstant e, ErrorCollector value) throws RuntimeException {
    return true;
  }

  @Override
  public Boolean visitNullExpression(NullExpression e, ErrorCollector value) throws RuntimeException {
    return true;
  }

}
