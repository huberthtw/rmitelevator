/*
 * Copyright 2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator.model.operate.controller;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;
import org.intranet.sim.event.EventQueue;

/**
 * @author Neil McKellar and Chris Dailey
 */
public interface Controller {
	void initialize(EventQueue eQ);
	
	void requestCar(Floor newFloor, Direction d);

	void addCar(Car car, float stoppingDistance);

	/**
	 * To be called only once when a car arrives at a location. This allows the
	 * controller to update any internal data structures that were keeping track
	 * of where the car was going to.
	 * 
	 * @param car
	 *            The car that is arriving.
	 * @return Whether the car is going up after this arrival. Can be used to
	 *         set the direction light on the entrance.
	 */
	boolean arrive(Car car);

	void setNextDestination(Car car);
}
