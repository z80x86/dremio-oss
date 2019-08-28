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
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rex.RexNode;

/**
 * Rule that converts a {@link org.apache.calcite.rel.logical.LogicalProject} to a Dremio "project" operation.
 */
public class ProjectRule extends RelOptRule {

  public static final ProjectRule INSTANCE = new ProjectRule();

  private ProjectRule() {
    super(RelOptHelper.any(LogicalProject.class, Convention.NONE), "ProjectRule");
  }

  @Override
  public boolean matches(RelOptRuleCall call) {
    // this cannot operate on a project that has a flatten in it.
    final Project project = call.rel(0);
    for (RexNode e : project.getChildExps()) {
      if (FlattenVisitors.hasFlatten(e)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    final Project toTransform = call.rel(0);
    final RelNode input = toTransform.getInput();
    final RelTraitSet traits = toTransform.getTraitSet().plus(Rel.LOGICAL);
    final RelNode convertedInput = convert(input, input.getTraitSet().plus(Rel.LOGICAL).simplify());
    call.transformTo(ProjectRel.create(toTransform.getCluster(), traits, convertedInput, toTransform.getProjects(), toTransform.getRowType()));
  }
}
