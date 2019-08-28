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

package com.dremio.exec.resolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.arrow.vector.types.FloatingPointPrecision;
import org.apache.arrow.vector.types.pojo.ArrowType;

import com.dremio.common.expression.CompleteType;
import com.dremio.common.expression.CompleteTypeInLogicalExpression;
import com.dremio.common.expression.LogicalExpression;
import com.dremio.common.types.TypeProtos.DataMode;
import com.dremio.common.types.TypeProtos.MinorType;
import com.dremio.exec.expr.annotations.FunctionTemplate.NullHandling;
import com.dremio.exec.expr.fn.AbstractFunctionHolder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TypeCastRules {

  private static Map<MinorType, Set<MinorType>> rules;

  public TypeCastRules() {
  }

  static {
    initTypeRules();
  }

  private static void initTypeRules() {
    rules = new HashMap<>();

    Set<MinorType> rule;

    /** TINYINT cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.MONEY);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.TINYINT, rule);

    /** SMALLINT cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.MONEY);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.SMALLINT, rule);

    /** INT cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.INT, rule);

    /** BIGINT cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.BIGINT, rule);

    /** UINT8 cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.UINT8, rule);

    /** DECIMAL cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.DECIMAL, rule);

    /** DATE cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.DATE, rule);

    /** TIME cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TIME);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.TIME, rule);

    /** TIMESTAMP cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIMESTAMPTZ);
    rules.put(MinorType.TIMESTAMP, rule);

    /** TIMESTAMPTZ cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIME);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.TIMESTAMPTZ, rule);

    /** Interval cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.INTERVALDAY);
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.INTERVAL, rule);

    /** INTERVAL YEAR cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.INTERVALDAY);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.INTERVALYEAR, rule);

    /** INTERVAL DAY cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.INTERVALDAY);
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.INTERVALDAY, rule);

    /** FLOAT4 cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rules.put(MinorType.FLOAT4, rule);

    /** FLOAT8 cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rules.put(MinorType.FLOAT8, rule);

    /** BIT cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rules.put(MinorType.BIT, rule);

    /** FIXEDCHAR cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.MONEY);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIME);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.INTERVALDAY);
    rules.put(MinorType.FIXEDCHAR, rule);

    /** FIXED16CHAR cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIME);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.INTERVALDAY);
    rules.put(MinorType.FIXED16CHAR, rule);

    /** FIXEDSIZEBINARY cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rules.put(MinorType.FIXEDSIZEBINARY, rule);

    /** VARCHAR cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIME);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.INTERVALDAY);
    rules.put(MinorType.VARCHAR, rule);

    /** VAR16CHAR cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.FIXEDCHAR);
    rule.add(MinorType.FIXED16CHAR);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VAR16CHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rule.add(MinorType.DATE);
    rule.add(MinorType.TIME);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.INTERVAL);
    rule.add(MinorType.INTERVALYEAR);
    rule.add(MinorType.INTERVALDAY);
    rules.put(MinorType.VAR16CHAR, rule);

    /** VARBINARY cast able from **/
    rule = new HashSet<>();
    rule.add(MinorType.TINYINT);
    rule.add(MinorType.SMALLINT);
    rule.add(MinorType.INT);
    rule.add(MinorType.BIGINT);
    rule.add(MinorType.UINT1);
    rule.add(MinorType.UINT2);
    rule.add(MinorType.UINT4);
    rule.add(MinorType.UINT8);
    rule.add(MinorType.DECIMAL);
    rule.add(MinorType.TIMESTAMP);
    rule.add(MinorType.TIMESTAMPTZ);
    rule.add(MinorType.FLOAT4);
    rule.add(MinorType.FLOAT8);
    rule.add(MinorType.BIT);
    rule.add(MinorType.VARCHAR);
    rule.add(MinorType.VARBINARY);
    rule.add(MinorType.FIXEDSIZEBINARY);
    rules.put(MinorType.VARBINARY, rule);

    rules.put(MinorType.UNION, Sets.newHashSet(MinorType.UNION));
  }

  public static boolean isCastableWithNullHandling(CompleteType from, CompleteType to, NullHandling nullHandling) {
    if(from.isComplex() || to.isComplex()){
      return false;
    }
    return isCastable(from.toMinorType(), to.toMinorType());
  }

  public static boolean isCastable(MinorType from, MinorType to) {
    return from.equals(MinorType.NULL) ||      //null could be casted to any other type.
        (rules.get(to) == null ? false : rules.get(to).contains(from));
  }

  public static DataMode getLeastRestrictiveDataMode(List<DataMode> dataModes) {
    boolean hasOptional = false;
    for(DataMode dataMode : dataModes) {
      switch (dataMode) {
        case REPEATED:
          return dataMode;
        case OPTIONAL:
          hasOptional = true;
      }
    }

    if(hasOptional) {
      return DataMode.OPTIONAL;
    } else {
      return DataMode.REQUIRED;
    }
  }

  /*
   * Function checks if casting is allowed from the 'from' -> 'to' minor type.
   * If its allowed we also check if the precedence map allows such a cast and return true if
   * both cases are satisfied.
   * In some cases it might return a common higher type for e.g. for float, decimal we return
   * double.
   */
  public static MinorType getLeastRestrictiveType(List<MinorType> types) {
    assert types.size() >= 2;
    MinorType result = types.get(0);
    if (result == MinorType.UNION) {
      return result;
    }

    ImmutableMap<MinorType, Integer> precedenceMap = ResolverTypePrecedence.PRECEDENCE_MAP;

    int resultPrec = precedenceMap.get(result);

    for (int i = 1; i < types.size(); i++) {
      MinorType next = types.get(i);
      if (next == MinorType.UNION) {
        return next;
      }
      if (next == result) {
        // both args are of the same type; continue
        continue;
      }

      // Force float -> decimal to convert both to double.
      if ((result == MinorType.FLOAT4 && next == MinorType.DECIMAL) ||
          (result == MinorType.DECIMAL && next == MinorType.FLOAT4)) {
        result = MinorType.FLOAT8;
        continue;
      }

      int nextPrec = precedenceMap.get(next);

      if (isCastable(next, result) && resultPrec >= nextPrec) {
        // result is the least restrictive between the two args; nothing to do continue
        continue;
      } else if(isCastable(result, next) && nextPrec >= resultPrec) {
        result = next;
        resultPrec = nextPrec;
      } else {
        return null;
      }
    }

    return result;
  }

  private static final int DATAMODE_CAST_COST = 1;

  /*
   * code decide whether it's legal to do implicit cast. -1 : not allowed for
   * implicit cast > 0: cost associated with implicit cast. ==0: parms are
   * exactly same type of arg. No need of implicit.
   */
  public static int getCost(List<CompleteType> argumentTypes, AbstractFunctionHolder holder) {
    int cost = 0;

    if (argumentTypes.size() != holder.getParamCount()) {
      return -1;
    }

    // Indicates whether we used secondary cast rules
    boolean secondaryCast = false;

    // number of arguments that could implicitly casts using precedence map or didn't require casting at all
    int nCasts = 0;

    /*
     * If we are determining function holder for decimal data type, we need to make sure the output type of
     * the function can fit the precision that we need based on the input types.
     */
    if (holder.checkPrecisionRange() == true) {
      List<LogicalExpression> logicalExpressions = Lists.newArrayList();
      for(CompleteType completeType : argumentTypes) {
        logicalExpressions.add(
            new CompleteTypeInLogicalExpression(completeType));
      }

      if (!holder.isReturnTypeIndependent()) {
        return -1;
      }
    }

    ImmutableMap<MinorType, Integer> precedenceMap = ResolverTypePrecedence.PRECEDENCE_MAP;

    final int numOfArgs = holder.getParamCount();
    for (int i = 0; i < numOfArgs; i++) {
      final CompleteType argType = argumentTypes.get(i);
      final CompleteType parmType = holder.getParamType(i);

      //@Param FieldReader will match any type
      if (holder.isFieldReader(i)) {
//        if (Types.isComplex(call.args.get(i).getMajorType()) ||Types.isRepeated(call.args.get(i).getMajorType()) )
        // add the max cost when encountered with a field reader considering that it is the most expensive factor
        // contributing to the cost.
        cost += ResolverTypePrecedence.MAX_IMPLICIT_CAST_COST;
        continue;
//        else
//          return -1;
      }
      if (argType.isDecimal()) {
        if (parmType.getType().getTypeID() == ArrowType.ArrowTypeID.FloatingPoint
          && ((ArrowType.FloatingPoint) parmType.getType()).getPrecision() ==
          FloatingPointPrecision.SINGLE) {
          // do not allow decimals to be cast to float;
          return -1;
        }
      }

      if (!TypeCastRules.isCastableWithNullHandling(argType, parmType, holder.getNullHandling())) {
        return -1;
      }

      Integer parmVal = precedenceMap.get(parmType.toMinorType());
      Integer argVal = precedenceMap.get(argType.toMinorType());

      if (parmVal == null) {
        throw new RuntimeException(String.format(
            "Precedence for type %s is not defined", parmType));
      }

      if (argVal == null) {
        throw new RuntimeException(String.format(
            "Precedence for type %s is not defined", argType));
      }

      if (parmVal - argVal < 0) {

        /* Precedence rules does not allow to implicitly cast, however check
         * if the seconday rules allow us to cast
         */
        Set<MinorType> rules;
        if ((rules = (ResolverTypePrecedence.SECONDARY_IMPLICIT_CAST_RULES.get(parmType.toMinorType()))) != null &&
            rules.contains(argType.toMinorType()) != false) {
          secondaryCast = true;
        } else {
          return -1;
        }
      }

      int castCost;

      if ((castCost = (parmVal - argVal)) >= 0) {
        nCasts++;
        cost += castCost;
      }
    }

    if (secondaryCast) {
      // We have a secondary cast for one or more of the arguments, determine the cost associated
      int secondaryCastCost =  Integer.MAX_VALUE - 1;

      // Subtract maximum possible implicit costs from the secondary cast cost
      secondaryCastCost -= (nCasts * (ResolverTypePrecedence.MAX_IMPLICIT_CAST_COST + DATAMODE_CAST_COST));

      // Add cost of implicitly casting the rest of the arguments that didn't use secondary casting
      secondaryCastCost += cost;

      return secondaryCastCost;
    }

    return cost;
  }

  /*
   * Simple helper function to determine if input type is numeric
   */
  public static boolean isNumericType(MinorType inputType) {
    switch (inputType) {
      case TINYINT:
      case SMALLINT:
      case INT:
      case BIGINT:
      case UINT1:
      case UINT2:
      case UINT4:
      case UINT8:
      case DECIMAL:
      case FLOAT4:
      case FLOAT8:
        return true;
      default:
        return false;
    }
  }

}
