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
package com.dremio.service.namespace;

import com.dremio.service.namespace.dataset.proto.PartitionProtobuf;
import com.dremio.service.namespace.dataset.proto.PartitionProtobuf.NormalizedPartitionInfo;
import com.google.protobuf.ByteString;

/**
 * Represents the partition chunk + split data used by consumers of the NamespaceService interface
 */
public interface PartitionChunkMetadata {
  long getSize();

  long getRowCount();

  Iterable<PartitionProtobuf.PartitionValue> getPartitionValues();

  String getSplitKey();

  int getSplitCount();

  Iterable<PartitionProtobuf.DatasetSplit> getDatasetSplits();

  ByteString getPartitionExtendedProperty();

  /**
   * Instead: get dataset splits, then use the affinity from the splits
   */
  @Deprecated
  Iterable<PartitionProtobuf.Affinity> getAffinities();

  NormalizedPartitionInfo getNormalizedPartitionInfo();
}
