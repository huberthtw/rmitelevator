package remote.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.intranet.elevator.model.operate.controller.Assignment;

import rmit.elevator.controller.CarController;

public class AssignmentTask implements Serializable {
	HashSet<RemoteAssignment> assignments=new HashSet<RemoteAssignment>();
	List<RemoteCarController> carControllers=new ArrayList<RemoteCarController>();
	/*
	public AssignmentTask(HashSet<Assignment> assignments, List<CarController> carControllers) {
		// TODO Auto-generated constructor stub
		for (Assignment assignment:assignments){
			this.assignments.add(new RemoteAssignment(assignment));
		}
		for (CarController carCon:carControllers){
			this.carControllers.add(new RemoteCarController(carCon));
		}
	}*/
	public AssignmentTask(List<RemoteCarController> carControllerlists,HashSet<Assignment> assignments) {
		// TODO Auto-generated constructor stub
		for (Assignment assignment:assignments){
			RemoteAssignment ass=new RemoteAssignment(assignment);
			for (RemoteAssignment as:this.assignments){
				if (as.equals(ass)) continue;
			}
			this.assignments.add(ass);
		}
		carControllers.addAll(carControllerlists);
	}
	public HashSet<RemoteAssignment> getAssignments(){
		return assignments;
	}
	public List<RemoteCarController> getCarControllers(){
		return carControllers;
	}
}
