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
package com.dremio.sabot.exec;

import java.util.ArrayList;
import java.util.List;

import com.dremio.common.utils.protos.QueryIdHelper;
import com.dremio.exec.proto.UserBitShared.QueryId;

public class FailGreediestQueriesStrategy extends AbstractHeapClawBackStrategy {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FailGreediestQueriesStrategy.class);

  // If we are running short of heap, kill queries consuming upto this percentage of the total.
  private static final int CANCEL_PERCENTAGE = 25;

  public FailGreediestQueriesStrategy(FragmentExecutors fragmentExecutors, QueriesClerk queriesClerk) {
    super(fragmentExecutors, queriesClerk);
  }

  // find the greediest queries, and fail them.
  @Override
  public void clawBack() {
    // get all active queries.
    List<ActiveQuery> activeQueries = getSortedActiveQueries();
    if (activeQueries.size() == 0) {
      // if there are no active queries, nothing to do,
      logger.info("no active queries, nothing to fail");
      return;
    }

    // find the total memory used (we assume that the heap usage is proportional to the direct
    // memory).
    Long totalUsed = activeQueries
      .stream()
      .mapToLong(x -> x.directMemoryUsed)
      .reduce(0, Long::sum);

    // Collect queries amount to 25% of the total usage (atleast 1 query).
    List<QueryId> queriesToCancel = new ArrayList<>();
    long pendingCancelAmount = (totalUsed * CANCEL_PERCENTAGE) / 100;
    for (ActiveQuery activeQuery : activeQueries) {
      logger.info("Failing query " + QueryIdHelper.getQueryId(activeQuery.queryId) + " to avoid heap outage");

      queriesToCancel.add(activeQuery.queryId);
      pendingCancelAmount -= activeQuery.directMemoryUsed;
      if (pendingCancelAmount <= 0) {
        break;
      }
    }

    // fail the collected queries.
    failQueries(queriesToCancel);
  }
}
