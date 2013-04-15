package rmit.elevator.controller;

import java.io.Serializable;

import org.intranet.elevator.model.Car;

import rmit.elevator.controller.CarController;

public class RemoteCarController implements Serializable {
	private String name;
	private int currentFloor;
	private int direction;
	private int destination;
	public RemoteCarController(String car) {
		// TODO Auto-generated constructor stub
		name=car;
		currentFloor=1;
		direction=0;
		destination=-1;
	}
	public void setState(int floor,int d){
		currentFloor=floor;
		direction=d;
	}
	public void setDestination(int floor){
		destination=floor;
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
    public int getDestination(){
    	return destination;
    }
   
    public String toString(){
    	return "Car"+name;
    }
    public boolean equals(RemoteCarController car){
    	return name.equals(car.getName());
    }
}
