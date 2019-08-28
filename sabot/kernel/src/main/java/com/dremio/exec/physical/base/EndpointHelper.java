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
package com.dremio.exec.physical.base;

import com.dremio.exec.proto.CoordinationProtos.NodeEndpoint;

/**
 * Helper class to build minimal endpoints. Used in rpcs sent to executor nodes, and to track
 * status of running fragments.
 */
public class EndpointHelper {

  public static NodeEndpoint getMinimalEndpoint(NodeEndpoint endpoint) {
    NodeEndpoint.Builder builder = NodeEndpoint.newBuilder();
    if (endpoint.hasAddress()) {
      builder.setAddress(endpoint.getAddress());
    }
    if (endpoint.hasUserPort()) {
      builder.setUserPort(endpoint.getUserPort());
    }
    if (endpoint.hasFabricPort()) {
      builder.setFabricPort(endpoint.getFabricPort());
    }
    return builder.build();
  }
}
