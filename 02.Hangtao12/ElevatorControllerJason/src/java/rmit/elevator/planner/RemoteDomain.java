package rmit.elevator.planner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rmit.elevator.controller.RemoteAssignment;
import rmit.elevator.controller.RemoteCarController;

import au.rmit.ract.planning.pathplanning.entity.Edge;
import au.rmit.ract.planning.pathplanning.entity.SearchDomain;
import au.rmit.ract.planning.pathplanning.entity.State;
import au.rmit.ract.planning.pathplanning.interfaces.NodeIterator;

public class RemoteDomain extends SearchDomain{
	private HashSet<RemoteAssignment> assignments=new HashSet<RemoteAssignment>();
	private List<RemoteCarController> controllers=new ArrayList<RemoteCarController>();
	
	public RemoteDomain(HashSet<RemoteAssignment> assignments,List<RemoteCarController> controllers){
		this.assignments.addAll(assignments);
		this.controllers.addAll(controllers);
	}
	@Override
	public float cost(State arg0, State arg1) {
		// TODO Auto-generated method stub
		RemoteCarController carCon=((RemoteState)arg1).getCarController();
		RemoteAssignment ass0=((RemoteState)arg0).getPositions().get(carCon);
		if (ass0==null) {
			System.out.println("NULL Begin**************************************");
			System.out.println((((RemoteState)arg0).getPositions().keySet().iterator().next()));
			System.out.println(((RemoteState)arg0).getPositions().get(carCon));
			System.out.println(carCon);
			System.out.println(ass0);
			System.out.println("NULL En**d**************************************");
			
		}
		RemoteAssignment ass1=((RemoteState)arg1).getAssignment();
		//System.out.println(ass1);
		return Math.abs(ass0.getDestination()-ass1.getDestination());
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
	public ArrayList<RemoteState> getSuccessors(State arg0) {
		// TODO Auto-generated method stub
		ArrayList<RemoteState> successors=new ArrayList<RemoteState>();
		RemoteState newState;
		if (((RemoteState)arg0).getAssignments().size()==0){
			for (RemoteAssignment assignment:assignments){
				if (assignment.getCar()!=null){
					RemoteCarController carController=null;
					String name=assignment.getCar();
					for (RemoteCarController car:controllers){
						if (name.equals(car.getName())){
							carController=car;
							break;
						}
					}
					if (carController.getDestination()!=0){
						if (carController.getDestination()!=assignment.getDestination()) continue;
					}else{
						int sNum=carController.getCurrentFloor();
						if (carController.getDirection()!=0){
							//System.out.println("Car is moving!");
							int nextNum=assignment.getDestination();
							if (carController.getDirection()==1 && (sNum>nextNum)){
								continue;
							}
							if (carController.getDirection()==-1 && (sNum<nextNum)){
								continue;
							}
						}
					}
					newState= new RemoteState(((RemoteState)arg0).getAssignments(),assignment, carController);
					newState.setPosition(((RemoteState)arg0).getPositions());
					newState.addPosition(carController,assignment);
					successors.add(newState);
					continue;
				}
				for (RemoteCarController carController:controllers){
					int floor=carController.getDestination();
					if (floor>0){
						if (carController.getDestination()!=assignment.getDestination()) continue;
					}else if(floor<0){
						int sNum=carController.getCurrentFloor();
						if (carController.getDirection()!=0){
							//System.out.println("Car is moving!");
							int nextNum=assignment.getDestination();
							if (carController.getDirection()==1 && (sNum>nextNum)){
								continue;
							}
							if (carController.getDirection()==-1 && (sNum<nextNum)){
								continue;
							}
						}
					}
					newState= new RemoteState(((RemoteState)arg0).getAssignments(),assignment, carController);
					newState.setPosition(((RemoteState)arg0).getPositions());
					newState.addPosition(carController,assignment);
					successors.add(newState);
					//System.out.printf("***successor:%s,%s\n", newState,newState.getCarController());
					
				}
			}
		}else{
			for (RemoteAssignment ass:assignments){     
				if (!((RemoteState)arg0).getAssignments().contains(ass)){
					if (ass.getCar()!=null){
						String name=ass.getCar();
						RemoteCarController carController=null;
						for (RemoteCarController car:controllers){
							if (name.equals(car.getName())){
								carController=car;
								break;
							}
						}
						newState= new RemoteState(((RemoteState)arg0).getAssignments(),ass, carController);
						newState.setPosition(((RemoteState)arg0).getPositions());
						newState.addPosition(carController,ass);
						successors.add(newState);
						continue;
					}
					for(RemoteCarController carCon:controllers) {   
						newState= new RemoteState(((RemoteState)arg0).getAssignments(),ass,(RemoteCarController) carCon);
						newState.setPosition(((RemoteState)arg0).getPositions());
						newState.addPosition((RemoteCarController)carCon,ass);
						successors.add(newState);
					}
                }
			}
		}
		//System.out.println(arg0);
		//System.out.println(successors);
		//System.out.println("***************************************");
		return successors;
	}

	@Override
	public float hCost(State arg0, State arg1) {
		// TODO Auto-generated method stub
		return Math.abs(((RemoteState)arg0).getAssignments().size()-((RemoteState)arg1).getAssignments().size());
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

