package au.edu.rmit.elevator.controller;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

import rmit.elevator.controller.CarController;
import rmit.elevator.controller.CarControllerList;
import rmit.elevator.controller.CarRequestAssignment;
import rmit.elevator.planner.AStar;

import au.edu.rmit.elevator.common.ElevatorCallback;
import au.edu.rmit.elevator.common.ElevatorService;
import au.edu.rmit.elevator.controller.JController.ElevatorCallbackImpl;

import au.edu.rmit.elevator.common.*;

public class JClientController implements Controller{
	ElevatorService service;
	Logger logger = Logger.getLogger("JController");
	private List carControllers = new ArrayList();
	private HashSet<Assignment> assignments= new HashSet<Assignment>();
	static int instance;
	
	public JClientController() {
		logger.info("There are " + (instance++) + " instance.");
	}
	
	public void initialize(EventQueue eQ){
		try {
			Registry registry = LocateRegistry.getRegistry(8081);
			service = (ElevatorService) registry.lookup("elevator");
			logger.info("Connected to Elevator Service");
			logger.info("Registering with elevator service");
			logger.info("Registered with elevator service. Ready for action");
			service.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    carControllers.clear();
		logger.info("Initializing");

	}

	public void addCar(Car car, float stoppingDistance){
	    CarController controller = new CarController(car, stoppingDistance);
	    carControllers.add(controller);
	}

	public void requestCar(Floor newFloor, Direction d){
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
	    CarControllerList carControllerList;
		try {
			carControllerList = 
					service.AStarSearch(assignments,carControllers);
			carControllers = carControllerList.getList();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		//carControllers=service.AStarSearch(assignments,carControllers);
	    //System.out.println("#####################################");
		//System.out.println(assignments);
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

	public String toString(){
	    return "JClintController";
	}

	public boolean arrive(Car car){
		CarController c = getController(car);
	    return c.arrive(assignments);
	}	

	public void setNextDestination(Car car){
	    CarController c = getController(car);
	    c.setNextDestination();
	}

	private CarController getController(Car car){
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
