/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator.model.operate.controller;

import junit.framework.TestCase;

import org.intranet.elevator.model.Floor;
import org.intranet.sim.event.EventQueue;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class AssignmentTest extends TestCase
{

  private Assignment a;
  private EventQueue eQ;
  private Floor floorA;
  private Floor floorB;
  
  public AssignmentTest(String name)
  {
    super(name);
  }
  
  protected void setUp() throws Exception
  {
    super.setUp();
    eQ = new EventQueue();
    floorA = new Floor(eQ, 1, 0.0f, 10.0f);
    floorB = new Floor(eQ, 2, 11.0f, 21.0f);
    a = new Assignment(floorA, Direction.UP);
  }

  final public void testAssignment()
  {
    Assignment a = new Assignment(floorA, Direction.UP);
    assertNotNull(a);
  }

  final public void testGetDestination()
  {
    assertEquals(floorA, a.getDestination());
  }

  final public void testGetDirection()
  {
    assertEquals(Direction.UP, a.getDirection());
  }

  /*
   * Test for boolean equals(Object)
   */
  final public void testEqualsTrue()
  {
    Assignment b = new Assignment(floorA, Direction.UP);
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
  }
  
  final public void testNullEqualsFalse()
  {
    assertFalse(a.equals(null));
  }
  
  final public void testInstanceEqualsFalse()
  {
    assertFalse(a.equals("a"));
  }
  
  final public void testDestinationEqualsFalse()
  {
    Assignment b = new Assignment(floorB, Direction.UP);
    assertFalse(a.equals(b));
  }
  
  final public void testDirectionEqualsFalse()
  {
    Assignment b = new Assignment(floorA, Direction.DOWN);
    assertFalse(a.equals(b));
  }

}
