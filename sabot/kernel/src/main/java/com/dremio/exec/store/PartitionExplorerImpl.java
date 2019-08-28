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
package com.dremio.exec.store;

import java.util.List;

import com.dremio.exec.catalog.Catalog;
import com.dremio.service.namespace.NamespaceKey;
import com.google.common.collect.ImmutableList;

public class PartitionExplorerImpl implements PartitionExplorer {

  private final Catalog catalog;

  public PartitionExplorerImpl(Catalog catalog) {
    this.catalog = catalog;
  }

  @Override
  public Iterable<String> getSubPartitions(
      NamespaceKey table,
      List<String> partitionColumns,
      List<String> partitionValues
      ) throws PartitionNotFoundException {
    return catalog.getSubPartitions(table, partitionColumns, partitionValues);
  }

  @Override
  public Iterable<String> getSubPartitions(String schema, String table, List<String> partitionColumns, List<String> partitionValues)
      throws PartitionNotFoundException {
    return catalog.getSubPartitions(new NamespaceKey(ImmutableList.of(schema, table)), partitionColumns, partitionValues);
  }


}
