/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.sim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.intranet.sim.clock.Clock;
import org.intranet.sim.clock.ClockFactory;
import org.intranet.sim.event.EventQueue;
import org.intranet.ui.SingleValueParameter;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public abstract class Simulator
{
  private ClockFactory clockFactory;
  private boolean initialized;
  // TODO : Write tests
  private EventQueue eventQueue;
  private Clock clock;
  protected List parameters = new ArrayList();
  private List listeners = new ArrayList();
  Clock.FeedbackListener cc = new Clock.FeedbackListener()
  {
    public long timeUpdate(long time)
    {
      synchronized(getModel())
      {
        if (eventQueue.processEventsUpTo(time))
        {
          for (Iterator i = listeners.iterator(); i.hasNext(); )
          {
            SimulatorListener l = (SimulatorListener)i.next();
            l.modelUpdate(time);
          }
        }
        if (eventQueue.getEventList().size() == 0)
        {
          if (clock.isRunning())
            clock.pause();
          // This is a critical section.  The simulator uses this condition
          // to tell the Clock that it should not progress past the time
          // of the last (as in final) event of the simultation.
          // If the simultation framework is ever used for a
          // real-time simulation (such as a game that is ongoing),
          // this section would likely need to be changed, as the event
          // queue may be empty at times and user input may add events.
          return eventQueue.getLastEventProcessTime();
        }
        return time;
      }
    }
  };

  public interface SimulatorListener
  {
    void modelUpdate(long time);
  }
  public final void addListener(SimulatorListener sl)
  {
    listeners.add(sl);
  }
  public final void removeListener(SimulatorListener sl)
  {
    listeners.remove(sl);
  }

  protected Simulator()
  {
    super();
  }

  public final EventQueue getEventQueue()
  {
    if (eventQueue == null)
      throw new IllegalStateException("initialize() must be called before eventQueue is valid");
    return eventQueue;
  }

  public final Clock getClock()
  {
    if (clock == null)
      throw new IllegalStateException("initialize() must be called before clock is valid");
    return clock;
  }

  public final void initialize(ClockFactory cf)
  {
    // Stop the clock, we're starting over
    if (clock != null)
      clock.dispose();

    clockFactory = cf;
    eventQueue = new EventQueue();
    clock = clockFactory.createClock(cc);
    initializeModel();
    initialized = true;
  }
  
  public final boolean isInitializied()
  {
    return initialized; 
  }
  
  protected abstract void initializeModel();
  
  public abstract Model getModel();
  
  public final SingleValueParameter getParameter(String description)
  {
    for (Iterator i = getParameters().iterator(); i.hasNext(); )
    {
      SingleValueParameter p = (SingleValueParameter)i.next();
      if (p.getDescription().equals(description))
        return p; 
    }
    return null;
  }
  
  public abstract String getDescription();

  public final List getParameters()
  {
    return parameters;
  }
  
  public abstract Simulator duplicate();
}
