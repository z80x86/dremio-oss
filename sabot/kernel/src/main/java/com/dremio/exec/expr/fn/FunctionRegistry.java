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
package com.dremio.exec.expr.fn;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.commons.lang3.tuple.Pair;

import com.dremio.common.scanner.persistence.AnnotatedClassDescriptor;
import com.dremio.common.scanner.persistence.ScanResult;
import com.dremio.exec.expr.annotations.FunctionTemplate;
import com.dremio.exec.expr.annotations.FunctionTemplate.FunctionSyntax;
import com.dremio.exec.expr.fn.hll.HyperLogLog;
import com.dremio.exec.planner.sql.OperatorTable;
import com.dremio.exec.planner.sql.SqlAggOperator;
import com.dremio.exec.planner.sql.SqlDatePartOperator;
import com.dremio.exec.planner.sql.SqlFlattenOperator;
import com.dremio.exec.planner.sql.SqlOperatorImpl;
import com.dremio.exec.planner.sql.TypeInferenceUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Registry of Dremio functions.
 */
public class FunctionRegistry implements PrimaryFunctionRegistry{
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FunctionRegistry.class);

  private static final ImmutableMap<String, Pair<Integer, Integer>> funcToRange = ImmutableMap.<String, Pair<Integer, Integer>> builder()
      // CONCAT is allowed to take [1, infinity) number of arguments.
      // Currently, this flexibility is offered by RexToExpr to rewrite it as
      // a nested structure
      .put("CONCAT", Pair.of(1, Integer.MAX_VALUE))

      // When LENGTH is given two arguments, this function relies on RexToExpr to rewrite it as
      // another function based on the second argument (encodingType)
      .put("LENGTH", Pair.of(1, 2))

      // Dummy functions
      .put("CONVERT_TO", Pair.of(2, 2))
      .put("CONVERT_FROM", Pair.of(2, 3))
      .put("FLATTEN", Pair.of(1, 1)).build();

  // Function for E()
  public static final SqlFunction E_FUNCTION =
    new SqlFunction(new SqlIdentifier("E", SqlParserPos.ZERO), ReturnTypes.DOUBLE,
      null, OperandTypes.NILADIC, null, SqlFunctionCategory.NUMERIC);

  // key: function name (lowercase) value: list of functions with that name
  private final ArrayListMultimap<String, AbstractFunctionHolder> registeredFunctions = ArrayListMultimap.create();

  public ArrayListMultimap<String, AbstractFunctionHolder> getRegisteredFunctions() {
    return registeredFunctions;
  }

  public FunctionRegistry(ScanResult classpathScan) {
    FunctionConverter converter = new FunctionConverter();
    List<AnnotatedClassDescriptor> providerClasses = classpathScan.getAnnotatedClasses(FunctionTemplate.class.getName());

    // Hash map to prevent registering functions with exactly matching signatures
    // key: Function Name + Input's Major Type
    // value: Class name where function is implemented
    //
    final Map<String, String> functionSignatureMap = new HashMap<>();
    for (AnnotatedClassDescriptor func : providerClasses) {
      BaseFunctionHolder holder = converter.getHolder(func);
      if (holder != null) {
        // register handle for each name the function can be referred to
        String[] names = holder.getRegisteredNames();

        // Create the string for input types
        String functionInput = "";
        for (BaseFunctionHolder.ValueReference ref : holder.parameters) {
          functionInput += ref.getType().toString();
        }
        for (String name : names) {
          String functionName = name.toLowerCase();
          registeredFunctions.put(functionName, holder);
          String functionSignature = functionName + functionInput;
          String existingImplementation;
          if ((existingImplementation = functionSignatureMap.get(functionSignature)) != null) {
            throw new AssertionError(
                String.format(
                    "Conflicting functions with similar signature found. Func Name: %s, Class name: %s " +
                        " Class name: %s", functionName, func.getClassName(), existingImplementation));
          } else if (holder.isAggregating() && !holder.isDeterministic() ) {
            logger.warn("Aggregate functions must be deterministic, did not register function {}", func.getClassName());
          } else {
            functionSignatureMap.put(functionSignature, func.getClassName());
          }
        }
      } else {
        logger.warn("Unable to initialize function for class {}", func.getClassName());
      }
    }
    if (logger.isTraceEnabled()) {
      StringBuilder allFunctions = new StringBuilder();
      for (AbstractFunctionHolder method: registeredFunctions.values()) {
        allFunctions.append(method.toString()).append("\n");
      }
      logger.trace("Registered functions: [\n{}]", allFunctions);
    }

    // TODO(DX-13734): Add validation in FunctionRegistry to ensure required functions are registered
  }

  public int size(){
    return registeredFunctions.size();
  }

  /** Returns functions with given name. Function name is case insensitive. */
  @Override
  public List<AbstractFunctionHolder> getMethods(String name) {
    return this.registeredFunctions.get(name.toLowerCase());
  }

  public void register(OperatorTable operatorTable, boolean isDecimalV2Enabled) {
    operatorTable.add("NDV", HyperLogLog.NDV);
    operatorTable.add("DATE_PART", SqlDatePartOperator.INSTANCE);
    operatorTable.add("FLATTEN", SqlFlattenOperator.INSTANCE);
    operatorTable.add("E", E_FUNCTION);

    for (Entry<String, Collection<AbstractFunctionHolder>> function : registeredFunctions.asMap().entrySet()) {
      final ArrayListMultimap<Pair<Integer, Integer>, BaseFunctionHolder> functions = ArrayListMultimap.create();
      final ArrayListMultimap<Integer, BaseFunctionHolder> aggregateFunctions = ArrayListMultimap.create();
      final String name = function.getKey().toUpperCase();
      boolean isDeterministic = true;
      boolean isDynamic = false;
      FunctionSyntax syntax = FunctionSyntax.FUNCTION;
      for (AbstractFunctionHolder func : function.getValue()) {
        BaseFunctionHolder functionHolder = (BaseFunctionHolder) func;
        final int paramCount = func.getParamCount();
        if(functionHolder.isAggregating()) {
          aggregateFunctions.put(paramCount, functionHolder);
        } else {
          final Pair<Integer, Integer> argNumberRange;
          if(funcToRange.containsKey(name)) {
            argNumberRange = funcToRange.get(name);
          } else {
            argNumberRange = Pair.of(func.getParamCount(), func.getParamCount());
          }
          functions.put(argNumberRange, functionHolder);
        }

        if(!functionHolder.isDeterministic()) {
          isDeterministic = false;
        }
        if (functionHolder.isDynamic()) {
          isDynamic = true;
        }

        // All the functions are assumed to share the same syntax
        syntax = functionHolder.getSyntax();
      }

      final SqlSyntax sqlSyntax;
      switch(syntax) {
      case FUNCTION:
        sqlSyntax = SqlSyntax.FUNCTION;
        break;

      case FUNCTION_ID:
        sqlSyntax = SqlSyntax.FUNCTION_ID;
        break;

      default:
        throw new AssertionError("Dremio doesn't support function syntax" + syntax);
      }

      for (Entry<Pair<Integer, Integer>, Collection<BaseFunctionHolder>> entry : functions.asMap().entrySet()) {
        final SqlOperatorImpl sqlOperator;
        final Pair<Integer, Integer> range = entry.getKey();
        final int max = range.getRight();
        final int min = range.getLeft();
        sqlOperator = new SqlOperatorImpl(
            name,
            min,
            max,
            isDeterministic,
            isDynamic,
            TypeInferenceUtils.getSqlReturnTypeInference(name, Lists.newArrayList(entry.getValue
              ()), isDecimalV2Enabled),
            sqlSyntax);
        operatorTable.add(name, sqlOperator);
      }
      for (Entry<Integer, Collection<BaseFunctionHolder>> entry : aggregateFunctions.asMap().entrySet()) {
        operatorTable.add(name, new SqlAggOperator(
            name,
            entry.getKey(),
            entry.getKey(),
            TypeInferenceUtils.getSqlReturnTypeInference(name, Lists.newArrayList(entry.getValue
              ()), isDecimalV2Enabled)));
      }
    }
  }
}
