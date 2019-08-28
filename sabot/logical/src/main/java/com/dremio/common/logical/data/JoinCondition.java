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
package com.dremio.common.logical.data;

import com.dremio.common.expression.LogicalExpression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinCondition {
  private final String relationship;
  private final LogicalExpression left;
  private final LogicalExpression right;

  @JsonCreator
  public JoinCondition(@JsonProperty("relationship") String relationship,
      @JsonProperty("left") LogicalExpression left, @JsonProperty("right") LogicalExpression right) {
    super();
    this.relationship = relationship;
    this.left = left;
    this.right = right;
  }

  public String getRelationship() {
    return relationship;
  }

  public LogicalExpression getLeft() {
    return left;
  }

  public LogicalExpression getRight() {
    return right;
  }

  public JoinCondition flip(){
    return new JoinCondition(relationship, right, left);
  }

}
