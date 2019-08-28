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
package com.dremio.exec.physical.impl.partitionsender;

import com.dremio.PlanTestBase;

/**
 * PartitionerSenderRootExec test to cover mostly part that deals with multithreaded
 * ability to copy and flush data
 *
 */
public class TestPartitionSender extends PlanTestBase {

//  private static final SimpleParallelizer PARALLELIZER = new SimpleParallelizer(
//      1 /*parallelizationThreshold (slice_count)*/,
//      6 /*maxWidthPerNode*/,
//      1000 /*maxGlobalWidth*/,
//      1.2 /*affinityFactor*/,
//      AbstractAttemptObserver.NOOP);
//
//  private final static UserSession USER_SESSION = UserSession.Builder.newBuilder()
//      .withCredentials(UserBitShared.UserCredentials.newBuilder().setUserName("foo").build())
//      .build();
//
//
//  public static TemporaryFolder testTempFolder = new TemporaryFolder();
//
//  private final static int NUM_DEPTS = 40;
//  private final static int NUM_EMPLOYEES = 1000;
//  private final static int NODES_COUNT = 3;
//
//  private static String empTableLocation;
//
//  private static String groupByQuery;
//
//  @BeforeClass
//  public static void setupTempFolder() throws IOException {
//    testTempFolder.create();
//  }
//
//  @BeforeClass
//  public static void generateTestDataAndQueries() throws Exception {
//    // Table consists of two columns "emp_id", "emp_name" and "dept_id"
//    empTableLocation = testTempFolder.newFolder().getAbsolutePath();
//
//    // Write 100 records for each new file
//    final int empNumRecsPerFile = 100;
//    for(int fileIndex=0; fileIndex<NUM_EMPLOYEES/empNumRecsPerFile; fileIndex++) {
//      File file = new File(empTableLocation + File.separator + fileIndex + ".json");
//      PrintWriter printWriter = new PrintWriter(file);
//      for (int recordIndex = fileIndex*empNumRecsPerFile; recordIndex < (fileIndex+1)*empNumRecsPerFile; recordIndex++) {
//        String record = String.format("{ \"emp_id\" : %d, \"emp_name\" : \"Employee %d\", \"dept_id\" : %d }",
//            recordIndex, recordIndex, recordIndex % NUM_DEPTS);
//        printWriter.println(record);
//      }
//      printWriter.close();
//    }
//
//    // Initialize test queries
//    groupByQuery = String.format("SELECT dept_id, count(*) as numEmployees FROM dfs.\"%s\" GROUP BY dept_id", empTableLocation);
//  }
//
//  @AfterClass
//  public static void cleanupTempFolder() throws IOException {
//    testTempFolder.delete();
//  }
//
//  @Test
//  /**
//   * Main test to go over different scenarios
//   * @throws Exception
//   */
//  @Ignore("DX-3475")
//  public void testPartitionSenderCostToThreads() throws Exception {
//
//    final VectorContainer container = new VectorContainer();
//    container.buildSchema(SelectionVectorMode.FOUR_BYTE);
//    final SelectionVector4 sv = Mockito.mock(SelectionVector4.class, "SelectionVector4");
//    Mockito.when(sv.getCount()).thenReturn(100);
//    Mockito.when(sv.getTotalCount()).thenReturn(100);
//    for (int i = 0; i < 100; i++ ) {
//      Mockito.when(sv.get(i)).thenReturn(i);
//    }
//
//    final TopNOperator.SimpleRecordBatch incoming = new TopNOperator.SimpleRecordBatch(container, sv, null);
//
//    updateTestCluster(NODES_COUNT, null);
//
//    test("ALTER SESSION SET \"planner.slice_target\"=1");
//    String plan = getPlanInString("EXPLAIN PLAN FOR " + groupByQuery, JSON_FORMAT);
//    System.out.println("Plan: " + plan);
//
//    final SabotContext sabotContext = getSabotContext();
//    final PhysicalPlanReader planReader = sabotContext.getPlanReader();
//    final PhysicalPlan physicalPlan = planReader.readPhysicalPlan(plan);
//    final Fragment rootFragment = PopUnitTestBase.getRootFragmentFromPlanString(planReader, plan);
//    final PlanningSet planningSet = new PlanningSet();
//    final FunctionImplementationRegistry registry = FUNCTIONS();
//
//    // Create a planningSet to get the assignment of major fragment ids to fragments.
//    PARALLELIZER.initFragmentWrappers(rootFragment, planningSet);
//
//    final List<PhysicalOperator> operators = physicalPlan.getSortedOperators(false);
//
//    // get HashToRandomExchange physical operator
//    HashToRandomExchange hashToRandomExchange = null;
//    for ( PhysicalOperator operator : operators) {
//      if ( operator instanceof HashToRandomExchange) {
//        hashToRandomExchange = (HashToRandomExchange) operator;
//        break;
//      }
//    }
//
//    final OptionList options = new OptionList();
//    // try multiple scenarios with different set of options
//    options.add(OptionValue.createLong(OptionType.SESSION, "planner.slice_target", 1));
//    testThreadsHelper(hashToRandomExchange, sabotContext, options,
//        incoming, registry, planReader, planningSet, rootFragment, 8);
//
//    options.clear();
//    options.add(OptionValue.createLong(OptionType.SESSION, "planner.slice_target", 1));
//    options.add(OptionValue.createLong(OptionType.SESSION, "planner.partitioner_sender_max_threads", 10));
//    hashToRandomExchange.setCost(1000);
//    testThreadsHelper(hashToRandomExchange, sabotContext, options,
//        incoming, registry, planReader, planningSet, rootFragment, 10);
//
//    options.clear();
//    options.add(OptionValue.createLong(OptionType.SESSION, "planner.slice_target", 1000));
//    options.add(OptionValue.createLong(OptionType.SESSION, "planner.partitioner_sender_threads_factor",2));
//    hashToRandomExchange.setCost(14000);
//    testThreadsHelper(hashToRandomExchange, sabotContext, options,
//        incoming, registry, planReader, planningSet, rootFragment, 2);
//  }
//
//  /**
//   * Core of the testing
//   * @param hashToRandomExchange
//   * @param sabotContext
//   * @param options
//   * @param incoming
//   * @param registry
//   * @param planReader
//   * @param planningSet
//   * @param rootFragment
//   * @param expectedThreadsCount
//   * @throws Exception
//   */
//  private void testThreadsHelper(HashToRandomExchange hashToRandomExchange, SabotContext sabotContext, OptionList options,
//      RecordBatch incoming, FunctionImplementationRegistry registry, PhysicalPlanReader planReader, PlanningSet planningSet, Fragment rootFragment,
//      int expectedThreadsCount) throws Exception {
//
//    final QueryContextInformation queryContextInfo = Utilities.createQueryContextInfo("dummySchemaName");
//    final QueryWorkUnit qwu = PARALLELIZER.getFragments(options, sabotContext.getEndpoint(),
//        QueryId.getDefaultInstance(),
//        sabotContext.getBits(), planReader, rootFragment, USER_SESSION, queryContextInfo);
//
//    final List<MinorFragmentEndpoint> mfEndPoints = PhysicalOperatorUtil.getIndexOrderedEndpoints(Lists.newArrayList(sabotContext.getBits()));
//
//    for(PlanFragment planFragment : qwu.getFragments()) {
//      if (!planFragment.getFragmentJson().contains("hash-partition-sender")) {
//        continue;
//      }
//      MockPartitionSenderRootExec partionSenderRootExec = null;
//      FragmentContext context = null;
//      try {
//        context = new FragmentContext(sabotContext, planFragment, null, new PassthroughQueryObserver(mockUserClientConnection(null)), registry);
//        final int majorFragmentId = planFragment.getHandle().getMajorFragmentId();
//        final HashPartitionSender partSender = new HashPartitionSender(majorFragmentId, hashToRandomExchange, hashToRandomExchange.getExpression(), mfEndPoints);
//        partionSenderRootExec = new MockPartitionSenderRootExec(context, incoming, partSender);
//        assertEquals("Number of threads calculated", expectedThreadsCount, partionSenderRootExec.getNumberPartitions());
//
//        partionSenderRootExec.createPartitioner();
//        final PartitionerDecorator partDecor = partionSenderRootExec.getPartitioner();
//        assertNotNull(partDecor);
//
//        List<Partitioner> partitioners = partDecor.getPartitioners();
//        assertNotNull(partitioners);
//        final int actualThreads = NODES_COUNT > expectedThreadsCount ? expectedThreadsCount : NODES_COUNT;
//        assertEquals("Number of partitioners", actualThreads, partitioners.size());
//
//        for ( int i = 0; i < mfEndPoints.size(); i++) {
//          assertNotNull("PartitionOutgoingBatch", partDecor.getOutgoingBatches(i));
//        }
//
//        // check distribution of PartitionOutgoingBatch - should be even distribution
//        boolean isFirst = true;
//        int prevBatchCountSize = 0;
//        int batchCountSize = 0;
//        for (Partitioner part : partitioners ) {
//          final List<PartitionOutgoingBatch> outBatch = (List<PartitionOutgoingBatch>) part.getOutgoingBatches();
//          batchCountSize = outBatch.size();
//          if ( !isFirst ) {
//            assertTrue(Math.abs(batchCountSize - prevBatchCountSize) <= 1);
//          } else {
//            isFirst = false;
//          }
//          prevBatchCountSize = batchCountSize;
//        }
//
//        partionSenderRootExec.getStats().startProcessing();
//        try {
//          partDecor.partitionBatch(incoming);
//        } finally {
//          partionSenderRootExec.getStats().stopProcessing();
//        }
//        if ( actualThreads == 1 ) {
//          assertEquals("With single thread parent and child waitNanos should match", partitioners.get(0).getStats().getWaitNanos(), partionSenderRootExec.getStats().getWaitNanos());
//        }
//
//        // testing values distribution
//        partitioners = partDecor.getPartitioners();
//        isFirst = true;
//        // since we have fake Nullvector distribution is skewed
//        for (Partitioner part : partitioners ) {
//          final List<PartitionOutgoingBatch> outBatches = (List<PartitionOutgoingBatch>) part.getOutgoingBatches();
//          for (PartitionOutgoingBatch partOutBatch : outBatches ) {
//            final int recordCount = ((VectorAccessible) partOutBatch).getRecordCount();
//            if ( isFirst ) {
//              assertEquals("RecordCount", 100, recordCount);
//              isFirst = false;
//            } else {
//              assertEquals("RecordCount", 0, recordCount);
//            }
//          }
//        }
//        // test exceptions within threads
//        // test stats merging
//        partionSenderRootExec.getStats().startProcessing();
//        try {
//          partDecor.executeMethodLogic(new InjectExceptionTest());
//          fail("Should throw IOException here");
//        } catch (IOException ioe) {
//          final OperatorProfile.Builder oPBuilder = OperatorProfile.newBuilder();
//          partionSenderRootExec.getStats().addAllMetrics(oPBuilder);
//          final List<MetricValue> metrics = oPBuilder.getMetricList();
//          for ( MetricValue metric : metrics) {
//            if ( Metric.BYTES_SENT.metricId() == metric.getMetricId() ) {
//              assertEquals("Should add metricValue irrespective of exception", 5*actualThreads, metric.getLongValue());
//            }
//            if (Metric.SENDING_THREADS_COUNT.metricId() == metric.getMetricId()) {
//              assertEquals(actualThreads, metric.getLongValue());
//            }
//          }
//          assertEquals(actualThreads-1, ioe.getSuppressed().length);
//        } finally {
//          partionSenderRootExec.getStats().stopProcessing();
//        }
//      } finally {
//        // cleanup
//        partionSenderRootExec.close();
//        context.close();
//      }
//    }
//  }
//
//  @Test
//  /**
//   * Testing partitioners distribution algorithm
//   * @throws Exception
//   */
//  public void testAlgorithm() throws Exception {
//    int outGoingBatchCount;
//    int numberPartitions;
//    int k = 0;
//    final Random rand = new Random();
//    while ( k < 1000 ) {
//      outGoingBatchCount = rand.nextInt(1000)+1;
//      numberPartitions = rand.nextInt(32)+1;
//      final int actualPartitions = outGoingBatchCount > numberPartitions ? numberPartitions : outGoingBatchCount;
//      final int divisor = Math.max(1, outGoingBatchCount/actualPartitions);
//
//      final int longTail = outGoingBatchCount % actualPartitions;
//      int startIndex = 0;
//      int endIndex = 0;
//      for (int i = 0; i < actualPartitions; i++) {
//        startIndex = endIndex;
//        endIndex = startIndex + divisor;
//        if ( i < longTail ) {
//          endIndex++;
//        }
//      }
//      assertTrue("endIndex can not be > outGoingBatchCount", endIndex == outGoingBatchCount );
//      k++;
//    }
//  }
//
//  /**
//   * Helper class to expose some functionality of PartitionSenderRootExec
//   *
//   */
//  private static class MockPartitionSenderRootExec extends PartitionSenderOperator {
//
//    public MockPartitionSenderRootExec(FragmentContext context,
//        RecordBatch incoming, HashPartitionSender operator)
//        throws OutOfMemoryException {
//      super(context, incoming, operator);
//    }
//
//    @Override
//    public void close() throws Exception {
//      ((AutoCloseable) oContext).close();
//    }
//
//    public int getNumberPartitions() {
//      return numberPartitions;
//    }
//
//    public OperatorStats getStats() {
//      return this.stats;
//    }
//  }
//
//  /**
//   * Helper class to inject exceptions in the threads
//   *
//   */
//  private static class InjectExceptionTest implements PartitionTask {
//
//    @Override
//    public void execute(Partitioner partitioner) throws IOException {
//      // throws IOException
//      partitioner.getStats().addLongStat(Metric.BYTES_SENT, 5);
//      throw new IOException("Test exception handling");
//    }
//  }
}
