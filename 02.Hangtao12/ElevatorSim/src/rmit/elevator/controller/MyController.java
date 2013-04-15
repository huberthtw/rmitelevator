/*
 * Copyright 2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package rmit.elevator.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

import remote.test.AssignmentTask;
import remote.test.PlanedController;
import remote.test.PlanedControllerList;
import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;

import remote.test.RemoteController;

import rmit.elevator.planner.AStar;

/**
 * @author Neil McKellar and Chris Dailey
 */
public class MyController
  implements Controller
{
  private List carControllers = new ArrayList();
  private List<RemoteCarController> carControllerlists=new ArrayList<RemoteCarController>();
  private HashSet<Assignment> assignments= new HashSet<Assignment>();
  private List floors=new ArrayList();
  public void initialize(EventQueue eQ)
  {
    carControllers.clear();
    floors=Building.getFloorList();
  }

  public void addCar(Car car, float stoppingDistance)
  {
    CarController controller = new RemoteController(car, stoppingDistance);
    carControllers.add(controller);
    carControllerlists.add(((RemoteController)controller).getController());
  }
/*
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
    CarControllerList carControllerList=
    		AStar.AStarSearch(assignments,carControllers);
    carControllers = carControllerList.getList();
    //System.out.println("#####AStar Finished!################################");
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      controller.setNextDestination();
      /*for(Iterator j=controller.getCarAssignments().iterator();j.hasNext();){
    	  Assignment ass=(Assignment) j.next();
    	  System.out.println(ass);
      }
      System.out.println("#####################################");*//*
    }
  }
*/
public void requestCar(Floor newFloor, Direction d){
	assignments.add(new Assignment(newFloor, d));

    //System.out.println("######Request Floors#####");
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      Car car=controller.getCar();
      List floors=car.getFloorRequestPanel().getRequestedFloors();
      for (Iterator j=floors.iterator();j.hasNext();){
    	  Assignment newAss=new CarRequestAssignment((Floor)j.next(),Direction.NONE,(RemoteController)controller);
    	  assignments.add(newAss);
    	  //System.out.println(newAss);
      }
    }
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
	  Car car=controller.getCar();
	  //String name=controller.getCar().getName();	
      for (RemoteCarController con:carControllerlists){
    	  if (con.getName().equals(car.getName())){
    		  Assignment ass;
    		  if (car.getDestination()!=null && car.getLocation()==null){
    			  ass = controller.getNearestBase();
    		  	  int di;
    		  	  if (ass.getDirection().isUp()) di=1;
    		  	  else if (ass.getDirection().isDown()) di=-1;
    		  	  else di=0;
    		  	  con.setState(ass.getDestination().getFloorNumber(), di);
    		  	  break;
    		  }
    		  if (car.getLocation()!=null) con.setState(car.getLocation().getFloorNumber(), 0);
    		  else con.setState(car.getFloorAt().getFloorNumber(), 0);
    		  break;
    	  }
      }
    }
    AssignmentTask assignmentTask=new AssignmentTask(carControllerlists,assignments);
    PlanedControllerList planedControllerList;
    ArrayList<PlanedController> planedControllers=new ArrayList<PlanedController>();
    HashSet<RemoteAssignment> assignmentlists=new HashSet<RemoteAssignment>();
	assignmentlists=assignmentTask.getAssignments();
	// TODO Auto-generated method stub
	
	planedControllerList = 
			AStar.AStarSearch(carControllerlists,assignmentlists);
	planedControllers=planedControllerList.getList();

	//carControllers=service.AStarSearch(assignments,carControllers);
    //System.out.println("#####################################");
	//System.out.println(assignments);
    //System.out.println("#####AStar Finished!################################");
    for (Iterator i = carControllers.iterator(); i.hasNext();)
    {
      CarController controller = (CarController)i.next();
      String name=controller.getCar().getName();
      controller.getCarAssignments().removeAll();
      for (PlanedController con:planedControllers){
    	  if (con.getName().equals(name)){
    		  ArrayList<Integer> array=new ArrayList<Integer>();
    		  array.addAll(con.getList());
    		  for (Integer j:array){
    			  controller.add(new Assignment((Floor)floors.get(j-1),new Direction("NONE")));
    		  }
    		  break;
    	  }
      }
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
