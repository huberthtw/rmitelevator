package rmit.elevator.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.Controller;

import remote.test.PlanedController;
import remote.test.PlanedControllerList;
import remote.test.RemoteAssignment;
import remote.test.RemoteCarController;
import rmit.elevator.controller.CarController;
import rmit.elevator.controller.CarControllerList;

import au.rmit.ract.planning.pathplanning.ai.AStarPlanner;
import au.rmit.ract.planning.pathplanning.entity.ComputedPlan;

public class AStar {
	final static int f=0, g=1, h=2,level=3;
	public static PlanedControllerList AStarSearch(List<RemoteCarController> carControllers,
			HashSet<RemoteAssignment> assignments) {
		// TODO Auto-generated method stub
		RemoteState sState=new RemoteState();
		RemoteAssignment baseAssignment;
		for (RemoteCarController carController:carControllers){
			baseAssignment=new RemoteAssignment(carController,carController.getCurrentFloor());
			sState.addPosition(carController, baseAssignment);
			//System.out.println(carController);
		}
		//System.out.println("CONTROLLERLIST");
		//System.out.println(sState);
		//System.out.println(assignments);
		RemoteState gState=new RemoteState(assignments);
		RemoteDomain map=new RemoteDomain(assignments,carControllers);
		AStarPlanner aStarPlanner=new AStarPlanner(new ElevatorHeuristics());
		ComputedPlan path=(ComputedPlan) aStarPlanner.findPath(map, sState, gState);
		//System.out.println("****************************path*********");
		//System.out.println(path);
		//System.out.println("****************************path*********");
		PlanedControllerList newList=new PlanedControllerList();
		for (RemoteCarController carCon:carControllers){
			newList.addController(new PlanedController(carCon.getName()));
		}
		RemoteState tempState;
		for (int step=1;path!=null && step<path.getLength();step++){
			tempState=(RemoteState) path.getStep(step);
			//System.out.printf("***add State %s***\n",tempState.getCarController());
			newList.addAssignment(tempState.getCarController(),tempState.getAssignment());
		}
		//System.out.println("****************************path*********");
		return newList;
		//for (CarController carCon:carControllers){
		//	((CarController)carCon).setNextDestination();
		//}
	}
	public static CarControllerList AStarSearch(HashSet<Assignment> assignments,
			List<CarController> carControllerList) {
		List<CarController> carControllers= new ArrayList<CarController>();
		carControllers.addAll(carControllerList);
		//MyAStarSearch(assignments,carControllers);//use My AStar Search
		JPathPlanAStar(assignments,carControllers);//use JPathPlan AStar Search
		return new CarControllerList(carControllers);
	}
	private static void JPathPlanAStar(HashSet<Assignment> assignments,
			List<CarController> carControllers) {
		// TODO Auto-generated method stub
		ElevatorState sState=new ElevatorState();
		for (CarController carController:carControllers){
			sState.addPosition(carController, carController.getNearestBase());
		}
		//System.out.println(sState);
		//System.out.println(assignments);
		ElevatorState gState=new ElevatorState(assignments);
		ElevatorDomain map=new ElevatorDomain(assignments,carControllers);
		AStarPlanner aStarPlanner=new AStarPlanner(new ElevatorHeuristics());
		ComputedPlan path=(ComputedPlan) aStarPlanner.findPath(map, sState, gState);
		//System.out.println("****************************path*********");
		//System.out.println(path);
		//System.out.println("****************************path*********");
		for (CarController carCon:carControllers){
			((CarController) carCon).getCarAssignments().removeAll();
		}
		ElevatorState tempState;
		for (int step=1;path!=null && step<path.getLength();step++){
			tempState=(ElevatorState) path.getStep(step);
			//System.out.printf("***add State %s***\n",tempState.getCarController());
			tempState.getCarController().add(tempState.getAssignment());
		}
		//for (CarController carCon:carControllers){
		//	((CarController)carCon).setNextDestination();
		//}
	}
	public static void MyAStarSearch(HashSet<Assignment> assignments,
			List<CarController> carControllers) {
		LinkedList<ElevatorNode> open = new LinkedList<ElevatorNode>();
		HashMap<CarController,Assignment> carPosition=new HashMap<CarController,Assignment>();
		HashSet<Assignment> assignmentState=new HashSet<Assignment>();
		ElevatorNode goal=null,s,last;
		for (CarController carController:carControllers){
			carPosition.put(carController, carController.getNearestBase());
		}
		// TODO Auto-generated method stub
		for (CarController carController:carControllers){
			Assignment ass=carController.getNearestBase();
			Car car=carController.getCar();
			//System.out.println(ass);
			int sNum=ass.getDestination().getFloorNumber();
			for (Assignment assignment:assignments){
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
				ElevatorNode newNode= new ElevatorNode(assignmentState,assignment,carController);
				newNode.setPosition(carPosition);
				newNode.set(g, getStateCost(carPosition.get(carController),assignment));
				newNode.addPosition(carController,assignment);
				newNode.set(h, assignments.size()-1);
				newNode.set(f, newNode.get(g)+newNode.get(h));
				newNode.set(level, 1);
				addInOrder(open,newNode);
			}
		}
		
		while (!open.isEmpty()) {
			// get the first node from open list to expand
			//System.out.println("open:");
			//for (ElevatorNode node:open){
			//	System.out.printf("%s %f %f\n",((ElevatorState)node.getNode()).getAssignment(),node.get(f),node.get(g));
			//}
			s = open.removeFirst();/*
			last = s;assignmentState.clear();
			while (last != null) {
                assignmentState.add(((ElevatorState)last.getNode()).getAssignment());
                last = (ElevatorNode) last.getParent();
			}*/
			if (s.get(level)==assignments.size()) {
				goal=s;//System.out.println("Find Best!!");
				break;
			}
			//System.out.print("assignmentState");
			//System.out.println(assignmentState);
			// we will expand s now, first get all successors
			for (Assignment ass:assignments){     
				if (!((ElevatorState)s.getNode()).getAssignments().contains(ass)){
					for(CarController carCon:carControllers) {   
						/*int sNum=((ElevatorState)s.getNode()).getAssignment().getDestination().getFloorNumber();
						int assNum=ass.getDestination().getFloorNumber();
						if (ass.getDirection().isUp() && (sNum>assNum)){
							continue;
						}
						if (ass.getDirection().isDown() && (sNum<assNum)){
							continue;
						}*/
						ElevatorNode newNode= new ElevatorNode(((ElevatorState)s.getNode()).getAssignments(),ass,carCon);
						newNode.setParent(s);
						newNode.setPosition(s.getPositions());
						newNode.set(g, getStateCost(newNode.getPositions().get(carCon),ass)+s.get(g));
						newNode.addPosition(carCon,ass);
						newNode.set(level, s.get(level)+1);
						newNode.set(h, assignments.size()-newNode.get(level));
						newNode.set(f, newNode.get(g)+newNode.get(h));
						addInOrder(open,newNode);
					}
                }
			}
		}
		for (CarController carCon:carControllers){
			carCon.getCarAssignments().removeAll();
		}
		while(goal!=null){
			ElevatorState eleState=(ElevatorState)goal.getNode();
			eleState.getCarController().addInFront(eleState.getAssignment());
			goal=(ElevatorNode)goal.getParent();
			//System.out.printf("Add %s to %s\n", eleState.getAssignment(),eleState.getCarController());
		}
		//for (CarController carCon:carControllers){
		//	carCon.setNextDestination();
		//}
	}
	private static float getStateCost(Assignment baseAassignment, Assignment assignment) {
		// TODO Auto-generated method stub
		//System.out.println(assignment);
		//System.out.println(baseAassignment);
		return Math.abs(baseAassignment.getDestination().getFloorNumber()-assignment.getDestination().getFloorNumber());
		//return Math.abs(floor.getAbsoluteCeiling()-assfloor.getAbsoluteCeiling());
	}

	private static void addInOrder(LinkedList<ElevatorNode> open,
			ElevatorNode snextSearch) {
		// TODO Auto-generated method stub
		if (open.size()==0){
			open.add(0, snextSearch);
			return;
		}
		int begin=0,end=open.size()-1,temp=(end-begin)/2+begin;
		while (end-begin>0){
			if (open.get(temp).get(f)>=snextSearch.get(f)){
				end=temp-1;
				temp=(end-begin)/2+begin;
			}else{
				begin=temp+1;
				temp=(end-begin)/2+begin;
			}
		}
		if (open.get(begin).get(f)<snextSearch.get(f))
			open.add(begin+1, snextSearch);
		else open.add(begin, snextSearch);
		return;
	}


}
