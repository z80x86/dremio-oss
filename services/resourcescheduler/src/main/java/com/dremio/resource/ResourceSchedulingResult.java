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
package com.dremio.resource;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Result of resource allocation call
 */
public class ResourceSchedulingResult {
  private final ListenableFuture<ResourceSet> resourceSetFuture;
  private final ResourceSchedulingDecisionInfo resourceSchedulingDecisionInfo;

  public ResourceSchedulingResult(
    ResourceSchedulingDecisionInfo resourceSchedulingDecisionInfo,
    ListenableFuture<ResourceSet> resourceSetFuture
  ) {
    this.resourceSchedulingDecisionInfo = resourceSchedulingDecisionInfo;
    this.resourceSetFuture = resourceSetFuture;
  }

  public ListenableFuture<ResourceSet> getResourceSetFuture() {
    return resourceSetFuture;
  }

  public ResourceSchedulingDecisionInfo getResourceSchedulingDecisionInfo() {
    return resourceSchedulingDecisionInfo;
  }
}
