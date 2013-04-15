/*
 * Copyright 2003-2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package rmit.elevator.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.CarEntrance;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.FloorRequestPanel;
import org.intranet.elevator.model.FloorRequestPanel.Listener;
import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.CarAssignments;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.elevator.model.operate.controller.FloorContext;

import remote.test.AStarSearch;
import remote.test.AssignmentTask;
import remote.test.PlanedController;
import remote.test.PlanedControllerList;
import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;
import remote.test.RemoteController;
import rmit.elevator.planner.AStar;

/**
 * @author Neil McKellar and Chris Dailey
 * SOON : Still confusing, keep refactoring.
 */
public class CarController
{
  private final AStarSearch search;
  private final Car car;
  private final float stoppingDistance;
  private final CarAssignments assignments;
  private List floors=new ArrayList();
  public CarController(Car c, float stoppingDist, Listener listener) {
		// TODO Auto-generated constructor stub

	    super();
	    search=null;
	    car = c;
	    stoppingDistance = stoppingDist;
	    assignments = new CarAssignments(car.getName());

	    floors=Building.getFloorList();
	    
	    car.getFloorRequestPanel().addListener(listener);
  }
  public CarController(Car c, float stoppingDist)
  {
    super();
    search=null;
    car = c;
    stoppingDistance = stoppingDist;
    assignments = new CarAssignments(car.getName());

    floors=Building.getFloorList();
    
    car.getFloorRequestPanel().addListener(new FloorRequestPanel.Listener()
    {
      public void floorRequested(Floor destinationFloor)
      {  
    	List<CarController> carControllers = new ArrayList<CarController>();
    	HashSet<Assignment> tempAssignments= new HashSet<Assignment>();
    	carControllers.add((CarController) getCarController());
    	//System.out.println(carControllers);
    	for (Iterator i=assignments.iterator();i.hasNext();){
    		tempAssignments.add((Assignment)i.next());
    	}
    	tempAssignments.add(new Assignment(destinationFloor,Direction.NONE));
    	
    	CarControllerList carControllerList=
    			AStar.AStarSearch(tempAssignments, carControllers);
    	carControllers = carControllerList.getList();
    	for (Iterator i = carControllers.iterator(); i.hasNext();)
        {
          CarController controller = (CarController)i.next();
          controller.setNextDestination();
        //  for(Iterator j=controller.getCarAssignments().iterator();j.hasNext();){
        //	  Assignment ass=(Assignment) j.next();
        //	 // System.out.println(ass);
        //  }
          //System.out.println("#####################################");
        }
      }
    });/*
    car.getFloorRequestPanel().addListener(new FloorRequestPanel.Listener()
    {
      public void floorRequested(Floor destinationFloor)
      {
        float currentHeight = car.getHeight();
        if (destinationFloor.getHeight() > currentHeight)
        {
          addDestination(destinationFloor, Direction.UP);
        }
        else if (destinationFloor.getHeight() < currentHeight)
        {
          addDestination(destinationFloor, Direction.DOWN);
        }
        else
          throw new RuntimeException("Do we really want to go to the current floor?");
          // Maybe some day we can just turn off the button
//        getFloorRequest(floor).setRequested(false, currentTime);
      }
    });*/
  }
  public CarController(Car c, float stoppingDist, AStarSearch searchServer)
  {
    super();
    this.search=searchServer;
    car = c;
    stoppingDistance = stoppingDist;
    floors=Building.getFloorList();
    assignments = new CarAssignments(car.getName());
    
    car.getFloorRequestPanel().addListener(new FloorRequestPanel.Listener()
    {
      public void floorRequested(Floor destinationFloor)
      {  
    	List<RemoteCarController> carControllers = new ArrayList<RemoteCarController>();
    	RemoteCarController remoteCar=((RemoteController) getCarController()).getController();
    	Assignment ass;
    	if (car.getDestination()!=null && car.getLocation()==null){
    		ass = getNearestBase();
    		int di;
    		if (ass.getDirection().isUp()) di=1;
    		else if (ass.getDirection().isDown()) di=-1;
    		else di=0;
    		remoteCar.setState(ass.getDestination().getFloorNumber(), di);
    	}else if (car.getLocation()!=null) remoteCar.setState(car.getLocation().getFloorNumber(), 0);
    	else remoteCar.setState(car.getFloorAt().getFloorNumber(), 0);
    	HashSet<Assignment> tempAssignments= new HashSet<Assignment>();
    	carControllers.add(remoteCar);
    	//System.out.println(carControllers);
    	for (Iterator i=assignments.iterator();i.hasNext();){
    		tempAssignments.add((Assignment)i.next());
    	}
    	tempAssignments.add(new Assignment(destinationFloor,Direction.NONE));	
    	//System.out.println("Assignment Task:");
    	//for (Assignment task:tempAssignments){
        //	System.out.print(task+",");
    	//}
    	//System.out.println();
	    AssignmentTask assignmentTask=new AssignmentTask(carControllers,tempAssignments);
	    PlanedControllerList planedControllerList;
	    ArrayList<PlanedController> planedControllers=new ArrayList<PlanedController>();
	    HashSet<RemoteAssignment> assignmentlists=new HashSet<RemoteAssignment>();
		assignmentlists=assignmentTask.getAssignments();
		//planedControllerList = 
		//		JClientController.Search(assignmentTask);
		//planedControllers=planedControllerList.getList();
		try {
			planedControllerList = 
					search.Search(assignmentTask);
			planedControllers=planedControllerList.getList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	      assignments.removeAll();
   		  ArrayList<Integer> array=new ArrayList<Integer>();
   		  array.addAll(planedControllers.get(0).getList());
   		  for (Integer j:array){
   			  assignments.add(new Assignment((Floor)floors.get(j-1),new Direction("NONE")));
   		  }
	      setNextDestination();
      }
    });
  }
  
  public Car getCar()
  {
    return car;
  }

  Floor getDestination()
  {
    Assignment current = assignments.getCurrentAssignment();
    if (current == null)
      return null;
    return current.getDestination();
  }

  public float getCost(Floor floor, Direction destinationDirection)
  {
    // TODO: In morning simulation, cars get stuck on the top floor
    Assignment a = new Assignment(floor, destinationDirection);
    
    if (assignments.getCurrentAssignment() == null)
    {
      // don't care about direction
      float time = car.getTravelTime(floor);
      return time;
    }

    // Don't send another elevator to do the work if this elevator is already
    // doing it.
    if (assignments.contains(a))
        return 0.0F;

    // factors:
    // 1. how much will it slow down the elevator in processing existing
    //    tasks?
    // 2. how long will it take for elevator to arrive *******
    // 3. how does this affect the distribution of elevators in the system?
    //    (probably would eventually be in Building)
    float cost = 0.0F;

    // For now, only #2 above is implemented.
    float currentHeight = car.getHeight();
    for (Iterator allDestinations =
          assignments.iteratorIncluding(car.getFloorRequestPanel().getServicedFloors(),
              getNearestBase(), a);
           allDestinations.hasNext(); )
    {
      Assignment nextAssignment = (Assignment)allDestinations.next();
      Floor nextDestination = nextAssignment.getDestination();
      float nextHeight = nextDestination.getHeight();

      // stop condition when destination would be reached.
      // LATER: continue to evaluate total trip cost vs. partial trip cost
//      if (a.equals(nextAssignment))
//      {
//        cost += car.getTravelTime(nextDestination.getHeight() - currentHeight);
//        return cost;
//      }

      // accumulator for number of stops
      cost += floor.getCarEntranceForCar(car).getDoor().getMinimumCycleTime();

      // accumulator for total distance
      cost += car.getTravelTime(nextHeight - currentHeight);

      currentHeight = nextHeight;
      
      if (nextAssignment.equals(a)) break;
    }

    // all destinations have been accumulated, and we did not add this stop.
    // So now the stop must be added specifically from the last stop.
    cost += car.getTravelTime(floor.getHeight() - currentHeight);
    return cost;
  }
  
  /**
   * The nearest base is the nearest floor we could reasonably stop at.
   */
  public Assignment getNearestBase()
  {
    Assignment current = assignments.getCurrentAssignment();
    Direction currAssignmentDirection = (current == null) ?
        Direction.NONE : current.getDirection();

    // The first case is the car is docked
    Floor carLocation = car.getLocation();
    if (carLocation != null)
    {
      final CarEntrance entrance = carLocation.getCarEntranceForCar(car);
      Direction dockedDirection = entrance.isUp() ? Direction.UP :
         entrance.isDown() ? Direction.DOWN :
         currAssignmentDirection;
      return new Assignment(carLocation, dockedDirection);
    }

    // The second case is the car is idle
    Floor f = car.getFloorAt();
    if (f != null)
      return new Assignment(f, currAssignmentDirection);

    // Finally, the third case is the car is travelling
    float currentHeight = car.getHeight();
    Direction carDirection =
      (current.getDestination().getHeight() < currentHeight)?
        Direction.DOWN : Direction.UP;

    List floors = car.getFloorRequestPanel().getServicedFloors();
    for (Iterator i = createFloorContexts(floors, carDirection); i.hasNext(); )
    {
      FloorContext context = (FloorContext)i.next();
      if (context.contains(currentHeight))
      {
        float distance = Math.abs(context.getNext().getHeight() - currentHeight);
        boolean canCarStop = distance >= stoppingDistance;
        if (canCarStop || context.getNext() == getDestination())
          return new Assignment(context.getNext(), carDirection);
        return new Assignment(context.getSuccessor(), carDirection);
      }
    }
    throw new IllegalStateException("The car is somehow not between two floors.");
  }

  /**
   * @param floors
   * @param carDirection
   */
  private Iterator createFloorContexts(List floors, final Direction carDirection)
  {
    List sortedFloors = new ArrayList(floors);
    Collections.sort(sortedFloors, new Comparator()
    {
      public int compare(Object arg0, Object arg1)
      {
        Floor floor0 = (Floor)arg0;
        Floor floor1 = (Floor)arg1;
        float difference = floor0.getHeight() - floor1.getHeight();
        if (!carDirection.isUp())
          difference = -difference;
        if (difference > 0)
          return 1;
        if (difference < 0)
          return -1;
        return 0;
      }
    });
    List floorContexts = new ArrayList();
    for (int floorNum = 0; floorNum < sortedFloors.size() - 1; floorNum++)
    {
      Floor previous = (Floor)sortedFloors.get(floorNum);
      Floor next = (Floor)sortedFloors.get(floorNum + 1);
      Floor successor;
      if (floorNum == sortedFloors.size() - 2)
        successor = next;
      else
        successor = (Floor)sortedFloors.get(floorNum + 2);
      FloorContext set = new FloorContext(previous, next, successor);
      floorContexts.add(set);
    }
    return floorContexts.iterator();
  }

  public void addDestination(Floor d, Direction direction)
  {
    Assignment newAssignment = new Assignment(d, direction);
    Assignment baseAssignment = getNearestBase();

    List floorList = car.getFloorRequestPanel().getServicedFloors();
    assignments.addAssignment(floorList, baseAssignment, newAssignment);
//  LATER: Can we delete the commented out check for DOCKED in addDestination()?
//    if (car.getState() != Car.State.DOCKED)
      car.setDestination(assignments.getCurrentAssignment().getDestination());
  }

  public boolean arrive()
  {
    Floor location = car.getLocation();
    List serviceFloors = car.getFloorRequestPanel().getServicedFloors();
    Floor topFloor = (Floor)serviceFloors.get(serviceFloors.size()-1); 
    Floor bottomFloor = (Floor)serviceFloors.get(0);

    // remove from up/down list
    Assignment currentAssignment = assignments.getCurrentAssignment();
    if (currentAssignment!=null) assignments.removeAssignment(currentAssignment);
    // If the next assignment is on the same floor but going the other way
    // the doors would close and re-open.
    // To prevent this, we can remove that assignment and indicate that
    // we're at the "extreme" position, ready to go the other direction.
    Assignment newAssignment = assignments.getCurrentAssignment();
    if (newAssignment != null && newAssignment.getDestination() == location)
    {
      assignments.removeAssignment(newAssignment);
      if (currentAssignment.getDirection() == Direction.UP)
        topFloor = location;
      else
        bottomFloor = location;
      newAssignment = assignments.getCurrentAssignment();
    }
    /*if (newAssignment!=null){
    	Floor floor=newAssignment.getDestination();
    	if (!serviceFloors.contains(floor)){
    		if (newAssignment.getDirection().isUp()){
    			if (!floor.getCallPanel().isUp())
    				assignments.removeAssignment(newAssignment);
    		}else{
    			if (!floor.getCallPanel().isDown())
    				assignments.removeAssignment(newAssignment);
    		}
    	}
    }*/
    boolean wasUp = currentAssignment.getDirection().isUp();
    boolean atExtreme = (wasUp && location == topFloor) ||
                        (!wasUp && location == bottomFloor);
    boolean isUp = atExtreme ? !wasUp : wasUp;
    return isUp;
  }

  public void setNextDestination()
  { Floor location = car.getLocation();
    Assignment newAssignment = assignments.getCurrentAssignment();
    if (location!=null && !location.getCallPanel().isDown() && !location.getCallPanel().isUp()){
    	while (newAssignment != null && newAssignment.getDestination() == location)
    	{
    		assignments.removeAssignment(newAssignment);
    		newAssignment = assignments.getCurrentAssignment();
    	}
    }
    if (newAssignment!=null) car.setDestination(newAssignment.getDestination());
  }

  public boolean arrive(HashSet<Assignment> RequestAssignments) {
	// TODO Auto-generated method stub
	 Floor location = car.getLocation();
	 List serviceFloors = car.getFloorRequestPanel().getServicedFloors();
	 Floor topFloor = (Floor)serviceFloors.get(serviceFloors.size()-1); 
	 Floor bottomFloor = (Floor)serviceFloors.get(0);
    // remove from up/down list
     Assignment currentAssignment = assignments.getCurrentAssignment();
     if (RequestAssignments.contains(currentAssignment)) RequestAssignments.remove(currentAssignment);
	 assignments.removeAssignment(currentAssignment);
	 // If the next assignment is on the same floor but going the other way
	 // the doors would close and re-open.
     // To prevent this, we can remove that assignment and indicate that
     // we're at the "extreme" position, ready to go the other direction.
     Assignment newAssignment = assignments.getCurrentAssignment();
     while (newAssignment != null && newAssignment.getDestination() == location)
     {
       assignments.removeAssignment(newAssignment);
       if (RequestAssignments.contains(newAssignment)) RequestAssignments.remove(newAssignment);
       if (currentAssignment.getDirection() == Direction.UP)
         topFloor = location;
       else
	     bottomFloor = location;
	     newAssignment = assignments.getCurrentAssignment();
	   }
	    /*if (newAssignment!=null){
	    	Floor floor=newAssignment.getDestination();
	    	if (!serviceFloors.contains(floor)){
	    		if (newAssignment.getDirection().isUp()){
	    			if (!floor.getCallPanel().isUp())
	    				assignments.removeAssignment(newAssignment);
	    		}else{
	    			if (!floor.getCallPanel().isDown())
	    				assignments.removeAssignment(newAssignment);
	    		}
	    	}
	    }*/
	    boolean wasUp = currentAssignment.getDirection().isUp();
	    boolean atExtreme = (wasUp && location == topFloor) ||
	                        (!wasUp && location == bottomFloor);
	    boolean isUp = atExtreme ? !wasUp : wasUp;
	    return isUp;
  }
  
  public void addInFront(Assignment assignment) {
	// TODO Auto-generated method stub
	assignments.addInFront(assignment);
  }

  public CarAssignments getCarAssignments() {
	// TODO Auto-generated method stub
	return assignments;
  }
  public CarController getCarController(){
	  return this;
  }

  public void add(Assignment assignment) {
	// TODO Auto-generated method stub
	assignments.add(assignment);
  }
}
