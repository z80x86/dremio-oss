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
package com.dremio.service.listing;

import java.util.List;
import java.util.Map.Entry;

import com.dremio.datastore.IndexedStore.FindByCondition;
import com.dremio.service.Service;
import com.dremio.service.namespace.NamespaceException;
import com.dremio.service.namespace.NamespaceKey;
import com.dremio.service.namespace.proto.NameSpaceContainer;
import com.dremio.service.namespace.source.proto.SourceConfig;

/**
 * Dataset listing service.
 * <p>
 * + This is a facade of {@link com.dremio.service.namespace.NamespaceService}.
 * + Unlike {@link com.dremio.service.namespace.NamespaceService}, which can have consumers only on coordinator nodes,
 * this service can have consumers on all nodes.
 */
public interface DatasetListingService extends Service {

  /**
   * List entries in namespace given the condition, for the user. If condition isnull, returns all items, for the user.
   * <p>
   * See {@link com.dremio.service.namespace.NamespaceService#find(FindByCondition)}.
   *
   * @param username  username
   * @param condition condition
   * @return search results
   * @throws NamespaceException if there are exceptions listing entries
   */
  Iterable<Entry<NamespaceKey, NameSpaceContainer>> find(String username, FindByCondition condition)
      throws NamespaceException;

  /**
   * List all sources in namespace.
   * <p>
   * See {@link com.dremio.service.namespace.NamespaceService#getSources()}.
   *
   * @param username username
   * @return a list of SourceConfig
   * @throws NamespaceException if there are exceptions listing entries
   */
  List<SourceConfig> getSources(String username)
      throws NamespaceException;

  /**
   * Get source in namespace.
   * <p>
   * See {@link com.dremio.service.namespace.NamespaceService#getSources()}.
   *
   * @param username username
   * @param sourcename sourcename
   * @return The SourceConfig associated with the sourcename
   * @throws NamespaceException if there are exceptions listing entries
   */
  SourceConfig getSource(String username, String sourcename)
      throws NamespaceException;

  DatasetListingService UNSUPPORTED = new DatasetListingService() {
    @Override
    public Iterable<Entry<NamespaceKey, NameSpaceContainer>> find(String username, FindByCondition condition) {
      throw new UnsupportedOperationException("non-master coordinators or executors do not support dataset listing");
    }

    @Override
    public List<SourceConfig> getSources(String username) {
      throw new UnsupportedOperationException("non-master coordinators or executors do not support dataset listing");
    }

    @Override
    public SourceConfig getSource(String username, String sourcename) {
      throw new UnsupportedOperationException("non-master coordinators or executors do not support dataset listing");
    }

    @Override
    public void start() {
    }

    @Override
    public void close() {
    }
  };
}
