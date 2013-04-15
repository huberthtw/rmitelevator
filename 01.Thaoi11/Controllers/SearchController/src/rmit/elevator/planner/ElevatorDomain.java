package rmit.elevator.planner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.CarController;
import org.intranet.elevator.model.operate.controller.Controller;

import rmit.elevator.controller.CarRequestAssignment;

import au.rmit.ract.planning.pathplanning.entity.Edge;
import au.rmit.ract.planning.pathplanning.entity.SearchDomain;
import au.rmit.ract.planning.pathplanning.entity.State;
import au.rmit.ract.planning.pathplanning.interfaces.NodeIterator;

public class ElevatorDomain extends SearchDomain{
	private HashSet<Assignment> assignments=new HashSet<Assignment>();
	private List<CarController> controllers=new ArrayList<CarController>();
	
	public ElevatorDomain(HashSet<Assignment> assignments,List<CarController> controllers){
		this.assignments.addAll(assignments);
		this.controllers.addAll(controllers);
	}
	@Override
	public float cost(State arg0, State arg1) {
		// TODO Auto-generated method stub
		CarController carCon=((ElevatorState)arg1).getCarController();
		Assignment ass0=((ElevatorState)arg0).getPositions().get(carCon);
		Assignment ass1=((ElevatorState)arg1).getAssignment();
		return Math.abs(ass0.getDestination().getFloorNumber()-ass1.getDestination().getFloorNumber());
	}

	@Override
	public <T extends Edge> ArrayList<T> getChangedEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeIterator getNextSuccessor(State arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends State> ArrayList<T> getPredecessors(State arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ElevatorState> getSuccessors(State arg0) {
		// TODO Auto-generated method stub
		ArrayList<ElevatorState> successors=new ArrayList<ElevatorState>();
		ElevatorState newState;
		if (((ElevatorState)arg0).getAssignments().size()==0){
			for (Assignment assignment:assignments){
				if (assignment instanceof CarRequestAssignment){
					CarController carController=((CarRequestAssignment)assignment).getCarController();
					Assignment ass=carController.getNearestBase();
					Car car=carController.getCar();
					int sNum=ass.getDestination().getFloorNumber();
					if (car.getDestination()!=null && car.getLocation()==null){
						//System.out.println("Car is moving!");
						int nextNum=assignment.getDestination().getFloorNumber();
						if (ass.getDirection().isUp() && (sNum>nextNum)){
							continue;
						}
						if (ass.getDirection().isDown() && (sNum<nextNum)){
							continue;
						}
					}
					newState= new ElevatorState(((ElevatorState)arg0).getAssignments(),assignment, carController);
					newState.setPosition(((ElevatorState)arg0).getPositions());
					newState.addPosition(carController,assignment);
					successors.add(newState);
					continue;
				}
				for (CarController carController:controllers){
					Assignment ass=carController.getNearestBase();
					Car car=carController.getCar();
					int sNum=ass.getDestination().getFloorNumber();
					if (car.getDestination()!=null && car.getLocation()==null){
						//System.out.println("Car is moving!");
						int nextNum=assignment.getDestination().getFloorNumber();
						if (ass.getDirection().isUp() && (sNum>nextNum)){
							continue;
						}
						if (ass.getDirection().isDown() && (sNum<nextNum)){
							continue;
						}
					}
					newState= new ElevatorState(((ElevatorState)arg0).getAssignments(),assignment, carController);
					newState.setPosition(((ElevatorState)arg0).getPositions());
					newState.addPosition(carController,assignment);
					successors.add(newState);
					//System.out.printf("***successor:%s,%s\n", newState,newState.getCarController());
					
				}
			}
		}else{
			for (Assignment ass:assignments){     
				if (!((ElevatorState)arg0).getAssignments().contains(ass)){
					if (ass instanceof CarRequestAssignment){
						CarController carController=((CarRequestAssignment)ass).getCarController();
						newState= new ElevatorState(((ElevatorState)arg0).getAssignments(),ass, carController);
						newState.setPosition(((ElevatorState)arg0).getPositions());
						newState.addPosition(carController,ass);
						successors.add(newState);
						continue;
					}
					for(CarController carCon:controllers) {   
						newState= new ElevatorState(((ElevatorState)arg0).getAssignments(),ass,(CarController) carCon);
						newState.setPosition(((ElevatorState)arg0).getPositions());
						newState.addPosition((CarController)carCon,ass);
						successors.add(newState);
					}
                }
			}
		}
		return successors;
	}

	@Override
	public float hCost(State arg0, State arg1) {
		// TODO Auto-generated method stub
		return Math.abs(((ElevatorState)arg0).getAssignments().size()-((ElevatorState)arg1).getAssignments().size());
	}

	@Override
	public boolean isBlocked(State arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBlocked(State arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean updateCost(Edge arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateCost(State arg0, State arg1, float arg2) {
		// TODO Auto-generated method stub
		return false;
	}



}
