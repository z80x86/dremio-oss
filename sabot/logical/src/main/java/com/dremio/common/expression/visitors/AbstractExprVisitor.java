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
import com.dremio.common.expression.FunctionCall;
import com.dremio.common.expression.FunctionHolderExpression;
import com.dremio.common.expression.IfExpression;
import com.dremio.common.expression.LogicalExpression;
import com.dremio.common.expression.NullExpression;
import com.dremio.common.expression.SchemaPath;
import com.dremio.common.expression.InputReference;
import com.dremio.common.expression.TypedNullConstant;
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

public abstract class AbstractExprVisitor<T, VAL, EXCEP extends Exception> implements ExprVisitor<T, VAL, EXCEP> {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractExprVisitor.class);

  @Override
  public T visitFunctionCall(FunctionCall call, VAL value) throws EXCEP {
    return visitUnknown(call, value);
  }

  @Override
  public T visitFunctionHolderExpression(FunctionHolderExpression holder, VAL value) throws EXCEP {
    return visitUnknown(holder, value);
  }

  @Override
  public T visitIfExpression(IfExpression ifExpr, VAL value) throws EXCEP {
    return visitUnknown(ifExpr, value);
  }

  @Override
  public T visitBooleanOperator(BooleanOperator op, VAL value) throws EXCEP {
    return visitUnknown(op, value);
  }

  @Override
  public T visitSchemaPath(SchemaPath path, VAL value) throws EXCEP {
    return visitUnknown(path, value);
  }

  @Override
  public T visitFloatConstant(FloatExpression fExpr, VAL value) throws EXCEP {
    return visitUnknown(fExpr, value);
  }

  @Override
  public T visitIntConstant(IntExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitLongConstant(LongExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitInputReference(InputReference sideExpr, VAL value) throws EXCEP {
    return visitUnknown(sideExpr, value);
  }

  @Override
  public T visitDecimalConstant(DecimalExpression decExpr, VAL value) throws EXCEP {
    return visitUnknown(decExpr, value);
  }

  @Override
  public T visitDateConstant(DateExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitTimeConstant(TimeExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitTimeStampConstant(TimeStampExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitIntervalYearConstant(IntervalYearExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitIntervalDayConstant(IntervalDayExpression intExpr, VAL value) throws EXCEP {
    return visitUnknown(intExpr, value);
  }

  @Override
  public T visitDoubleConstant(DoubleExpression dExpr, VAL value) throws EXCEP {
    return visitUnknown(dExpr, value);
  }

  @Override
  public T visitBooleanConstant(BooleanExpression e, VAL value) throws EXCEP {
    return visitUnknown(e, value);
  }

  @Override
  public T visitQuotedStringConstant(QuotedString e, VAL value) throws EXCEP {
    return visitUnknown(e, value);
  }

  @Override
  public T visitCastExpression(CastExpression e, VAL value) throws EXCEP {
    return visitUnknown(e, value);
  }

  @Override
  public T visitConvertExpression(ConvertExpression e, VAL value) throws EXCEP {
    return visitUnknown(e, value);
  }

  @Override
  public T visitNullConstant(TypedNullConstant e, VAL value) throws EXCEP {
    return visitUnknown(e, value);
  }

  @Override
  public T visitNullExpression(NullExpression e, VAL value) throws EXCEP {
    return visitUnknown(e, value);
  }

  @Override
  public T visitUnknown(LogicalExpression e, VAL value) throws EXCEP {
    throw new UnsupportedOperationException(String.format("Expression of type %s not handled by visitor type %s.", e.getClass().getCanonicalName(), this.getClass().getCanonicalName()));
  }


}
