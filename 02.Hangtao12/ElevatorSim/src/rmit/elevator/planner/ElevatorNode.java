package rmit.elevator.planner;

import java.util.HashMap;
import java.util.HashSet;

import org.intranet.elevator.model.operate.controller.Assignment;

import rmit.elevator.controller.CarController;

import au.rmit.ract.planning.pathplanning.entity.SearchNode;
import au.rmit.ract.planning.pathplanning.entity.State;

public class ElevatorNode extends SearchNode{
	public ElevatorNode(State node) {
		super(node);
		// TODO Auto-generated constructor stub
	}
	
	public ElevatorNode(Assignment assignment, CarController carController) {
		// TODO Auto-generated constructor stub
		super(new ElevatorState(assignment,carController));
	}
	
	public ElevatorNode(HashSet<Assignment> assignments,Assignment assignment, CarController carController) {
		// TODO Auto-generated constructor stub
		super(new ElevatorState(assignments,assignment,carController));
	}
	
	public String toString(){
		return ((ElevatorState)super.getNode()).getAssignment().toString();
	}
	public boolean equals(ElevatorNode o){
		return super.getNode().equals(o.getNode());
	}
	public void setPosition(HashMap<CarController, Assignment> carPosition) {
		// TODO Auto-generated method stub
		((ElevatorState)super.getNode()).setPosition(carPosition);
	}

	public void addPosition(CarController carController, Assignment assignment) {
		// TODO Auto-generated method stub
		((ElevatorState)super.getNode()).addPosition(carController,assignment);		
	}

	public HashMap<CarController, Assignment> getPositions() {
		// TODO Auto-generated method stub
		return 	((ElevatorState)super.getNode()).getPositions();
	}

}
