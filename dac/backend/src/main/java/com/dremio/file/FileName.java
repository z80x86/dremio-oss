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
package com.dremio.file;

import javax.validation.constraints.Pattern;

import com.dremio.dac.model.common.LeafEntity;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * name of a file.
 */
public class FileName extends LeafEntity {
  private String name;

  @JsonCreator
  public FileName(String name) {
    super(name);

    this.name = name;
  }

  @Pattern(regexp = "^[^@:{/.][^@:{/]*$", message = "File name cannot start with a period, contain a colon, forward slash, at sign, or open curly bracket.")
  public String getName() {
    return super.getName();
  }

  @Override
  public LeafType getLeafType() {
    return LeafType.FILE;
  }
}
