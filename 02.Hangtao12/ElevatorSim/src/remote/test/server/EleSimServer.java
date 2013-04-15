
package remote.test.server;  
  
import java.rmi.RemoteException;  
import java.rmi.registry.LocateRegistry;  
import java.rmi.registry.Registry;  
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.Direction;

import remote.test.AStarSearch;
import remote.test.AssignmentTask;
import remote.test.PlanedController;
import remote.test.PlanedControllerList;
import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;

import rmit.elevator.controller.CarController;
import rmit.elevator.controller.CarControllerList;
import rmit.elevator.planner.AStar;
  
public class EleSimServer extends java.rmi.server.UnicastRemoteObject implements  
        AStarSearch{  
    private static final long serialVersionUID = 2279096828129284306L;  
  
    public EleSimServer() throws RemoteException {  
        super();  
    }  
  
    public static void main(String[] args) {  
        try {  
            EleSimServer h = new EleSimServer();  
            Registry registry = LocateRegistry.createRegistry(2500);  
            registry.bind("hello", h);  
            System.out.println("Start...");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }

	@Override
	public PlanedControllerList Search(AssignmentTask assignmentTask)
			throws RemoteException {
		HashSet<RemoteAssignment> assignments=new HashSet<RemoteAssignment>();
		List<RemoteCarController> carControllers=new ArrayList<RemoteCarController>();
		assignments=assignmentTask.getAssignments();
		carControllers=assignmentTask.getCarControllers();
		for (RemoteCarController car:carControllers){
			System.out.printf("Car%s,currentFloor:%d\n",car.getName(),car.getCurrentFloor());
		}
		System.out.println(assignments);
		// TODO Auto-generated method stub
		PlanedControllerList newList= AStar.AStarSearch(carControllers,assignments);
	    ArrayList<PlanedController> planedControllers=new ArrayList<PlanedController>();
		planedControllers=newList.getList();
		for (PlanedController con:planedControllers){
			ArrayList<Integer> array=new ArrayList<Integer>();
			System.out.printf("Car%s:\n",con.getName());
			array.addAll(con.getList());
			for (Integer j:array){
				System.out.printf("Floor%d,",j);
	    	}
			System.out.println();
	    }
		System.out.println("Server AStarSearch Done!!");
		return newList;
	}  
}  
