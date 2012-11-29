/*
 * Copyright 2003-2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.sim.clock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public abstract class Clock
{
  private boolean isRunning = false;
  private List listeners = new ArrayList();
  private FeedbackListener feedbackListener;

  protected long simulationTime;

  public interface Listener
  {
    void timeUpdate(long time);
    void stateUpdate(boolean running);
  }

  public interface FeedbackListener
  {
    long timeUpdate(long time);
  }

  public Clock(FeedbackListener c)
  {
    super();
    setFeedbackListener(c);
  }

  public final long getSimulationTime()
  {
    return simulationTime;
  }

  public final void addListener(Listener l)
  {
    listeners.add(l);
  }

  public final void setFeedbackListener(FeedbackListener l)
  {
    feedbackListener = l;
  }

  public final boolean isRunning()
  {
    return isRunning;
  }

  protected final void setRunningState(boolean newRunningState)
  {
    isRunning = newRunningState;
    for(Iterator i = listeners.iterator(); i.hasNext();)
    {
      Listener l = (Listener) i.next();
      l.stateUpdate(isRunning);
    }
  }

  protected final void setSimulationTime(long t)
  {
    simulationTime = feedbackListener.timeUpdate(t);
    for(Iterator i = listeners.iterator(); i.hasNext();)
    {
      Listener l = (Listener) i.next();
      l.timeUpdate(simulationTime);
    }
  }
  
  public abstract void dispose();
  public abstract void start();
  public abstract void pause();
}
