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
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.logical.LogicalSort;

/**
 * Rule that converts an {@link Sort} to a {@link SortRel}, implemented by a Dremio "order" operation.
 */
public class SortRule extends RelOptRule {
  public static final RelOptRule INSTANCE = new SortRule();

  private SortRule() {
    super(RelOptHelper.any(LogicalSort.class, Convention.NONE), "SortRule");
  }

  @Override
  public boolean matches(RelOptRuleCall call) {
    final Sort sort = call.rel(0);
    return sort.offset == null && sort.fetch == null;
  }

  @Override
  public void onMatch(RelOptRuleCall call) {

    final Sort sort = call.rel(0);

    final RelNode input = sort.getInput();
    final RelTraitSet traits = sort.getTraitSet().plus(Rel.LOGICAL);

    final RelNode convertedInput = convert(input, input.getTraitSet().plus(Rel.LOGICAL).simplify());
    call.transformTo(SortRel.create(sort.getCluster(), traits, convertedInput, sort.getCollation()));
  }
}
