/*
 * Copyright 2004 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator.model;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.intranet.elevator.RandomElevatorSimulator;
import org.intranet.sim.clock.Clock;
import org.intranet.sim.clock.ClockFactory;
import org.intranet.sim.clock.RealTimeClock;

/**
 * @author Neil McKellar and Chris Dailey
 *  
 */
public class ResultsCheck
  extends TestCase
{

  public static void main(String[] args)
  {
    TestRunner.run(ResultsCheck.class);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp()
    throws Exception
  {
    super.setUp();
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown()
    throws Exception
  {
    super.tearDown();
  }

  /*
   * Class under test for Simulator clone(ClockFactory)
   */
  public final void testCloneClockFactory()
  {
  }

  public final void testFixedElevatorSimulator()
  {
    ClockFactory clockFactory = new ClockFactory()
    {
      public Clock createClock(final Clock.FeedbackListener cl)
      {
        return new Clock(cl)
        {
          public void dispose()
          {
          }

          public void pause()
          {
            setRunningState(false);
          }

          public void start()
          {
            if (isRunning())
              throw new IllegalStateException("Can't start while already running");
            setRunningState(true);
            setSimulationTime(999999999);
          }
        };
      }
    };

  }
  public final void testRandomElevatorSimulator()
  {
    RandomElevatorSimulator res = new RandomElevatorSimulator();
    res.getParameter("Number of Floors").setValueFromUI("3");
    res.getParameter("Number of Elevators").setValueFromUI("1");
    res.getParameter("Number of People").setValueFromUI("11");
    res.getParameter("Rider insertion time (ms)").setValueFromUI("500");
    res.initialize(new RealTimeClock.RealTimeClockFactory());
    ((RealTimeClock)res.getClock()).setTimeConversion(20);
    res.getClock().start();
    while (res.getClock().isRunning())
    {
      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    assertEquals(0, res.getEventQueue().getEventList().size());
    assertFalse(res.getClock().isRunning());
    // SOON: figure out what this tests
    assertEquals(110187, res.getClock().getSimulationTime());
  }

  public final void testInitialize()
  {
  }
}