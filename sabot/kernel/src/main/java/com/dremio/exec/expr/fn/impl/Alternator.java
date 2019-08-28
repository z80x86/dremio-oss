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
package com.dremio.exec.expr.fn.impl;

import org.apache.arrow.vector.holders.NullableBigIntHolder;

import com.dremio.exec.expr.SimpleFunction;
import com.dremio.exec.expr.annotations.FunctionTemplate;
import com.dremio.exec.expr.annotations.Output;
import com.dremio.exec.expr.annotations.Workspace;

public class Alternator {

  @FunctionTemplate(name = "alternate", isDeterministic = false)
  public static class Alternate2 implements SimpleFunction{
    @Workspace int val;
    @Output NullableBigIntHolder out;

    public void setup() {
      val = 0;
    }


    public void eval() {
      out.isSet = 1;
      out.value = val;
      if(val == 0){
        val = 1;
      }else{
        val = 0;
      }
    }
  }

  @FunctionTemplate(name = "alternate3", isDeterministic = false)
  public static class Alternate3 implements SimpleFunction{
    @Workspace int val;
    @Output NullableBigIntHolder out;

    public void setup() {
      val = 0;
    }


    public void eval() {
      out.isSet = 1;
      out.value = val;
      if(val == 0){
        val = 1;
      }else if(val == 1){
        val = 2;
      }else{
        val = 0;
      }
    }
  }
}
