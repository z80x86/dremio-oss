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
package com.dremio.exec.store.common;

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;

import com.dremio.exec.calcite.logical.ScanCrel;
import com.dremio.exec.catalog.conf.ConnectionConf;
import com.dremio.exec.catalog.conf.SourceType;
import com.dremio.exec.planner.logical.Rel;

public abstract class SourceLogicalConverter extends ConverterRule {

  private final SourceType pluginType;

  public SourceLogicalConverter(Class<? extends ConnectionConf<?, ?>> clazz) {
    this(clazz.getAnnotation(SourceType.class));
  }

  public SourceLogicalConverter(SourceType pluginType) {
    super(ScanCrel.class, Convention.NONE, Rel.LOGICAL, pluginType.value() + "LogicalScanConverter");
    this.pluginType = pluginType;
  }

  @Override
  public boolean matches(RelOptRuleCall call) {
    ScanCrel scan = call.rel(0);
    return scan.getPluginId().getType().equals(pluginType);
  }

  @Override
  public final Rel convert(RelNode rel){
    return convertScan((ScanCrel) rel);
  }

  public abstract Rel convertScan(ScanCrel scan);

}
