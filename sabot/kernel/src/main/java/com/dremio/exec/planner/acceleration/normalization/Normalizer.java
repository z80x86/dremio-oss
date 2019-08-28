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
package com.dremio.exec.planner.acceleration.normalization;

import org.apache.calcite.rel.RelNode;

/**
 * An interface that is used to normalize an incoming query for canonicalization.
 *
 * <p>
 * In general, used for pre-acceleration clean-up, common simplifications and canonicalization.
 * </p>
 */
public interface Normalizer {

  /**
   * Normalizes the given {@link RelNode query}
   *
   * @param query incoming query
   * @return normalized query
   */
  RelNode normalize(RelNode query);

}
