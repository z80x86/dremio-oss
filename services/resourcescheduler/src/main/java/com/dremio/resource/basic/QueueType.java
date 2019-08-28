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
package com.dremio.resource.basic;

/**
 * QueueType for basic resource management
 */
public enum QueueType {
  // TODO figure out split between capacities for below queues
  SMALL(30D),
  LARGE(30D),
  REFLECTION_SMALL(25D),
  REFLECTION_LARGE(15D);

  private double capacity;

  QueueType(double capacity) {
    this.capacity = capacity;
  }

  public double getCapacity() {
    return capacity;
  }
}
