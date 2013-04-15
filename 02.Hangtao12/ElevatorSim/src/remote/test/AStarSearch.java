package remote.test;
  
import java.rmi.RemoteException;  
import java.util.HashSet;
import java.util.List;

import org.intranet.elevator.model.operate.controller.Assignment;

import rmit.elevator.controller.CarController;
import rmit.elevator.controller.CarControllerList;
  
public interface AStarSearch extends java.rmi.Remote{  
    PlanedControllerList Search(AssignmentTask assignmentTask)	throws RemoteException;  
}  