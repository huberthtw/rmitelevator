/*
 * Copyright 2003 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator;

import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.model.operate.Person;
import org.intranet.elevator.model.operate.controller.MetaController;
import org.intranet.sim.Model;
import org.intranet.sim.Simulator;
import org.intranet.sim.event.Event;
import org.intranet.ui.IntegerParameter;

/**
 * @author Neil McKellar and Chris Dailey
 *
 */
public class NoIdleElevatorCarSimulator
  extends Simulator
{
  private IntegerParameter downDestParameter;
  private IntegerParameter upDestParameter;
  private IntegerParameter floorsParameter;
  private IntegerParameter carsParameter;
  
  private Building building;
  public NoIdleElevatorCarSimulator()
  {
    super();
    floorsParameter = new IntegerParameter("Number of Floors",5);
    parameters.add(floorsParameter);
    carsParameter = new IntegerParameter("Number of Elevators",1);
    parameters.add(carsParameter);
    upDestParameter = new IntegerParameter("Up Destination", 3);
    parameters.add(upDestParameter);
    downDestParameter = new IntegerParameter("Down Destination", 2);
    parameters.add(downDestParameter);
  }
  
  public void initializeModel()
  {
    int numFloors = floorsParameter.getIntegerValue();
    int numCars = carsParameter.getIntegerValue();
    final int upDest = upDestParameter.getIntegerValue() - 1;
    final int downDest = downDestParameter.getIntegerValue() - 1;

    building = new Building(getEventQueue(), numFloors, numCars,
        new MetaController());
    final Person a = building.createPerson(building.getFloor(1));
    Event eventA = new Event(0)
    {
      public void perform()
      {
        a.setDestination(building.getFloor(upDest));
      }
    };
    getEventQueue().addEvent(eventA);

    final Person c = building.createPerson(building.getFloor(3));
    Event eventC = new Event(0)
    {
      public void perform()
      {
        c.setDestination(building.getFloor(downDest));
      }
    };
    getEventQueue().addEvent(eventC);
  }

  public final Model getModel()
  {
    return building;
  }
  
  public String getDescription()
  {
    return "People Going Different Directions, Only One Car";
  }

  public Simulator duplicate()
  {
    return new NoIdleElevatorCarSimulator();
  }
}
