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
package io.protostuff;

/**
 * Byte string utilities.
 */
public final class ByteStringUtil {

  /**
   * Wrap the given byte array into a {@link ByteString}, to avoid copying.
   * <p>
   * Warning: use only in a scenario where the given byte array will never be modified by other actors.
   *
   * @param bytes byte array
   * @return byte string
   */
  public static ByteString wrap(byte[] bytes) {
    return ByteString.wrap(bytes);
  }

  /**
   * Unwrap the given {@link ByteString} into byte array, to avoid copying.
   * <p>
   * Warning: use only in a scenario where the unwrapped byte array will never be modified by other actors.
   *
   * @param byteString byte string
   * @return byte array
   */
  public static byte[] unwrap(ByteString byteString) {
    return byteString.getBytes();
  }

  // prevent instantiation
  private ByteStringUtil() {
  }
}
