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
package com.dremio.exec.util;

import java.lang.reflect.Modifier;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;

public class GuavaPatcher {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GuavaPatcher.class);

  private static volatile boolean patched;

  public static synchronized void patch() {
    if (!patched) {
      try {
        patchStopwatch();
        patchCloseables();
        patched = true;
      } catch (Throwable e) {
        logger.warn("Unable to patch Guava classes.", e);
      }
    }
  }

  /**
   * Makes Guava stopwatch look like the old version for compatibility with hbase-server (for test purposes).
   */
  private static void patchStopwatch() throws Exception {

    final ClassPool pool = ClassPool.getDefault();
    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    if (contextClassLoader != null) {
      pool.insertClassPath(new LoaderClassPath(contextClassLoader));
    }

    CtClass cc = pool.get("com.google.common.base.Stopwatch");

    // Expose the constructor for Stopwatch for old libraries who use the pattern new Stopwatch().start().
    for (CtConstructor c : cc.getConstructors()) {
      if (!Modifier.isStatic(c.getModifiers())) {
        c.setModifiers(Modifier.PUBLIC);
      }
    }

    // Add back the Stopwatch.elapsedMillis() method for old consumers.
    CtMethod newmethod = CtNewMethod.make(
        "public long elapsedMillis() { return elapsed(java.util.concurrent.TimeUnit.MILLISECONDS); }", cc);
    cc.addMethod(newmethod);

    // Load the modified class instead of the original.
    cc.toClass();

    logger.info("Google's Stopwatch patched for old HBase Guava version.");
  }

  private static void patchCloseables() throws Exception {

    final ClassPool pool = ClassPool.getDefault();
    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    if (contextClassLoader != null) {
      pool.insertClassPath(new LoaderClassPath(contextClassLoader));
    }

    CtClass cc = pool.get("com.google.common.io.Closeables");


    // Add back the Closeables.closeQuietly() method for old consumers.
    CtMethod newmethod = CtNewMethod.make(
        "public static void closeQuietly(java.io.Closeable closeable) { try{closeable.close();}catch(Exception e){} }",
        cc);
    cc.addMethod(newmethod);

    // Load the modified class instead of the original.
    cc.toClass();

    logger.info("Google's Closeables patched for old HBase Guava version.");
  }

}
