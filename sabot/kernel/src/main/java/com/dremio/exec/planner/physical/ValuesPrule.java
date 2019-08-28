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
package com.dremio.exec.planner.physical;

import java.io.IOException;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;

import com.dremio.exec.planner.logical.RelOptHelper;
import com.dremio.exec.planner.logical.ValuesRel;

public class ValuesPrule extends RelOptRule {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ValuesPrule.class);

  public static final ValuesPrule INSTANCE = new ValuesPrule();

  private ValuesPrule(){
    super(RelOptHelper.any(ValuesRel.class), "Prel.ValuesPrule");
  }

  @Override
  public void onMatch(final RelOptRuleCall call) {
    final ValuesRel rel = (ValuesRel) call.rel(0);
    try{
      call.transformTo(new ValuesPrel(rel.getCluster(), rel.getTraitSet().plus(Prel.PHYSICAL), rel.getRowType(), rel.getTuplesAsJsonOptions(), rel.getRows()));
    }catch(IOException e){
      logger.warn("Failure while converting JSONOptions.", e);
    }
  }



}
