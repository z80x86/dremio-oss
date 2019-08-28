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

public class MagicString {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MagicString.class);

  private long offset;
  private byte[] bytes;

  public MagicString(long offset, byte[] bytes) {
    super();
    this.offset = offset;
    this.bytes = bytes;
  }

  public long getOffset() {
    return offset;
  }

  public byte[] getBytes() {
    return bytes;
  }



}
