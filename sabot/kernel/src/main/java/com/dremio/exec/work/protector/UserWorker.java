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
package com.dremio.exec.work.protector;

import com.dremio.common.utils.protos.ExternalIdHelper;
import com.dremio.exec.proto.GeneralRPCProtos.Ack;
import com.dremio.exec.proto.UserBitShared.ExternalId;
import com.dremio.exec.work.foreman.TerminationListenerRegistry;
import com.dremio.options.OptionManager;
import com.dremio.sabot.rpc.user.UserSession;

public interface UserWorker {

  void submitWork(ExternalId externalId, UserSession session,
    UserResponseHandler responseHandler, UserRequest request, TerminationListenerRegistry registry);

  default void submitWork(UserSession session, UserResponseHandler responseHandler,
      UserRequest request, TerminationListenerRegistry registry) {
    submitWork(ExternalIdHelper.generateExternalId(), session, responseHandler, request, registry);
  }

  Ack cancelQuery(ExternalId query, String username);

  Ack resumeQuery(ExternalId query);

  OptionManager getSystemOptions();

}
