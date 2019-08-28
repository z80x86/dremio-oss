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

package com.dremio.exec.planner.logical;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;

import com.dremio.exec.calcite.logical.EmptyCrel;

/**
 * Convert {@link EmptyCrel} to Dremio logical {@link EmptyRel}
 */
public class EmptyRule extends RelOptRule {
  public static final RelOptRule INSTANCE = new EmptyRule();

  private EmptyRule() {
    super(RelOptHelper.any(EmptyCrel.class, Convention.NONE), "EmptyRule");
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    final EmptyCrel crel = call.rel(0);
    call.transformTo(new EmptyRel(
      crel.getCluster(),
      crel.getTraitSet().plus(Rel.LOGICAL),
      crel.getRowType(),
      crel.getProjectedSchema()));
  }
}
