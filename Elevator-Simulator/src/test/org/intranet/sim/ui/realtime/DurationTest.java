/*
 * Copyright 2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.sim.ui.realtime;

import junit.framework.TestCase;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class DurationTest
    extends TestCase
{

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(DurationTest.class);
  }

  public final void testMsFormat()
  {
    assertEquals("00.123", Duration.format(123));
  }

  public final void testSecFormat()
  {
    assertEquals("25.123", Duration.format(25123));
  }

  public final void testMinFormat()
  {
    assertEquals("03:45.123", Duration.format(225123));
  }

  public final void testHourFormat()
  {
    assertEquals("04:03:45.123", Duration.format(14625123));
  }

  public final void testDayFormat()
  {
    assertEquals("5 day 04:03:45.123", Duration.format(446625123));
  }

  public final void testMonthFormat()
  {
    assertEquals("3 month 5 day 04:03:45.123", Duration.format(8222625123L));
  }

  public final void testYearFormat()
  {
    assertEquals("4 year 3 month 5 day 04:03:45.123", Duration.format(134366625123L));
  }
}
