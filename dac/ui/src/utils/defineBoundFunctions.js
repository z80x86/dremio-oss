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

export default function(object, functions) {
  Object.entries(functions).forEach(([key, value]) => {
    const symbol = Symbol(key);
    Object.defineProperty(object, symbol, {
      writable: true,
      enumerable: false,
      configurable: true,
      value: null
    });
    Object.defineProperty(object, key, {
      enumerable: false,
      configurable: true,
      get() {
        return this[symbol] = (this[symbol] || value.bind(this));
      },
      set(newValue) {
        return this[symbol] = newValue;
      }
    });
  });
}
