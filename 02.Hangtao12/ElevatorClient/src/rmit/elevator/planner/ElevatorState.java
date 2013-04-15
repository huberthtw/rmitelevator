package rmit.elevator.planner;

import java.util.HashMap;
import java.util.HashSet;

import org.intranet.elevator.model.operate.controller.Assignment;

import rmit.elevator.controller.CarController;

import au.rmit.ract.planning.pathplanning.entity.State;

public class ElevatorState extends State{
	private HashSet<Assignment> assignments=new HashSet<Assignment>();
	private HashMap<CarController,Assignment> carPositions=new HashMap<CarController,Assignment>();
	private ElevatorState parent;
	private Assignment assignment;
	private CarController carController;
	public ElevatorState(){super();};
	public ElevatorState(Assignment assignment, CarController carController) {
		// TODO Auto-generated constructor stub
		assignments.add(assignment);
		this.assignment=assignment;
		this.carController=carController;
	}
	public ElevatorState(HashSet<Assignment> assignments) {
		// TODO Auto-generated constructor stub
		this.assignments.addAll(assignments);
	}
	public ElevatorState(HashSet<Assignment> assignments,Assignment assignment, CarController carController) {
		// TODO Auto-generated constructor stub
		this.assignments.addAll(assignments);
		this.assignments.add(assignment);
		this.assignment=assignment;
		this.carController=carController;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		HashSet<Assignment> ass=((ElevatorState)arg0).getAssignments();
		//System.out.printf("***********goalNode:%s\n",ass);
		//System.out.printf("***********tempNode:%s\n----------------------", assignments);
		return assignments.equals(ass);
	}

	public HashSet<Assignment> getAssignments() {
		// TODO Auto-generated method stub
		return assignments;
	}
	public CarController getCarController() {
		// TODO Auto-generated method stub
		return carController;
	}

	public Assignment getAssignment() {
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
	public HashMap<CarController, Assignment> getPositions() {
		// TODO Auto-generated method stub
		return carPositions;
	}
	public void setPosition(HashMap<CarController, Assignment> carPosition) {
		// TODO Auto-generated method stub
		this.carPositions.putAll(carPosition);
	}

	public void addPosition(CarController carController, Assignment destination) {
		// TODO Auto-generated method stub
		carPositions.put(carController, destination);
	}
	public String toString(){
		return assignments.toString();
	}

}
