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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import javax.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dremio.common.config.LogicalPlanPersistence;
import com.dremio.common.config.SabotConfig;
import com.dremio.common.exceptions.UserException;
import com.dremio.concurrent.Runnables;
import com.dremio.concurrent.SafeRunnable;
import com.dremio.connector.metadata.DatasetHandle;
import com.dremio.connector.metadata.EntityPath;
import com.dremio.datastore.KVStore;
import com.dremio.datastore.KVStoreProvider;
import com.dremio.datastore.LocalKVStoreProvider;
import com.dremio.exec.catalog.conf.ConnectionConf;
import com.dremio.exec.catalog.conf.SourceType;
import com.dremio.exec.server.SabotContext;
import com.dremio.exec.server.options.SystemOptionManager;
import com.dremio.exec.store.CatalogService;
import com.dremio.exec.store.SchemaConfig;
import com.dremio.exec.store.StoragePlugin;
import com.dremio.exec.store.sys.store.provider.KVPersistentStoreProvider;
import com.dremio.service.coordinator.ClusterCoordinator;
import com.dremio.service.listing.DatasetListingService;
import com.dremio.service.namespace.NamespaceKey;
import com.dremio.service.namespace.NamespaceService;
import com.dremio.service.namespace.SourceState;
import com.dremio.service.namespace.dataset.proto.DatasetConfig;
import com.dremio.service.namespace.source.proto.SourceConfig;
import com.dremio.service.namespace.source.proto.SourceInternalData;
import com.dremio.service.scheduler.Cancellable;
import com.dremio.service.scheduler.Schedule;
import com.dremio.service.scheduler.SchedulerService;
import com.dremio.test.DremioTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Unit tests for PluginsManager.
 */
public class TestPluginsManager {
  private KVStoreProvider storeProvider;
  private PluginsManager plugins;
  private SabotContext sabotContext;
  private SchedulerService schedulerService;

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
    final SabotConfig sabotConfig = SabotConfig.create();
    sabotContext = mock(SabotContext.class);

    // used in c'tor
    when(sabotContext.getClasspathScan())
        .thenReturn(CLASSPATH_SCAN_RESULT);
    when(sabotContext.getNamespaceService(anyString()))
        .thenReturn(mockNamespaceService);
    when(sabotContext.getDatasetListing())
        .thenReturn(mockDatasetListingService);

    final LogicalPlanPersistence lpp = new LogicalPlanPersistence(SabotConfig.create(), CLASSPATH_SCAN_RESULT);
    when(sabotContext.getLpPersistence())
        .thenReturn(lpp);
    when(sabotContext.getStoreProvider())
        .thenReturn(psp);

    final SystemOptionManager som = new SystemOptionManager(CLASSPATH_SCAN_RESULT, lpp, psp);
    som.init();
    when(sabotContext.getOptionManager())
        .thenReturn(som);

    // used in start
    when(sabotContext.getKVStoreProvider())
        .thenReturn(storeProvider);
    when(sabotContext.getConfig())
        .thenReturn(DremioTest.DEFAULT_SABOT_CONFIG);

    // used in newPlugin
    when(sabotContext.getRoles())
        .thenReturn(Sets.newHashSet(ClusterCoordinator.Role.MASTER));
    when(sabotContext.isMaster())
        .thenReturn(true);

    KVStore<NamespaceKey, SourceInternalData> sourceDataStore = storeProvider.getStore(CatalogSourceDataCreator.class);
    schedulerService = mock(SchedulerService.class);
    mockScheduleInvocation();
    plugins = new PluginsManager(sabotContext, sourceDataStore, schedulerService,
        ConnectionReader.of(sabotContext.getClasspathScan(), sabotConfig));
    plugins.start();
  }

  @After
  public void shutdown() throws Exception {
    if (plugins != null) {
      plugins.close();
    }

    if (storeProvider != null) {
      storeProvider.close();
    }
  }

  private void mockScheduleInvocation() {
    doAnswer(new Answer<Cancellable>() {
      @Override
      public Cancellable answer(InvocationOnMock invocation) {
        final Object[] arguments = invocation.getArguments();
        if (arguments[1] instanceof SafeRunnable) {
          return mock(Cancellable.class);
        }
        // allow thread that does first piece of work: scheduleMetadataRefresh
        // (that was not part of thread before) go through
        final Runnable r = (Runnable) arguments[1];
        Runnables.executeInSeparateThread(new Runnable() {
          @Override
          public void run() {
            r.run();
          }

        });
        return mock(Cancellable.class);
      } // using SafeRunnable, as Runnable is also used to run initial setup that used to run w/o any scheduling
    }).when(schedulerService).schedule(any(Schedule.class), any(Runnable.class));
  }

  private static final String INSPECTOR = "inspector";

  private static final EntityPath DELETED_PATH = new EntityPath(ImmutableList.of(INSPECTOR, "deleted"));

  private static final DatasetConfig datasetConfig = new DatasetConfig();

  private static final EntityPath ENTITY_PATH = new EntityPath(ImmutableList.of(INSPECTOR, "one"));
  private static final DatasetHandle DATASET_HANDLE = () -> ENTITY_PATH;

  @SourceType(value = INSPECTOR, configurable = false)
  public static class Inspector extends ConnectionConf<Inspector, StoragePlugin> {
    private final boolean hasAccessPermission;

    Inspector() {
      this.hasAccessPermission = true;
    }

    Inspector(boolean hasAccessPermission) {
      this.hasAccessPermission = hasAccessPermission;
    }

    @Override
    public StoragePlugin newPlugin(SabotContext context, String name, Provider<StoragePluginId> pluginIdProvider) {
      final ExtendedStoragePlugin mockStoragePlugin = mock(ExtendedStoragePlugin.class);
      try {
        when(mockStoragePlugin.listDatasetHandles())
            .thenReturn(Collections::emptyIterator);

        when(mockStoragePlugin.getDatasetHandle(eq(DELETED_PATH)))
            .thenReturn(Optional.empty());

        when(mockStoragePlugin.getDatasetHandle(eq(ENTITY_PATH)))
            .thenReturn((Optional) Optional.of(DATASET_HANDLE));

        when(mockStoragePlugin.getState())
            .thenReturn(SourceState.GOOD);

        when(mockStoragePlugin.hasAccessPermission(anyString(), any(), any())).thenReturn(hasAccessPermission);
      } catch (Exception ignored) {
        throw new IllegalStateException("will not throw");
      }

      return mockStoragePlugin;
    }

    @Override
    public boolean equals(Object other) {
      // this forces the replace call to always do so
      return false;
    }
  }

  @Test
  public void permissionCacheShouldClearOnReplace() throws Exception {
    final NamespaceKey sourceKey = new NamespaceKey(INSPECTOR);
    final SourceConfig inspectorConfig = new SourceConfig()
        .setType(INSPECTOR)
        .setName(INSPECTOR)
        .setMetadataPolicy(CatalogService.DEFAULT_METADATA_POLICY)
        .setConfig(new Inspector(true).toBytesString());

    final KVStore<NamespaceKey, SourceInternalData> kvStore = storeProvider.getStore(CatalogSourceDataCreator.class);

    // create one; lock required
    final ManagedStoragePlugin plugin;
    try (AutoCloseable ignored = plugins.writeLock()) {
      plugin = plugins.create(inspectorConfig);
      plugin.startAsync().checkedGet();
    }

    final SchemaConfig schemaConfig = mock(SchemaConfig.class);
    when(schemaConfig.getUserName()).thenReturn("user");
    final MetadataRequestOptions metadataRequestOptions = new MetadataRequestOptions(schemaConfig, 1000);

    // force a cache of the permissions
    plugin.checkAccess(new NamespaceKey("test"), datasetConfig, "user", metadataRequestOptions);

    // create a replacement that will always fail permission checks
    final SourceConfig newConfig = new SourceConfig()
        .setType(INSPECTOR)
        .setName(INSPECTOR)
        .setMetadataPolicy(CatalogService.DEFAULT_METADATA_POLICY)
        .setConfig(new Inspector(false).toBytesString());

    plugin.replacePlugin(newConfig, sabotContext, 1000);

    // will throw if the cache has been cleared
    boolean threw = false;
    try {
      plugin.checkAccess(new NamespaceKey("test"), datasetConfig, "user", metadataRequestOptions);
    } catch (UserException e) {
      threw = true;
    }

    assertTrue(threw);
  }
}
