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
package com.dremio.exec.catalog;

import static com.dremio.test.DremioTest.CLASSPATH_SCAN_RESULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dremio.common.config.LogicalPlanPersistence;
import com.dremio.common.config.SabotConfig;
import com.dremio.common.exceptions.UserException;
import com.dremio.concurrent.Runnables;
import com.dremio.connector.ConnectorException;
import com.dremio.connector.metadata.BytesOutput;
import com.dremio.connector.metadata.DatasetHandle;
import com.dremio.connector.metadata.DatasetHandleListing;
import com.dremio.connector.metadata.DatasetMetadata;
import com.dremio.connector.metadata.EntityPath;
import com.dremio.connector.metadata.GetDatasetOption;
import com.dremio.connector.metadata.GetMetadataOption;
import com.dremio.connector.metadata.ListPartitionChunkOption;
import com.dremio.connector.metadata.PartitionChunkListing;
import com.dremio.connector.metadata.extensions.ValidateMetadataOption;
import com.dremio.datastore.KVStore;
import com.dremio.datastore.KVStoreProvider;
import com.dremio.datastore.LocalKVStoreProvider;
import com.dremio.exec.catalog.conf.ConnectionConf;
import com.dremio.exec.catalog.conf.SourceType;
import com.dremio.exec.planner.logical.ViewTable;
import com.dremio.exec.server.SabotContext;
import com.dremio.exec.server.options.SystemOptionManager;
import com.dremio.exec.store.SchemaConfig;
import com.dremio.exec.store.StoragePluginRulesFactory;
import com.dremio.exec.store.sys.store.provider.KVPersistentStoreProvider;
import com.dremio.service.coordinator.ClusterCoordinator;
import com.dremio.service.listing.DatasetListingService;
import com.dremio.service.namespace.NamespaceKey;
import com.dremio.service.namespace.NamespaceService;
import com.dremio.service.namespace.SourceState;
import com.dremio.service.namespace.capabilities.SourceCapabilities;
import com.dremio.service.namespace.dataset.proto.DatasetConfig;
import com.dremio.service.namespace.source.proto.MetadataPolicy;
import com.dremio.service.namespace.source.proto.SourceConfig;
import com.dremio.service.namespace.source.proto.SourceInternalData;
import com.dremio.service.namespace.source.proto.UpdateMode;
import com.dremio.service.scheduler.Cancellable;
import com.dremio.service.scheduler.Schedule;
import com.dremio.service.scheduler.SchedulerService;
import com.dremio.test.DremioTest;
import com.google.common.collect.Sets;

/**
 *
 */
public class TestFailedToStartPlugin {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestFailedToStartPlugin.class);

  private SabotConfig sabotConfig;
  private SabotContext sabotContext;
  private KVStoreProvider storeProvider;
  private SchedulerService schedulerService;
  private PluginsManager plugins;
  private static MockUpPlugin mockUpPlugin;

  private static final String MOCK_UP = "mockup-failed-to-start";

  @Before
  public void setup() throws Exception {
    storeProvider = new LocalKVStoreProvider(CLASSPATH_SCAN_RESULT, null, true, false);
    storeProvider.start();
    final KVPersistentStoreProvider psp = new KVPersistentStoreProvider(
        new Provider<KVStoreProvider>() {
          @Override
          public KVStoreProvider get() {
            return storeProvider;
          }
        },
        true
    );
    final NamespaceService mockNamespaceService = mock(NamespaceService.class);
    final DatasetListingService mockDatasetListingService = mock(DatasetListingService.class);

    mockUpPlugin = new MockUpPlugin();
    MetadataPolicy rapidRefreshPolicy = new MetadataPolicy()
        .setAuthTtlMs(1L)
        .setDatasetUpdateMode(UpdateMode.PREFETCH)
        .setNamesRefreshMs(1L)
        .setDatasetDefinitionRefreshAfterMs(1L)
        .setDatasetDefinitionExpireAfterMs(1L);

    final SourceConfig mockUpConfig = new SourceConfig()
        .setName(MOCK_UP)
        .setMetadataPolicy(rapidRefreshPolicy)
        .setCtime(100L)
        .setConnectionConf(new MockUpConfig());

    when(mockDatasetListingService.getSources(any(String.class)))
        .thenReturn(Arrays.asList(mockUpConfig));
    when(mockDatasetListingService.getSource(any(String.class), any(String.class)))
        .thenReturn(mockUpConfig);

    when(mockNamespaceService.getSources())
        .thenReturn(Arrays.asList(mockUpConfig));
    when(mockNamespaceService.getSource(any(NamespaceKey.class)))
        .thenReturn(mockUpConfig);
    when(mockNamespaceService.getAllDatasets(any(NamespaceKey.class)))
        .thenReturn(Collections.emptyList());

    sabotConfig = SabotConfig.create();

    sabotContext = mock(SabotContext.class);
    // used in c'tor
    when(sabotContext.getClasspathScan())
        .thenReturn(CLASSPATH_SCAN_RESULT);
    when(sabotContext.getDatasetListing())
        .thenReturn(mockDatasetListingService);
    when(sabotContext.getNamespaceService(anyString()))
        .thenReturn(mockNamespaceService);

    final LogicalPlanPersistence lpp = new LogicalPlanPersistence(SabotConfig.create(), CLASSPATH_SCAN_RESULT);
    when(sabotContext.getLpPersistence())
        .thenReturn(lpp);
    when(sabotContext.getStoreProvider())
        .thenReturn(psp);

    final SystemOptionManager som = new SystemOptionManager(CLASSPATH_SCAN_RESULT, lpp, psp);
    som.init();
    when(sabotContext.getOptionManager())
        .thenReturn(som);

    when(sabotContext.isMaster())
      .thenReturn(true);

    // used in start
    when(sabotContext.getKVStoreProvider())
        .thenReturn(storeProvider);
    when(sabotContext.getConfig())
        .thenReturn(DremioTest.DEFAULT_SABOT_CONFIG);

    // used in newPlugin
    when(sabotContext.getRoles())
        .thenReturn(Sets.newHashSet(ClusterCoordinator.Role.MASTER));

    schedulerService = mock(SchedulerService.class);
  }

  /**
   * Simple counter
   */
  private class InvocationCounter {
    private volatile long count = 0;

    public long getCount() {
      return count;
    }

    public void incrementCount() {
      count++;
    }
  }

  private void mockScheduleInvocation(InvocationCounter counter) {
    doAnswer(new Answer<Cancellable>() {
      @Override
      public Cancellable answer(InvocationOnMock invocation) {
        final Object[] arguments = invocation.getArguments();
        final Runnable r = (Runnable) arguments[1];
        Runnables.executeInSeparateThread(new Runnable() {
          @Override
          public void run() {
            /* Next run scheduled for 100ms from now. Let's sleep for that 100ms to avoid the run failure of the
               runnable r (SingletonRunnable) which will skip secondary execution before its previous run exits.
             */
            try {
              Thread.sleep(100L);
            } catch (Exception e) {
              // ignored
            }
            counter.incrementCount();
            r.run();
          }

        });
        return mock(Cancellable.class);
      }
    }).when(schedulerService).schedule(any(Schedule.class), any(Runnable.class));
  }

  /**
   * Wait until metadata refresh counts up three times - as we added thread for initial step,
   * making sure the metadata refresh actually ran in the meantime
   * N.B., if we only wait for one upcount, we might race with the metadata refresh
   */
  private static void waitForRefresh(InvocationCounter counter) throws Exception {
    long initialCount = counter.getCount();
    while (counter.getCount() <= initialCount + 3) {
      Thread.sleep(1);
    }
  }

  @Test
  public void testFailedToStart() throws Exception {
    InvocationCounter counter = new InvocationCounter();
    mockScheduleInvocation(counter);

    KVStore<NamespaceKey, SourceInternalData> sourceDataStore = storeProvider.getStore(CatalogSourceDataCreator.class);
    plugins = new PluginsManager(sabotContext, sourceDataStore, schedulerService, ConnectionReader.of(sabotContext.getClasspathScan(), sabotConfig));

    mockUpPlugin.setThrowAtStart();
    assertEquals(0, mockUpPlugin.getNumFailedStarts());
    plugins.start();
    // mockUpPlugin should be failing over and over right around now
    waitForRefresh(counter);
    long currNumFailedStarts = mockUpPlugin.getNumFailedStarts();
    assertTrue(currNumFailedStarts > 1);
    mockUpPlugin.unsetThrowAtStart();
    waitForRefresh(counter);
    currNumFailedStarts = mockUpPlugin.getNumFailedStarts();
    waitForRefresh(counter);
    assertEquals(currNumFailedStarts, mockUpPlugin.getNumFailedStarts());
  }

  @Test
  public void testBadSourceAtStart() throws Exception {
    InvocationCounter counter = new InvocationCounter();
    mockScheduleInvocation(counter);

    KVStore<NamespaceKey, SourceInternalData> sourceDataStore = storeProvider.getStore(CatalogSourceDataCreator.class);
    plugins = new PluginsManager(sabotContext, sourceDataStore, schedulerService, ConnectionReader.of(sabotContext.getClasspathScan(), sabotConfig));

    mockUpPlugin.setSimulateBadState(true);
    assertEquals(false, mockUpPlugin.gotDatasets());
    plugins.start();
    // metadata refresh should be running right now
    waitForRefresh(counter);
    assertFalse(mockUpPlugin.gotDatasets());
    mockUpPlugin.setSimulateBadState(false);
    // Give metadata refresh a chance to run again
    waitForRefresh(counter);
    assertTrue(mockUpPlugin.gotDatasets());
    mockUpPlugin.unsetGotDatasets();
    waitForRefresh(counter);
    assertTrue(mockUpPlugin.gotDatasets());
  }

  @SourceType(value = MOCK_UP, configurable = false)
  public static class MockUpConfig extends ConnectionConf<MockUpConfig, MockUpPlugin> {

    @Override
    public MockUpPlugin newPlugin(SabotContext context, String name,
                                  Provider<StoragePluginId> pluginIdProvider) {
      return mockUpPlugin;
    }
  }

  public static class MockUpPlugin implements ExtendedStoragePlugin {
    boolean throwAtStart = false;
    long numFailedStarts = 0;
    boolean gotDatasets = false;
    boolean simulateBadState = false;

    @Override
    public boolean hasAccessPermission(String user, NamespaceKey key, DatasetConfig datasetConfig) {
      return true;
    }

    @Override
    public SourceState getState() {
      if (throwAtStart) {
        return SourceState.badState("throwAtStart is set");
      }
      if (simulateBadState) {
        return SourceState.badState("simulated bad state");
      }
      return SourceState.goodState();
    }

    @Override
    public SourceCapabilities getSourceCapabilities() {
      return SourceCapabilities.NONE;
    }

    @Override
    public ViewTable getView(List<String> tableSchemaPath, SchemaConfig schemaConfig) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends StoragePluginRulesFactory> getRulesFactoryClass() {
      return null;
    }

    @Override
    public void close() {
    }

    @Override
    public void start() {
      if (throwAtStart) {
        ++numFailedStarts;
        throw UserException.resourceError().build(logger);
      }
    }

    public void setThrowAtStart() {
      throwAtStart = true;
    }

    public void unsetThrowAtStart() {
      throwAtStart = false;
    }

    public long getNumFailedStarts() {
      return numFailedStarts;
    }

    public void unsetGotDatasets() {
      gotDatasets = false;
    }

    public boolean gotDatasets() {
      return gotDatasets;
    }

    public void setSimulateBadState(boolean value) {
      simulateBadState = value;
    }

    @Override
    public DatasetHandleListing listDatasetHandles(GetDatasetOption... options) {
      gotDatasets = true;
      return Collections::emptyIterator;
    }

    @Override
    public Optional<DatasetHandle> getDatasetHandle(EntityPath datasetPath, GetDatasetOption... options) {
      return Optional.empty();
    }

    @Override
    public DatasetMetadata getDatasetMetadata(
        DatasetHandle datasetHandle,
        PartitionChunkListing chunkListing,
        GetMetadataOption... options
    ) throws ConnectorException {
      throw new ConnectorException("invalid handle");
    }

    @Override
    public PartitionChunkListing listPartitionChunks(DatasetHandle datasetHandle, ListPartitionChunkOption... options)
        throws ConnectorException {
      throw new ConnectorException("invalid handle");
    }

    @Override
    public boolean containerExists(EntityPath containerPath) {
      return false;
    }

    @Override
    public BytesOutput provideSignature(DatasetHandle datasetHandle, DatasetMetadata metadata) {
      return BytesOutput.NONE;
    }

    @Override
    public MetadataValidity validateMetadata(
        BytesOutput signature,
        DatasetHandle datasetHandle,
        DatasetMetadata metadata,
        ValidateMetadataOption... options
    ) {
      return MetadataValidity.VALID;
    }
  }
}
