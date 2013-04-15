package rmit.elevator.planner;

import java.util.HashMap;
import java.util.HashSet;

import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;


import au.rmit.ract.planning.pathplanning.entity.State;

public class RemoteState extends State{
	private HashSet<RemoteAssignment> assignments=new HashSet<RemoteAssignment>();
	private HashMap<RemoteCarController,RemoteAssignment> carPositions=new HashMap<RemoteCarController,RemoteAssignment>();
	private RemoteState parent;
	private RemoteAssignment assignment;
	private RemoteCarController carController;
	public RemoteState(){super();};
	public RemoteState(RemoteAssignment assignment, RemoteCarController carController) {
		// TODO Auto-generated constructor stub
		assignments.add(assignment);
		this.assignment=assignment;
		this.carController=carController;
	}
	public RemoteState(HashSet<RemoteAssignment> assignments) {
		// TODO Auto-generated constructor stub
		this.assignments.addAll(assignments);
	}
	public RemoteState(HashSet<RemoteAssignment> assignments,RemoteAssignment assignment, RemoteCarController carController) {
		// TODO Auto-generated constructor stub
		this.assignments.addAll(assignments);
		this.assignments.add(assignment);
		this.assignment=assignment;
		this.carController=carController;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		HashSet<RemoteAssignment> ass=((RemoteState)arg0).getAssignments();
		//System.out.printf("***********goalNode:%s\n",ass);
		//System.out.printf("***********tempNode:%s\n----------------------", assignments);
		return assignments.equals(ass);
	}

	public HashSet<RemoteAssignment> getAssignments() {
		// TODO Auto-generated method stub
		return assignments;
	}
	public RemoteCarController getCarController() {
		// TODO Auto-generated method stub
		return carController;
	}

	public RemoteAssignment getAssignment() {
		// TODO Auto-generated method stub
		return assignment;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Deprecated
	public boolean isBlocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public State parent() {
		// TODO Auto-generated method stub
		return parent;
	}
	public HashMap<RemoteCarController, RemoteAssignment> getPositions() {
		// TODO Auto-generated method stub
		return carPositions;
	}
	public void setPosition(HashMap<RemoteCarController, RemoteAssignment> carPosition) {
		// TODO Auto-generated method stub
		this.carPositions.putAll(carPosition);
	}

	public void addPosition(RemoteCarController carController, RemoteAssignment destination) {
		// TODO Auto-generated method stub
		carPositions.put(carController, destination);
	}
	public String toString(){
		return assignments.toString();
	}
	public void addPosition(RemoteCarController carController2, int currentFloor) {
		// TODO Auto-generated method stub
		carPositions.put(carController, new RemoteAssignment(currentFloor));
	}

}
