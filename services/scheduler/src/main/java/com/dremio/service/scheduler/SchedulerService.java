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
package com.dremio.service.scheduler;

import com.dremio.service.Service;

/**
 * Service to schedule periodic tasks
 */
public interface SchedulerService extends Service {

  /**
   * Schedule a periodic task
   *
   * The task will be scheduled to run at the specific instants provided by {@code schedule}.
   * If the execution time is greater than the amount of time between each instant, the execution is skipped.
   *
   * @param schedule the schedule to follow for executing {@code task}
   * @param task the task to run. {@code Runnable#toString()} will be used for any error message/logging
   *             information pertaining to that task.
   * @return a {@code Cancellable} instance, to cancel the periodic execution
   */
  Cancellable schedule(Schedule schedule, Runnable task);
}
