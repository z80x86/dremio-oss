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
package com.dremio.exec.store.mock;

import com.dremio.exec.physical.base.AbstractStore;
import com.dremio.exec.physical.base.OpProps;
import com.dremio.exec.physical.base.PhysicalOperator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("mock-store")
public class MockStorePOP extends AbstractStore {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MockStorePOP.class);

  @JsonCreator
  public MockStorePOP(@JsonProperty("props") OpProps props, @JsonProperty("child") PhysicalOperator child) {
    super(props, child);
  }

  @Override
  protected PhysicalOperator getNewWithChild(PhysicalOperator child) {
    return new MockStorePOP(OpProps.prototype(), child);
  }

  @Override
  public int getOperatorType() {
    return Integer.MIN_VALUE;
  }

}
