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
package com.dremio.dac.cmd;

/**
 * Utility class that encapsulates admin logging.
 */

public final class AdminLogger {

  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("admin");

  private AdminLogger(){}

  /**
   * This method logs admin tools messages to the user.
   * Formats the provided message by replacing the {} placeholders
   * with respective arguments
   * @param msg String message with placeholders for args
   * @param args Var args that are embedded in the msg for logging
   */
  public static void log(String msg, Object... args) {
    logger.info(msg, args);
  }

}
