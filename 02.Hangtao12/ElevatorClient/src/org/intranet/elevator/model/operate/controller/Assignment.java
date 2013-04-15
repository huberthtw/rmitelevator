/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator.model.operate.controller;

import org.intranet.elevator.model.Floor;


public class Assignment
{
  private final Floor destination;
  private final Direction direction;
  
  public Assignment(Floor floor, Direction dir)
  {
    destination = floor;
    direction = dir;
  }
  
  public Floor getDestination()
  {
    return destination;
  }
  
  public Direction getDirection()
  {
    return direction;
  }
  
  public boolean equals(Object o)
  {
    if (o == null || !(o instanceof Assignment))
      return false;
      
    Assignment a = (Assignment)o;
    return (a.destination == destination && a.direction == direction);
  }
  
  public int hashCode()
  {
    return destination.hashCode() + direction.hashCode();
  }
  
  public String toString()
  {
    return destination.getFloorNumber() + direction.toString();
  }
}