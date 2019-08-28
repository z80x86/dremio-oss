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
package com.dremio.dac.api;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serialize a timestamp to a ISO DateTime string.
 */
public class TimestampToISODateStringSerializer extends JsonSerializer<Long> {
  @Override
  public void serialize(Long timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    // Joda may throw an exception if the timestamp is MAX_LONG so protect ourselves here.
    try {
      DateTime dateTime = new DateTime(timestamp).withZone(DateTimeZone.UTC);
      jsonGenerator.writeObject(dateTime.toString(ISODateTimeFormat.dateTime()));
    } catch (IllegalFieldValueException e) {
      jsonGenerator.writeString("ERROR");
    }
  }
}
