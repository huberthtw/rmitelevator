package rmit.elevator.planner;

import java.util.HashMap;
import java.util.HashSet;

import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;

import au.rmit.ract.planning.pathplanning.entity.SearchNode;
import au.rmit.ract.planning.pathplanning.entity.State;

public class RemoteNode extends SearchNode{
	public RemoteNode(State node) {
		super(node);
		// TODO Auto-generated constructor stub
	}
	
	public RemoteNode(RemoteAssignment assignment, RemoteCarController carController) {
		// TODO Auto-generated constructor stub
		super(new RemoteState(assignment,carController));
	}
	
	public RemoteNode(HashSet<RemoteAssignment> assignments,RemoteAssignment assignment, RemoteCarController carController) {
		// TODO Auto-generated constructor stub
		super(new RemoteState(assignments,assignment,carController));
	}
	
	public String toString(){
		return ((RemoteState)super.getNode()).getAssignment().toString();
	}
	public boolean equals(RemoteNode o){
		return super.getNode().equals(o.getNode());
	}
	public void setPosition(HashMap<RemoteCarController, RemoteAssignment> carPosition) {
		// TODO Auto-generated method stub
		((RemoteState)super.getNode()).setPosition(carPosition);
	}

	public void addPosition(RemoteCarController carController, RemoteAssignment assignment) {
		// TODO Auto-generated method stub
		((RemoteState)super.getNode()).addPosition(carController,assignment);		
	}

	public HashMap<RemoteCarController, RemoteAssignment> getPositions() {
		// TODO Auto-generated method stub
		return 	((RemoteState)super.getNode()).getPositions();
	}

}
