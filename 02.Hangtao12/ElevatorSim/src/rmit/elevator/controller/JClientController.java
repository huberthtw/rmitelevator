package rmit.elevator.controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.CarRequestPanel;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

import rmit.elevator.controller.CarController;
import java.rmi.Naming;  
import remote.test.AssignmentTask;  
import remote.test.AStarSearch;  
import remote.test.PlanedController;
import remote.test.PlanedControllerList;
import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;
import remote.test.RemoteController;
  
public class JClientController implements Controller{ 

	private List carControllers = new ArrayList();
    private List<RemoteCarController> carControllerlists=new ArrayList<RemoteCarController>();
	private HashSet<Assignment> assignments= new HashSet<Assignment>();
	private static AStarSearch search;
	private List floors=new ArrayList();
	public void initialize(EventQueue eQ){		 
        try {  
            search= (AStarSearch) Naming.lookup("rmi://127.0.0.1:2500/hello");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	    carControllers.clear();
	    floors=Building.getFloorList();
	}
	public static PlanedControllerList Search(AssignmentTask assignmentTask){
		PlanedControllerList newList=null;
		try {
			newList=search.Search(assignmentTask);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newList;
	}
	public void addCar(Car car, float stoppingDistance){
	    //CarController controller = new RemoteController(car, stoppingDistance);
	    CarController controller = new RemoteController(car, stoppingDistance,search);
	    carControllers.add(controller);
	    carControllerlists.add(((RemoteController)controller).getController());
	}

	public void requestCar(Floor newFloor, Direction d){
		boolean inCar=false;
		assignments.clear();
	    assignments.add(new Assignment(newFloor, d));
	    //System.out.println("######Request Floors#####");
	    for (Object f:floors){
	    	CarRequestPanel panel=((Floor)f).getCallPanel();
	    	if (panel.isDown() || panel.isUp()) 
	    	    assignments.add(new Assignment((Floor)f, Direction.NONE));
	    }
	    for (Iterator i = carControllers.iterator(); i.hasNext();)
	    {
	      CarController controller = (CarController)i.next();
	      Car car=controller.getCar();
	      List floors=car.getFloorRequestPanel().getRequestedFloors();
	      for (Iterator j=floors.iterator();j.hasNext();){
	    	  Assignment newAss=new CarRequestAssignment((Floor)j.next(),Direction.NONE,(RemoteController)controller);
	    	  assignments.add(newAss);
	    	  inCar=true;
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
		if (inCar) System.out.println(assignmentlists);
		// TODO Auto-generated method stub
		try {
			planedControllerList = 
					search.Search(assignmentTask);
			planedControllers=planedControllerList.getList();
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

	public String toString(){
	    return "JClintController";
	}

	public boolean arrive(Car car){
		CarController c = getController(car);
		return c.arrive();
//	    return c.arrive(assignments);
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
