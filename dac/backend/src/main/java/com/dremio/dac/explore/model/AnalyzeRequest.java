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
package com.dremio.dac.explore.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SQLAnalyze API Request
 */
public class AnalyzeRequest {

  private final String sql;
  private final List<String> context;
  private final int cursorPosition;

  @JsonCreator
  public AnalyzeRequest(@JsonProperty("sql") String sql,
                        @JsonProperty("context") List<String> context,
                        @JsonProperty("cursorPosition") int cursorPosition) {
    this.sql = sql;
    this.context = context;
    this.cursorPosition = cursorPosition;
  }

  /**
   * Get query SQL
   */
  public String getSql() {
    return sql;
  }

  /**
   * Get query context
   */
  public List<String> getContext() {
    return context;
  }

  /**
   * Get cursor position in SQL editor
   */
  public int getCursorPosition() {
    return cursorPosition;
  }

  @Override
  public String toString() {
    return "AnalyzeRequest{" +
      "sql='" + sql + '\'' +
      ", context=" + context +
      ", cursorPosition=" + cursorPosition +
      '}';
  }
}
