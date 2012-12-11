/*
 * Copyright 2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package rmit.elevator.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.CarController;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

import rmit.elevator.planner.AStar;

/**
 * @author Neil McKellar and Chris Dailey
 */
public class MyController
  implements Controller
{
  private List carControllers = new ArrayList();
  private HashSet<Assignment> assignments= new HashSet<Assignment>();
  
  public void initialize(EventQueue eQ)
  {
    carControllers.clear();
  }

  public void addCar(Car car, float stoppingDistance)
  {
    CarController controller = new CarController(car, stoppingDistance);
    carControllers.add(controller);
  }

  public void requestCar(Floor newFloor, Direction d)
  {
	assignments.add(new Assignment(newFloor, d));

    //System.out.println("######Request Floors#####");
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      Car car=controller.getCar();
      List floors=car.getFloorRequestPanel().getRequestedFloors();
      for (Iterator j=floors.iterator();j.hasNext();){
    	  Assignment newAss=new CarRequestAssignment((Floor)j.next(),Direction.NONE,controller);
    	  assignments.add(newAss);
    	  //System.out.println(newAss);
      }
    }
    //System.out.println("#####################################");
	//System.out.println(assignments);
	AStar.AStarSearch(assignments,carControllers);
    //System.out.println("#####AStar Finished!################################");
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      controller.setNextDestination();
      /*for(Iterator j=controller.getCarAssignments().iterator();j.hasNext();){
    	  Assignment ass=(Assignment) j.next();
    	  System.out.println(ass);
      }
      System.out.println("#####################################");*/
    }
  }

  private CarController findBestCar(Floor floor, Direction direction)
  {
    // if only one car, duh
    if (carControllers.size() == 1) return (CarController)carControllers.get(0);

    CarController c = null;
    float lowestCost = Float.MAX_VALUE;
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      float cost = controller.getCost(floor, direction);
      if (cost < lowestCost)
      {
        c = controller;
        lowestCost = cost;
      }
      else if (cost == lowestCost)
      {
        // Previously, the simulation simply collected statistics.
        // With the addition of this comparison, the statistics being gathered
        // are affecting the outcome of the simulation.
        if (controller.getCar().getTotalDistance() < c.getCar().getTotalDistance())
          c = controller;
      }
    }

    return c;
  }

  public String toString()
  {
    return "MyController";
  }

  public boolean arrive(Car car)
  {
    CarController c = getController(car);
    return c.arrive(assignments);
  }

  public void setNextDestination(Car car)
  {
    CarController c = getController(car);
    c.setNextDestination();
  }

  private CarController getController(Car car)
  {
    CarController c = null;
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      if (controller.getCar() == car)
        c = controller;
    }
    return c;
  }
}
