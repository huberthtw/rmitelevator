package remote.test;

import java.io.Serializable;

import org.intranet.elevator.model.operate.controller.Assignment;

import rmit.elevator.controller.CarRequestAssignment;

public class RemoteAssignment implements Serializable{
	//private RemoteCarController car;
	private String car;
	private int destination;
	public RemoteAssignment(Assignment assignment) {
		// TODO Auto-generated constructor stub
		destination=assignment.getDestination().getFloorNumber();
		if (assignment instanceof CarRequestAssignment){
			System.out.println("GOOD");
			car=((RemoteController)((CarRequestAssignment)assignment).getCarController()).getController().getName();
		}else car=null;
	}
	public RemoteAssignment(int floor) {
		// TODO Auto-generated constructor stub
		destination=floor;
		car=null;
	}
	public RemoteAssignment(RemoteCarController carController, int currentFloor) {
		// TODO Auto-generated constructor stub
		destination=currentFloor;
		car=carController.getName();
	}
	public String getCar(){
	//public RemoteCarController getCar(){
		return car;
	}
	public int getDestination(){
		return destination;
	}
	public String toString(){
		Integer i=destination;
    	return "Floor"+i.toString()+car;
    }
    public boolean equals(RemoteAssignment ass){
    	if (car!=null && ass.getCar()!=null)
    		return car.equals(ass.getCar()) && destination==ass.getDestination();
    	else if(car==null && ass.getCar()==null) return destination==ass.getDestination();
    	else return false;
    }
}
