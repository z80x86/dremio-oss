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

import com.dremio.service.namespace.dataset.proto.DatasetConfig;

public abstract class ConversionContext {
  public final static ConversionContext EMPTY = new EmptyContext();

  private final static class EmptyContext extends ConversionContext {}

  public static class NamespaceConversionContext extends ConversionContext {
    private final AuthorizationContext authContext;
    private final DatasetConfig datasetConfig;

    public NamespaceConversionContext(final AuthorizationContext authContext, final DatasetConfig datasetConfig) {
      this.datasetConfig = datasetConfig;
      this.authContext = authContext;
    }

    public AuthorizationContext getAuthContext() {
      return authContext;
    }

    public DatasetConfig getDatasetConfig() {
      return datasetConfig;
    }
  }
}
