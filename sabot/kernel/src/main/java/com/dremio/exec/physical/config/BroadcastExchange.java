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
package com.dremio.exec.physical.config;

import com.dremio.exec.physical.PhysicalOperatorSetupException;
import com.dremio.exec.physical.base.AbstractExchange;
import com.dremio.exec.physical.base.OpProps;
import com.dremio.exec.physical.base.PhysicalOperator;
import com.dremio.exec.physical.base.PhysicalOperatorUtil;
import com.dremio.exec.physical.base.Receiver;
import com.dremio.exec.physical.base.Sender;
import com.dremio.exec.planner.fragment.EndpointsIndex;
import com.dremio.exec.record.BatchSchema;

public class BroadcastExchange extends AbstractExchange {

  public BroadcastExchange(
      OpProps props,
      OpProps senderProps,
      OpProps receiverProps,
      BatchSchema schema,
      PhysicalOperator child) {
    super(props, senderProps, receiverProps, schema, child);
  }

  @Override
  protected PhysicalOperator getNewWithChild(PhysicalOperator child) {
    return new BroadcastExchange(props, senderProps, receiverProps, schema, child);
  }

  @Override
  public Sender getSender(int minorFragmentId, PhysicalOperator child, EndpointsIndex.Builder builder) throws PhysicalOperatorSetupException {
    return new BroadcastSender(senderProps, schema, child, receiverMajorFragmentId, PhysicalOperatorUtil.getIndexOrderedEndpoints(receiverLocations, builder));
  }

  @Override
  public Receiver getReceiver(int minorFragmentId, EndpointsIndex.Builder builder) {
    return new UnorderedReceiver(receiverProps, schema, senderMajorFragmentId, PhysicalOperatorUtil.getIndexOrderedEndpoints(senderLocations, builder), false);
  }

}
