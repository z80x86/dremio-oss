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
package com.dremio.exec.store;

/**
 * Listable entities under a schema.
 */
public class SchemaEntity {

  public enum SchemaEntityType {
    TABLE,
    FILE_TABLE, // file that has format settings or marked as physical dataset
    FOLDER_TABLE, //folder that has format settings or marked as physical dataset
    SUBSCHEMA,
    FILE, // a file on filesystem with no format settings
    FOLDER // most cases same as sub schema in the context of listing.
  }

  private final SchemaEntityType type;
  private final String owner;
  private final String path;

  public SchemaEntity(String path, SchemaEntityType type, String owner) {
    this.path = path;
    this.type = type;
    this.owner = owner;
  }

  @Override
  public String toString() {
    return type + ":" + path;
  }

  public SchemaEntityType getType() {
    return type;
  }

  public String getOwner() {
    return owner;
  }

  public String getPath() {
    return path;
  }
}
