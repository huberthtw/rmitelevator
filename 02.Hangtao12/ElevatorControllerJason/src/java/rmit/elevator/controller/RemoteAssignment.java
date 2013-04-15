package rmit.elevator.controller;

public class RemoteAssignment {
	//private RemoteCarController car;
	private String car;
	private int destination;
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
