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
package com.dremio.sabot.task;

import com.dremio.sabot.threads.AvailabilityCallback;
import com.dremio.sabot.threads.sharedres.SharedResourceType;

public interface AsyncTask extends Runnable {

  void refreshState();
  Task.State getState();
  SharedResourceType getFirstBlockedResource();
  void updateSleepDuration(long duration);
  void updateBlockedOnDownstreamDuration(long duration);
  void updateBlockedOnUpstreamDuration(long duration);
  void addBlockedOnSharedResourceDuration(SharedResourceType resource, long duration);
  void setWakeupCallback(AvailabilityCallback callback);
  void setTaskDescriptor(TaskDescriptor descriptor);
}
