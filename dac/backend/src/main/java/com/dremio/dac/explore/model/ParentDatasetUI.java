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
package com.dremio.dac.explore.model;

import java.util.List;

import com.dremio.service.namespace.dataset.proto.DatasetType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Return information about dataset's parents.
 */
public class ParentDatasetUI {

  private final List<String> fullPath;
  private final DatasetType datasetType;

  @JsonCreator
  public ParentDatasetUI(
    @JsonProperty("fullPath") List<String> fullPath,
    @JsonProperty("datasetType") DatasetType datasetType) {
    this.fullPath = fullPath;
    this.datasetType = datasetType;
  }

  public List<String> getFullPath() {
    return fullPath;
  }

  public DatasetType getDatasetType() {
    return datasetType;
  }

}
