/*
 * Copyright 2004 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class FloorRequestPanel
{
  private final List floors = new ArrayList();
  private final List requestedFloors = new ArrayList();
  private final List listeners = new ArrayList();

  public final void addServicedFloor(final Floor floor)
  {
    floors.add(floor);
  }

  public final Iterator getServicedFloorsI()
  {
    return floors.iterator();
  }

  public final Floor getFloorAt(float height)
  {
    for (Iterator i = floors.iterator(); i.hasNext();)
    {
      Floor f = (Floor)i.next();
      if (f.getHeight() == height)
        return f;
    }
    return null;
  }

  public List getServicedFloors()
  {
    return floors;
  }
  
  public List getRequestedFloors()
  {
    return requestedFloors;
  }

  Floor getMaxFloor()
  {
    Floor maxFloor = null;
    float floorHeight = Float.MIN_VALUE;
    for (Iterator i = floors.iterator(); i.hasNext(); )
    {
      Floor f = (Floor)i.next();
      if (f.getHeight() > floorHeight)
      {
        floorHeight = f.getHeight();
        maxFloor = f; 
      }
    }
    return maxFloor;
  }

  Floor getMinFloor()
  {
    Floor minFloor = null;
    float floorHeight = Float.MAX_VALUE;
    for (Iterator i = floors.iterator(); i.hasNext(); )
    {
      Floor f = (Floor)i.next();
      if (f.getHeight() < floorHeight)
      {
        floorHeight = f.getHeight();
        minFloor = f; 
      }
    }
    return minFloor;
  }

  // called by Person
  public final void requestFloor(Floor floor)
  {
    if (!floors.contains(floor))
      throw new IllegalArgumentException("Cannot request unreachable floors.");
    if (! requestedFloors.contains(floor))
    {
      requestedFloors.add(floor);
      for (Iterator i = listeners.iterator(); i.hasNext();)
      {
        Listener listener = (Listener)i.next();
        listener.floorRequested(floor);
      }
    }
  }

  final void requestFulfilled(Floor floor)
  {
    if (!floors.contains(floor))
      throw new IllegalArgumentException("Cannot fulfill request for unreachable floor.  " + floor);
    requestedFloors.remove(floor);
  }

  public static interface Listener {
    void floorRequested(Floor floor);
  }

  public void addListener(Listener l)
  {
    listeners.add(l);
  }
}
