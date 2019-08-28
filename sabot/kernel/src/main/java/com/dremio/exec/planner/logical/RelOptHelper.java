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

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.rel.RelNode;

public class RelOptHelper {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RelOptHelper.class);

  public static RelOptRuleOperand any(Class<? extends RelNode> first, RelTrait trait){
    return RelOptRule.operand(first, trait, RelOptRule.any());
  }

  public static RelOptRuleOperand any(Class<? extends RelNode> first){
    return RelOptRule.operand(first, RelOptRule.any());
  }

  public static RelOptRuleOperand any(Class<? extends RelNode> first, Class<? extends RelNode> second) {
    return RelOptRule.operand(first, RelOptRule.operand(second, RelOptRule.any()));
  }

  public static RelOptRuleOperand some(Class<? extends RelNode> rel, RelOptRuleOperand first, RelOptRuleOperand... rest){
    return RelOptRule.operand(rel, RelOptRule.some(first, rest));
  }

  public static RelOptRuleOperand some(Class<? extends RelNode> rel, RelTrait trait, RelOptRuleOperand first, RelOptRuleOperand... rest){
    return RelOptRule.operand(rel, trait, RelOptRule.some(first, rest));
  }

}
