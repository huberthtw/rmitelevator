/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.intranet.elevator.model.operate.controller.AssignmentTest;
import org.intranet.elevator.model.operate.controller.CarAssignmentsTest;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class ModelTests
{

  public static void main(String[] args)
  {
    junit.swingui.TestRunner.run(ModelTests.class);
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite("Test for org.intranet.elevator.model");
    //$JUnit-BEGIN$
    suite.addTest(new TestSuite(AssignmentTest.class));
    suite.addTest(new TestSuite(CarAssignmentsTest.class));
    suite.addTest(new TestSuite(ChangeDirectionTest.class));
    //$JUnit-END$
    return suite;
  }
}
