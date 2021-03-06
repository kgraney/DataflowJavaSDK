/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.dataflow.sdk.transforms.windowing;

import static com.google.cloud.dataflow.sdk.testing.WindowFnTestUtils.runWindowFn;
import static com.google.cloud.dataflow.sdk.testing.WindowFnTestUtils.set;
import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests for CalendarWindows WindowFn.
 */
@RunWith(JUnit4.class)
public class CalendarWindowsTest {

  private static Instant makeTimestamp(int year, int month, int day, int hours, int minutes) {
    return new DateTime(year, month, day, hours, minutes, DateTimeZone.UTC).toInstant();
  }

  @Test
  public void testDays() throws Exception {
    Map<IntervalWindow, Set<String>> expected = new HashMap<>();

    final List<Long> timestamps = Arrays.asList(
        makeTimestamp(2014, 1, 1, 0, 0).getMillis(),
        makeTimestamp(2014, 1, 1, 23, 59).getMillis(),

        makeTimestamp(2014, 1, 2, 0, 0).getMillis(),
        makeTimestamp(2014, 1, 2, 5, 5).getMillis(),

        makeTimestamp(2015, 1, 1, 0, 0).getMillis(),
        makeTimestamp(2015, 1, 1, 5, 5).getMillis());


    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 1, 1, 0, 0),
            makeTimestamp(2014, 1, 2, 0, 0)),
        set(timestamps.get(0), timestamps.get(1)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 1, 2, 0, 0),
            makeTimestamp(2014, 1, 3, 0, 0)),
        set(timestamps.get(2), timestamps.get(3)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2015, 1, 1, 0, 0),
            makeTimestamp(2015, 1, 2, 0, 0)),
        set(timestamps.get(4), timestamps.get(5)));

    assertEquals(expected, runWindowFn(CalendarWindows.days(1), timestamps));
  }

  @Test
  public void testWeeks() throws Exception {
    Map<IntervalWindow, Set<String>> expected = new HashMap<>();

    final List<Long> timestamps = Arrays.asList(
        makeTimestamp(2014, 1, 1, 0, 0).getMillis(),
        makeTimestamp(2014, 1, 5, 5, 5).getMillis(),

        makeTimestamp(2014, 1, 8, 0, 0).getMillis(),
        makeTimestamp(2014, 1, 12, 5, 5).getMillis(),

        makeTimestamp(2015, 1, 1, 0, 0).getMillis(),
        makeTimestamp(2015, 1, 6, 5, 5).getMillis());


    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 1, 1, 0, 0),
            makeTimestamp(2014, 1, 8, 0, 0)),
        set(timestamps.get(0), timestamps.get(1)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 1, 8, 0, 0),
            makeTimestamp(2014, 1, 15, 0, 0)),
        set(timestamps.get(2), timestamps.get(3)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 12, 31, 0, 0),
            makeTimestamp(2015, 1, 7, 0, 0)),
        set(timestamps.get(4), timestamps.get(5)));

    assertEquals(expected,
        runWindowFn(CalendarWindows.weeks(1, DateTimeConstants.WEDNESDAY), timestamps));
  }

  @Test
  public void testMonths() throws Exception {
    Map<IntervalWindow, Set<String>> expected = new HashMap<>();

    final List<Long> timestamps = Arrays.asList(
        makeTimestamp(2014, 1, 1, 0, 0).getMillis(),
        makeTimestamp(2014, 1, 31, 5, 5).getMillis(),

        makeTimestamp(2014, 2, 1, 0, 0).getMillis(),
        makeTimestamp(2014, 2, 15, 5, 5).getMillis(),

        makeTimestamp(2015, 1, 1, 0, 0).getMillis(),
        makeTimestamp(2015, 1, 31, 5, 5).getMillis());


    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 1, 1, 0, 0),
            makeTimestamp(2014, 2, 1, 0, 0)),
        set(timestamps.get(0), timestamps.get(1)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 2, 1, 0, 0),
            makeTimestamp(2014, 3, 1, 0, 0)),
        set(timestamps.get(2), timestamps.get(3)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2015, 1, 1, 0, 0),
            makeTimestamp(2015, 2, 1, 0, 0)),
        set(timestamps.get(4), timestamps.get(5)));

    assertEquals(expected,
        runWindowFn(CalendarWindows.months(1), timestamps));
  }

  @Test
  public void testMultiMonths() throws Exception {
    Map<IntervalWindow, Set<String>> expected = new HashMap<>();

    final List<Long> timestamps = Arrays.asList(
        makeTimestamp(2014, 3, 5, 0, 0).getMillis(),
        makeTimestamp(2014, 10, 4, 23, 59).getMillis(),

        makeTimestamp(2014, 10, 5, 0, 0).getMillis(),
        makeTimestamp(2015, 3, 1, 0, 0).getMillis(),

        makeTimestamp(2016, 1, 5, 0, 0).getMillis(),
        makeTimestamp(2016, 1, 31, 5, 5).getMillis());


    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 3, 5, 0, 0),
            makeTimestamp(2014, 10, 5, 0, 0)),
        set(timestamps.get(0), timestamps.get(1)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2014, 10, 5, 0, 0),
            makeTimestamp(2015, 5, 5, 0, 0)),
        set(timestamps.get(2), timestamps.get(3)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2015, 12, 5, 0, 0),
            makeTimestamp(2016, 7, 5, 0, 0)),
        set(timestamps.get(4), timestamps.get(5)));

    assertEquals(expected, runWindowFn(
        CalendarWindows.months(7).withStartingMonth(2014, 3).beginningOnDay(5), timestamps));
  }

  @Test
  public void testYears() throws Exception {
    Map<IntervalWindow, Set<String>> expected = new HashMap<>();

    final List<Long> timestamps = Arrays.asList(
        makeTimestamp(2000, 5, 5, 0, 0).getMillis(),
        makeTimestamp(2010, 5, 4, 23, 59).getMillis(),

        makeTimestamp(2010, 5, 5, 0, 0).getMillis(),
        makeTimestamp(2015, 3, 1, 0, 0).getMillis(),

        makeTimestamp(2052, 1, 5, 0, 0).getMillis(),
        makeTimestamp(2060, 5, 4, 5, 5).getMillis());


    expected.put(
        new IntervalWindow(
            makeTimestamp(2000, 5, 5, 0, 0),
            makeTimestamp(2010, 5, 5, 0, 0)),
        set(timestamps.get(0), timestamps.get(1)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2010, 5, 5, 0, 0),
            makeTimestamp(2020, 5, 5, 0, 0)),
        set(timestamps.get(2), timestamps.get(3)));

    expected.put(
        new IntervalWindow(
            makeTimestamp(2050, 5, 5, 0, 0),
            makeTimestamp(2060, 5, 5, 0, 0)),
        set(timestamps.get(4), timestamps.get(5)));

    assertEquals(expected, runWindowFn(
        CalendarWindows.years(10).withStartingYear(2000).beginningOnDay(5, 5), timestamps));
  }

  @Test
  public void testTimeZone() throws Exception {
    Map<IntervalWindow, Set<String>> expected = new HashMap<>();

    DateTimeZone timeZone = DateTimeZone.forID("America/Los_Angeles");

    final List<Long> timestamps = Arrays.asList(
        new DateTime(2014, 1, 1, 0, 0, timeZone).getMillis(),
        new DateTime(2014, 1, 1, 23, 59, timeZone).getMillis(),

        new DateTime(2014, 1, 2, 8, 0, DateTimeZone.UTC).getMillis(),
        new DateTime(2014, 1, 3, 7, 59, DateTimeZone.UTC).getMillis());

    expected.put(
        new IntervalWindow(
            new DateTime(2014, 1, 1, 0, 0, timeZone).toInstant(),
            new DateTime(2014, 1, 2, 0, 0, timeZone).toInstant()),
        set(timestamps.get(0), timestamps.get(1)));

    expected.put(
        new IntervalWindow(
            new DateTime(2014, 1, 2, 0, 0, timeZone).toInstant(),
            new DateTime(2014, 1, 3, 0, 0, timeZone).toInstant()),
        set(timestamps.get(2), timestamps.get(3)));

    assertEquals(expected, runWindowFn(
        CalendarWindows.days(1).withTimeZone(timeZone),
        timestamps));
  }
}
