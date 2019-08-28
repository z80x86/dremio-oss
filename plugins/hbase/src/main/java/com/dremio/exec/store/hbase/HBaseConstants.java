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
package com.dremio.exec.store.hbase;

import com.dremio.common.expression.SchemaPath;
import com.dremio.common.types.TypeProtos.MajorType;
import com.dremio.common.types.TypeProtos.MinorType;
import com.dremio.common.types.Types;

public interface HBaseConstants {
  public static final String ROW_KEY = "row_key";

  public static final SchemaPath ROW_KEY_PATH = SchemaPath.getSimplePath(ROW_KEY);

  public static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";

  public static final String HBASE_ZOOKEEPER_PORT = "hbase.zookeeper.property.clientPort";

  public static final MajorType COLUMN_FAMILY_TYPE = Types.optional(MinorType.STRUCT);

  public static final MajorType COLUMN_TYPE = Types.optional(MinorType.VARBINARY);
}
