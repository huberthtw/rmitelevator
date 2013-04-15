package remote.test;

import java.io.Serializable;

import org.intranet.elevator.model.Car;

import rmit.elevator.controller.CarController;

public class RemoteCarController implements Serializable {
	private String name;
	private int currentFloor;
	private int direction;
	public RemoteCarController(Car car) {
		// TODO Auto-generated constructor stub
		name=car.getName();
		currentFloor=1;
		direction=0;
	}
	public void setState(int floor,int d){
		currentFloor=floor;
		direction=d;
	}
    public int getCurrentFloor(){
    	return currentFloor;
    }
    public String getName(){
    	return name;
    }
    public int getDirection(){
    	return direction;
    }
   
    public String toString(){
    	return "Car"+name;
    }
    public boolean equals(RemoteCarController car){
    	return name.equals(car.getName());
    }
}
