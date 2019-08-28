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
package com.dremio.exec.store.dfs;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.dremio.exec.catalog.CatalogServiceImpl;
import com.dremio.exec.catalog.ManagedStoragePlugin;
import com.dremio.exec.catalog.StoragePluginId;
import com.dremio.service.namespace.source.proto.SourceConfig;

/**
 * Test when the file limit is hit for directories when using external file systems.
 */
public class TestMaxFileLimit extends TestSubPathFileSystemPlugin {

  @ClassRule
  public static TemporaryFolder testFolder = TestSubPathFileSystemPlugin.testFolder;

  @BeforeClass
  public static void setup() throws Exception {
    testNoResult("alter system set \"%s\" = 1", FileDatasetHandle.DFS_MAX_FILES.getOptionName());
    TestSubPathFileSystemPlugin.generateTestData();
    addSubPathDfsExternalPlugin();
  }

  private static void addSubPathDfsExternalPlugin() throws Exception {
    final CatalogServiceImpl pluginRegistry = (CatalogServiceImpl) getSabotContext().getCatalogService();
    final ManagedStoragePlugin msp = pluginRegistry.getManagedSource("dfs_test");
    StoragePluginId pluginId = msp.getId();
    InternalFileConf nasConf = pluginId.getConnectionConf();
    nasConf.path = storageBase.getPath();
    nasConf.mutability = SchemaMutability.ALL;

    // Add one configuration for testing when internal is true
    nasConf.isInternal = false;

    SourceConfig config = pluginId.getConfig();
    config.setId(null);
    config.setTag(null);
    config.setConfigOrdinal(null);
    config.setName("subPathDfs");
    config.setConfig(nasConf.toBytesString());
    pluginRegistry.getSystemUserCatalog().createSource(config);
  }

  @Override
  @Test
  public void testTooManyFiles() throws Exception {
    errorMsgTestHelper("SELECT * FROM subPathDfs.\"largeDir\"",
      "VALIDATION ERROR: Number of files in dataset 'largeDir' contained 2 files which exceeds the maximum number of files of 1");
  }

  @Test
  public void testTooManyFilesAfterRefreshExternal() throws Exception {
    test("SELECT * FROM subPathDfs.\"largeDir2\"");

    // Add a new file to the large data set.
    final File tempFile = new File(storageBase, "largeDir2/tbl2.csv");
    generateTestDataFile(tempFile);

    // Should not be queryable due to refresh.
    try {
      errorMsgTestHelper("ALTER PDS subPathDfs.\"largeDir2\" REFRESH METADATA",
        "SYSTEM ERROR: FileCountTooLargeException: Number of files in dataset 'largeDir2' contained 2 files which exceeds the maximum number of files of 1");
    } finally {
      tempFile.delete();
    }

    // Verify re-queryable after the delete.
    test("ALTER PDS subPathDfs.\"largeDir2\" REFRESH METADATA");
    test("SELECT * FROM subPathDfs.\"largeDir2\"");
  }

  @AfterClass
  public static void shutdown() throws Exception {
    TestSubPathFileSystemPlugin.shutdown();
  }
}
