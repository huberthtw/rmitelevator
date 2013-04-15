package au.edu.rmit.elevator.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.CarRequestPanel;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

import au.edu.rmit.elevator.ElevatorEnvironment;

public class JController implements Controller {
	private List cars = new ArrayList();
	private boolean up = true;
	private boolean carsMoving = false;

	public void initialize(EventQueue eQ) {
		ElevatorEnvironment.getInstance().initialize(eQ);
		cars.clear();
		carsMoving = false;
		up = true;
	}

	public void requestCar(Floor newFloor, Direction d) {
		ElevatorEnvironment.getInstance().requestCar(newFloor, d);
		//moveCars();
	}

	public void addCar(final Car car, float stoppingDistance) {
		ElevatorEnvironment.getInstance().addCar(car, stoppingDistance);
		cars.add(car);
	}

	// TODO: Reduce code duplication between isUp(), getCurrentIndex(), and
	// sendToNextFloor()
	public boolean arrive(Car car) {
		return ElevatorEnvironment.getInstance().arrive(car);/*
		List floors = car.getFloorRequestPanel().getServicedFloors();
		int idx = getCurrentIndex(car);
		// At the top floor, go down; at the bottom floor go up
		up = (idx == floors.size() - 1) ? false : idx == 0 ? true : up;
		return up;*/
	}
	
	public String toString() {
		return "Jason Controller";
	}


	public void setNextDestination(Car car) {
		ElevatorEnvironment.getInstance().setNextDestination(car);
	}
}
