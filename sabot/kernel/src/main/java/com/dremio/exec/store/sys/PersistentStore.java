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
package com.dremio.exec.store.sys;

import java.util.Iterator;
import java.util.Map;

/**
 * An abstraction used to store and retrieve instances of given value type.
 *
 * @param <V>  value type
 */
public interface PersistentStore<V> extends AutoCloseable {

  /**
   * Returns the value for the given key if exists, null otherwise.
   * @param key  lookup key
   */
  V get(String key);

  /**
   * Stores the (key, value) tuple in the store. Lifetime of the tuple depends upon the store implementation
   *
   * @param key  lookup key
   * @param value  value to store
   */
  void put(String key, V value);


  /**
   * Removes the value corresponding to the given key if exists, nothing happens otherwise.
   * @param key  lookup key
   */
  void delete(String key);

  /**
   * Returns an iterator of entries.
   */
  Iterator<Map.Entry<String, V>> getAll();

}
