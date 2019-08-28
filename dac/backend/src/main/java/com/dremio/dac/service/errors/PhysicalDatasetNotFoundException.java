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
package com.dremio.dac.service.errors;

import com.dremio.dac.model.common.NamespacePath;
import com.dremio.dac.model.sources.PhysicalDatasetPath;
import com.dremio.dac.model.sources.PhysicalDatasetResourcePath;
import com.dremio.dac.model.sources.SourceName;
import com.dremio.service.namespace.dataset.proto.DatasetType;

/**
 * Throw when Physical Dataset not present in namespace.
 */
public class PhysicalDatasetNotFoundException extends NotFoundException {
  private static final long serialVersionUID = 1L;

  private final NamespacePath path;

  public PhysicalDatasetNotFoundException(
      SourceName sourceName,
      PhysicalDatasetPath physicalDatasetPath,
      Exception error) {
    super(
        new PhysicalDatasetResourcePath(sourceName, physicalDatasetPath),
        "physical dataset " + physicalDatasetPath.toPathString(),
        error);
    this.path = physicalDatasetPath;
  }

  public PhysicalDatasetNotFoundException(
      NamespacePath pdp,
      DatasetType type,
      Exception error) {
    super(new PhysicalDatasetResourcePath(pdp, type), "physical dataset " + pdp.toPathString(), error);
    this.path = new PhysicalDatasetPath(pdp, type);
  }

  public NamespacePath getPath() {
    return path;
  }
}
