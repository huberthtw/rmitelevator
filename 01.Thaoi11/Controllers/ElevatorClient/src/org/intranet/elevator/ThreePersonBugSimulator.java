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
public class ThreePersonBugSimulator
  extends Simulator
{
  //  private IntegerParameter floorsParameter;
  private IntegerParameter carsParameter;
  
  private Building building;

  public ThreePersonBugSimulator()
  {
    super();
//    floorsParameter = new IntegerParameter("Number of Floors",10);
//    parameters.add(floorsParameter);
    carsParameter = new IntegerParameter("Insert second request at",29000);
    parameters.add(carsParameter);
  }
  
  public void initializeModel()
  {
//    int numFloors = floorsParameter.getValue();
    int numCars = carsParameter.getIntegerValue();

    building = new Building(getEventQueue(), 6, 1, new MetaController());
    
    createPerson(3, 0, 0);
    createPerson(1, 2, numCars);
//    createPerson(4, 8, 20000);
  }
  
  private void createPerson(int start, final int dest, long simTime)
  {
    final Person person = building.createPerson(building.getFloor(start));
    // insertion event for destination at time
    Event event = new Event(simTime)
    {
      public void perform()
      {
        person.setDestination(building.getFloor(dest));
      }
    };
    getEventQueue().addEvent(event);
  }

  public final Model getModel()
  {
    return building;
  }
  
  public String getDescription()
  {
    return "Three Person Trip Bug";
  }

  public Simulator duplicate()
  {
    return new ThreePersonBugSimulator();
  }
}
