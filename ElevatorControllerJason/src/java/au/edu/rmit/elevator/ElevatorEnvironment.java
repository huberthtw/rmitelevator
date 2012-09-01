package au.edu.rmit.elevator;

// Environment code for project Elevator Simulator in Jason

import jason.asSyntax.*;
import jason.asSyntax.parser.ParseException;
import jason.environment.*;
import jason.functions.floor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.*;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.FloorRequestPanel;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

public class ElevatorEnvironment extends Environment implements Controller {

	private Logger logger = Logger.getLogger("Elevator Simulator in Jason."
			+ ElevatorEnvironment.class.getName());

	public ElevatorEnvironment() {
		// logger.setLevel(Level.WARNING);
	}

	static private ElevatorEnvironment instance;
	private boolean moving = false;

	/**
	 * Retrieve the singleton instance of the environment object
	 * 
	 * @return the environment's singleton instance
	 */
	static public ElevatorEnvironment getInstance() {
		return instance;
	}

	public static final Term up = Literal.parse("up");

	private ArrayList<Car> cars = new ArrayList<Car>();

	// Maps that store where the cars are going and their direction once they
	// get to a floor
	private HashMap<Car, boolean[]> directions = new HashMap<Car, boolean[]>();
	private HashMap<Car, Floor> destinations = new HashMap<Car, Floor>();

	/** Called before the MAS execution with the args informed in .mas2j */
	@Override
	public void init(String[] args) {
		super.init(args);
		instance = this;
		addPercept(Literal.parseLiteral("percept(demo)"));
	}

	@Override
	public synchronized boolean executeAction(String agName, Structure action) {
		Car car = findCarByName(agName);
		String act = action.getFunctor();
		logger.info(act);
		try {
			if (act.equals("set_destination")) {
				// logger.info("Setting destination for "+agName);
				int floorNumber = (int) ((NumberTerm) action.getTerm(0))
						.solve();
				setDestination(car, floorNumber);
				// setDirection(car, floorNumber,
				// action.getTerm(1).equals(ASSyntax.createAtom("up")));
			} else if (act.equals("set_direction")) {
				int floorNumber = (int) ((NumberTerm) action.getTerm(0))
						.solve();
				setDirection(car, floorNumber,
						action.getTerm(1).equals(ASSyntax.createAtom("up")));
			} else if (act.equals("stop")) {
				setDestination(car, 0);
			} else if (act.equals("go")) {
				StepHandler.trigger();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Retrieve a car from the list of known cars by its name
	 * 
	 * @param name
	 *            name of the car/agent
	 * @return the corresponding Car object
	 */
	private Car findCarByName(String name) {
		for (Car c : cars)
			if (c.getName().equals(name))
				return c;
		return null;
	}

	/** Called before the end of MAS execution */
	@Override
	public void stop() {
		super.stop();
	}

	public void initialize(EventQueue eQ) {
		cars.clear();
		directions.clear();
		destinations.clear();
		logger.info("Initializing");
		// clearPercepts();
		moving = false;

	}

	/**
	 * Executed when a car is requested at newFloor, going toward direction d
	 */
	public void requestCar(Floor newFloor, Direction d) {
		if (newFloor == null)
			logger.info("Floor is not valid");
		else
			logger.info("Car requested at floor" + newFloor.getFloorNumber()
					+ " to go " + (d.isUp() ? "up" : "down"));
		addPercept(ASSyntax.createLiteral("request",
				ASSyntax.createNumber(newFloor.getFloorNumber()),
				ASSyntax.createAtom(d.toString().toLowerCase())));
		addPercept(ASSyntax.createLiteral("started"));
		addPercept(ASSyntax.createLiteral(
				"top",
				ASSyntax.createNumber(cars.get(0).getFloorRequestPanel()
						.getServicedFloors().size())));
		hang();
	}

	/**
	 * Executed when a car is added to the elevator system, at stoppingDistance
	 * 
	 * @param car
	 * @param stoppingDistance
	 */
	public void addCar(Car car, float stoppingDistance) {
		for (int i = 0; i < cars.size(); i++) {
			if (((Car) cars.get(i)).getName().equals(car.getName())) {
				cars.remove(i);
				break;
			}
		}
		cars.add(car);
		logger.info("Adding a car");
		car.getFloorRequestPanel().addListener(
				new SimplePanelListener(car.getName()));
		// Inform the agent system about the new car and its capacity
		// Remove old data
		removePerceptsByUnif(ASSyntax.createLiteral("car",
				ASSyntax.createAtom(car.getName()), ASSyntax.createVar()));
		// Set new capacity
		addPercept(ASSyntax.createLiteral("car",
				ASSyntax.createAtom(car.getName()),
				ASSyntax.createNumber(car.getCapacity())));
		logger.info("Reseting position of " + car.getName());
		removePerceptsByUnif(car.getName(),
				ASSyntax.createLiteral("at", ASSyntax.createVar()));
		removePerceptsByUnif(ASSyntax.createLiteral("started"));
		/*
		 * addPercept(car.getName(), ASSyntax.createLiteral("at",
		 * ASSyntax.createNumber(0)));
		 */
	}

	/**
	 * Executed when a Car arrives at its destination.
	 * 
	 * @param car
	 * @return
	 */
	public boolean arrive(Car car) {
		if (directions.get(car) == null) {
			directions.put(car, new boolean[car.getFloorRequestPanel()
					.getServicedFloors().size()]);
		}
		int f = car.getLocation().getFloorNumber();
		logger.info(car.getName() + " arrives at " + f);
		List<Floor> floors = car.getFloorRequestPanel().getServicedFloors();
		// TODO: Clear previous service beliefs without (potential) race
		// condition

		// When a floor is found to be "in service", let all the agents know
		for (Floor fl : floors) {
			addPercept(ASSyntax.createLiteral("service",
					ASSyntax.createAtom(car.getName()),
					ASSyntax.createNumber(fl.getFloorNumber())));
		}

		removePerceptsByUnif(car.getName(),
				ASSyntax.createLiteral("at", ASSyntax.createVar()));

		// Inform the car regarding its new location
		Floor currentFloor = car.getLocation();
		int floorNo = (currentFloor != null) ? currentFloor.getFloorNumber()
				: 1;
		addPercept(car.getName(),
				ASSyntax.createLiteral("at", ASSyntax.createNumber(floorNo)));
		// At this point, the car should have updated the direction and
		// destination accordingly
		// hang();
		boolean dir = getDirection(car, f);
		System.out.println("Removing request at " + floorNo + " to go toward "
				+ (dir ? "up" : "down"));
		removePercept(ASSyntax.createLiteral("request",
				ASSyntax.createNumber(floorNo),
				ASSyntax.createAtom(dir ? "up" : "down")));
		removePercept(
				car.getName(),
				ASSyntax.createLiteral("request",
						ASSyntax.createNumber(floorNo)));
		return dir;
	}

	public void setNextDestination(Car car) {
		// Do not set this manually, unless specified by either the simulator or
		// the agents
		logger.info("Setting next destination for " + car.getName());
		Floor goodFloor = destinations.get(car);
		if (goodFloor != null) {
			logger.info("Going to floor " + goodFloor.getFloorNumber());
		} else
			logger.info(car.getName() + " will stop now.");
		car.setDestination(goodFloor);
	}

	private void setDirection(Car car, int floorNumber, boolean direction) {
		(directions.get(car))[floorNumber - 1] = direction;
	}

	private boolean getDirection(Car car, int floorNumber) {
		if (floorNumber == 1)
			return true;
		if (floorNumber == directions.get(car).length)
			return false;
		boolean dir;
		dir = (directions.get(car))[floorNumber - 1];
		return dir;
	}

	private void setDestination(Car car, int floorNumber) {
		if (floorNumber == 0)
			destinations.put(car, null);
		else
			for (Object f : car.getFloorRequestPanel().getServicedFloors()) {
				if (((Floor) f).getFloorNumber() == floorNumber) {
					destinations.put(car, (Floor) f);
					break;
				}
			}
		// destinations.put(car,
		// (Floor)car.getFloorRequestPanel().getServicedFloors().get(floorNumber-1));
		setNextDestination(car);
		if (directions.get(car) == null) {
			directions.put(car, new boolean[car.getFloorRequestPanel()
					.getServicedFloors().size()]);
		}
	}

	private void hang() {
		StepHandler.step(500);
	}

	private class SimplePanelListener implements FloorRequestPanel.Listener {
		private String carName;

		public SimplePanelListener(String carName) {
			this.carName = carName;
		}

		public void floorRequested(Floor floor) {
			// Forward the request to the corresponding car_driver
			addPercept(
					carName,
					ASSyntax.createLiteral("request",
							ASSyntax.createNumber(floor.getFloorNumber())));
		}
	}
}
