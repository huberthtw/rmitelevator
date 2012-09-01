/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.sim.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public final class EventQueue
{
  private long currentTime = -1; // Invalid time value initially

  private long lastTime;
  private long lastEventProcessTime;

  private SortedSet eventSet = new TreeSet(new Event.EventTimeComparator());
  
  public interface Listener
  {
    void eventAdded(Event e);
    
    void eventRemoved(Event e);
    
    void eventError(Exception ex);
  }
  
  private List listeners = new ArrayList();
  
  public void addEvent(Event event)
  {
//System.out.println("EventQueue event at currentTime=" + currentTime +
// " for time="+event.getTime()+ ", class="+event.getClass().getName());
	  /*
    if (event.getTime() < lastTime)
    {
      throw new IllegalArgumentException(
                  "Event occurs *before* the last time we processed: " +
                  event.getTime() + " < " + lastTime);
    }
    if ((currentTime != -1) && (event.getTime() < currentTime))
    {
      throw new IllegalArgumentException(
                "Event occurs *before* the current time: " +
                event.getTime() + " < " + currentTime);
    }
    */
    if (eventSet.contains(event))
    {
      throw new IllegalArgumentException("Cannot re-add an Event to the queue!");
    }
    eventSet.add(event);
    
    for(Iterator i = listeners.iterator(); i.hasNext();)
    {
      Listener listener = (Listener)i.next();
      listener.eventAdded(event);
    }
  }
  
  public void removeEvent(Event event)
  {
    if (!eventSet.contains(event))
    {
      throw new IllegalArgumentException("Cannot remove an Event that is not in the queue!");
    }
    eventSet.remove(event);
    for(Iterator i = listeners.iterator(); i.hasNext();)
    {
      Listener listener = (Listener)i.next();
      listener.eventRemoved(event);
    }
  }
  
  public List getEventList()
  {
    return new ArrayList(eventSet);
  }
  
  /**
   * Processes events in the event list up to the requested time.
   * The method throws an exception if the requested time is before the 
   * last processed time mark.  The remainging pending events that require
   * updates are also notified.
   * @param time The requested time to process up to in the list of events.
   * @throws RuntimeException When the requested time is before the last time.
   * @return true if events were processed
   */
  public boolean processEventsUpTo(long time)
  {
    if (time < lastTime)
    {
      throw new RuntimeException("Requested time is earlier than last time.");
    }

    int numEventsProcessed = 0;
    do
    {
      if (eventSet.size() == 0) break;  // can't process events if there aren't any
      Event currentEvent = (Event) eventSet.first();
      // Since eventSet is ordered, and we're only interested in processing events
      // up to 'time', if we find an event after 'time' then we stop processing
      // the Set.
      if (currentEvent.getTime() > time) break;
      // Now we know the event needs to be processed
      removeEvent(currentEvent);
      long oldCurrentTime = currentTime;
      currentTime = currentEvent.getTime();
      try {
        // If the time has progressed, we must update the TrackingUpdateEvents
        // so further calculations in Event.perform() are based on up-to-date
        // values.
        if (oldCurrentTime != currentTime)
          numEventsProcessed += updateEventProgress();

        lastEventProcessTime = currentTime;
        currentEvent.perform();
        numEventsProcessed++;
      } catch(Exception e) {
        e.printStackTrace();
        for (Iterator i = listeners.iterator(); i.hasNext(); )
        {
          Listener l = (Listener)i.next();
          l.eventError(e);
        }
      }
    } while (true);
    currentTime = time;
    numEventsProcessed += updateEventProgress();
    currentTime = -1;
    lastTime = eventSet.size() == 0 ? lastEventProcessTime : time;
    return (numEventsProcessed != 0);
  }

  private int updateEventProgress()
  {
    int numEventsProcessed = 0;
    // Update any events that have incremental progress between states
    for (Iterator i = eventSet.iterator(); i.hasNext();)
    {
      Event evt = (Event) i.next();
      if (evt instanceof IncrementalUpdateEvent)
      {
        IncrementalUpdateEvent updateEvent = (IncrementalUpdateEvent) evt;
        try {
          updateEvent.updateTime();
          numEventsProcessed++;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    return numEventsProcessed;
  }
  
  public void addListener(Listener listener)
  {
    listeners.add(listener);
  }
  
  public void removeListener(Listener listener)
  {
    listeners.remove(listener);
  }

  public long getCurrentTime()
  {
	//@Thoai: check this again
    /*if (currentTime == -1)
      throw new
        IllegalStateException("Current time is invalid when not processing events");*/
    return currentTime;
  }

  public long getLastEventProcessTime()
  {
    return lastEventProcessTime;
  }
}
