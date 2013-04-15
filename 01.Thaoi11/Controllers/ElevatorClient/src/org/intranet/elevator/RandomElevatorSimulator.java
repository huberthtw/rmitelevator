/*
 * Copyright 2003-2005 Neil McKellar and Chris Dailey
 * All rights reserved.
 */
package org.intranet.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.Building;
import org.intranet.elevator.model.operate.Person;
import org.intranet.elevator.model.operate.controller.*;
import org.intranet.sim.Model;
import org.intranet.sim.Simulator;
import org.intranet.sim.event.Event;
import org.intranet.ui.ChoiceParameter;
import org.intranet.ui.IntegerParameter;
import org.intranet.ui.LongParameter;

import au.edu.rmit.elevator.controller.JController;

/**
 * @author Neil McKellar and Chris Dailey
 * 
 */
public class RandomElevatorSimulator extends Simulator {
	private IntegerParameter floorsParameter;
	private IntegerParameter carsParameter;
	private IntegerParameter capacityParameter;
	private IntegerParameter ridersParameter;
	private LongParameter durationParameter;
	private LongParameter seedParameter;
	private ChoiceParameter controllerParameter;

	private Building building;

	public RandomElevatorSimulator() {
		super();
		floorsParameter = new IntegerParameter("Number of Floors", 10);
		parameters.add(floorsParameter);
		carsParameter = new IntegerParameter("Number of Elevators", 1);
		parameters.add(carsParameter);
		capacityParameter = new IntegerParameter("Elevator Capacity", 8);
		parameters.add(capacityParameter);
		ridersParameter = new IntegerParameter("Number of People", 20);
		parameters.add(ridersParameter);
		durationParameter = new LongParameter("Rider insertion time (ms)",
				50000);
		parameters.add(durationParameter);
		seedParameter = new LongParameter("Random seed", 635359);
		parameters.add(seedParameter);

		List controllers = new ArrayList();
		Controller controller1 = new MetaController();
		Controller controller2 = new SimpleController();
		Controller controller3 = new JController();
		controllers.add(controller1);
		controllers.add(controller2);
		controllers.add(controller3);
		controllerParameter = new ChoiceParameter("Controller", controllers,
				controller1, Controller.class);
		parameters.add(controllerParameter);
	}

	public void initializeModel() {
		int numFloors = floorsParameter.getIntegerValue();
		int numCars = carsParameter.getIntegerValue();
		int carCapacity = capacityParameter.getIntegerValue();
		int numRiders = ridersParameter.getIntegerValue();
		long duration = durationParameter.getLongValue();
		long seed = seedParameter.getLongValue();
		Controller controller = (Controller) controllerParameter
				.getChoiceValue();

		building = new Building(getEventQueue(), numFloors, numCars,
				carCapacity, controller);

		Random rand = new Random(seed);

		for (int i = 0; i < numRiders; i++) {
			// starting floor
			Floor startingFloor = building.getFloor(rand.nextInt(numFloors));
			final Person person = building.createPerson(startingFloor);
			// destination floor
			Floor floor = null;
			do {
				floor = building.getFloor(rand.nextInt(numFloors));
			} while (floor == startingFloor);
			final Floor destFloor = floor;
			// time to insert
			long insertTime = rand.nextInt((int) duration);
			// insertion event for destination at time
			Event event = new Event(insertTime) {
				public void perform() {
					person.setDestination(destFloor);
				}
			};
			getEventQueue().addEvent(event);
		}
	}

	public final Model getModel() {
		return building;
	}

	public String getDescription() {
		return "Random Rider Insertion";
	}

	public Simulator duplicate() {
		return new RandomElevatorSimulator();
	}
}
