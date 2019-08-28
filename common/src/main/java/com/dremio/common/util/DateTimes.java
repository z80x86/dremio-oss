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
package com.dremio.common.util;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalDateTimes;

public class DateTimes {

  /*
   * Formatters used to convert from/to Dremio representation into Calcite representation
   * during constant reduction
   */
  public static final DateTimeFormatter CALCITE_LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  public static final DateTimeFormatter CALCITE_LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendValue(HOUR_OF_DAY, 2)
      .appendLiteral(':')
      .appendValue(MINUTE_OF_HOUR, 2)
      .appendLiteral(':')
      .appendValue(SECOND_OF_MINUTE, 2)
      .optionalStart()
      .appendFraction(NANO_OF_SECOND, 0, 9, true)
      .toFormatter();
  public static final DateTimeFormatter CALCITE_LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .append(CALCITE_LOCAL_DATE_FORMATTER)
      .appendLiteral(' ')
      .append(CALCITE_LOCAL_TIME_FORMATTER)
      .toFormatter();

  public static long toMillis(LocalDateTime localDateTime) {
    return LocalDateTimes.getLocalMillis(localDateTime);
  }

  public static long toMillis(DateTime dateTime) {
    return dateTime.toDateTime(DateTimeZone.UTC).getMillis();
  }

  public static int toMillisOfDay(final DateTime dateTime) {
    return dateTime.toDateTime(DateTimeZone.UTC).millisOfDay().get();
  }

  /**
   * Convert from JDBC date escape string format to utc millis, ignoring local
   * timezone.
   *
   * Note, the current implementation is ridiculous as it goes through two
   * conversions. Should be updated to no conversion.
   *
   * @param jdbcEscapeString
   * @return Milliseconds since epoch.
   */
  public static long toMillisFromJdbcDate(String jdbcEscapeString){
    return toMillis(new LocalDateTime(Date.valueOf(jdbcEscapeString).getTime()));
  }

  /**
   * Convert from JDBC timestamp escape string format to utc millis, ignoring local
   * timezone.
   *
   * Note, the current implementation is ridiculous as it goes through two
   * conversions. Should be updated to no conversion.
   *
   * @param jdbcEscapeString
   * @return Milliseconds since epoch.
   */
  public static long toMillisFromJdbcTimestamp(String jdbcEscapeString){
    return toMillis(new LocalDateTime(Timestamp.valueOf(jdbcEscapeString).getTime()));
  }

  public static DateTime toDateTime(LocalDateTime localDateTime) {
    return localDateTime.toDateTime(DateTimeZone.UTC);
  }
}
