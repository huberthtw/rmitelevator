package au.edu.rmit.elevator;

// Environment code for project Elevator Simulator in Jason

import jason.asSyntax.*;
import jason.asSyntax.parser.ParseException;
import jason.environment.*;
import jason.functions.floor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.*;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.Floor;

import au.edu.rmit.elevator.common.*;

public class ServerEnvironment extends Environment implements ElevatorService {

	private Logger logger = Logger.getLogger("Elevator Simulator in Jason."
			+ ServerEnvironment.class.getName());

	private static final ElevatorCallback NULL_CALLBACK = new NullCallBack();
	private ElevatorCallback callback;

	private ElevatorCallback getCallback() {
		return callback != null ? callback : NULL_CALLBACK;
	}

	public ServerEnvironment() throws RemoteException {
		// logger.setLevel(Level.WARNING);
	}

	private static ServerEnvironment instance;
	private boolean moving = false;

	/**
	 * Retrieve the singleton instance of the environment object
	 * 
	 * @return the environment's singleton instance
	 */
	static public ServerEnvironment getInstance() {
		return instance;
	}

	public static final Term up = Literal.parse("up");

	private ArrayList<Car> cars = new ArrayList<Car>();

	// Maps that store where the cars are going and their direction once they
	// get to a floor
	private HashMap<String, boolean[]> directions = new HashMap<String, boolean[]>();
	private HashMap<String, Integer> destinations = new HashMap<String, Integer>();

	/** Called before the MAS execution with the args informed in .mas2j */
	@Override
	public void init(String[] args) {
		super.init(args);
		instance = this;
		addPercept(Literal.parseLiteral("percept(demo)"));
	}

	@Override
	public synchronized boolean executeAction(String agName, Structure action) {

		String act = action.getFunctor();
		logger.info(act);
		if (running)
			try {
				if (act.equals("set_destination")) {
					// logger.info("Setting destination for "+agName);
					int floorNumber = (int) ((NumberTerm) action.getTerm(0))
							.solve();
					setDestination(agName, floorNumber);
					// setDirection(car, floorNumber,
					// action.getTerm(1).equals(ASSyntax.createAtom("up")));
				} else if (act.equals("set_direction")) {
					int floorNumber = (int) ((NumberTerm) action.getTerm(0))
							.solve();
					setDirection(agName, floorNumber,
							action.getTerm(1).equals(ASSyntax.createAtom("up")));
				} else if (act.equals("stop")) {
					setDestination(agName, 0);
				} else if (act.equals("go")) {
					StepHandler.trigger();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		return true;
	}

	public void register(ElevatorCallback callback) throws RemoteException {
		logger.info("Client connected");
		this.callback = callback;
	}

	public void initialize() throws RemoteException {
		cars.clear();
		directions.clear();
		destinations.clear();
		logger.info("Initializing");
		// clearPercepts();
		moving = false;
		running = false;
		hang();
	}

	public void addCar(String car, int capacity, float distance)
			throws RemoteException {
		if (running) {
			initialize();
		}
		// Inform the agent system about the new car and its capacity
		// Remove old data
		removePerceptsByUnif(ASSyntax.createLiteral("car",
				ASSyntax.createAtom(car), ASSyntax.createVar()));
		// Set new capacity
		addPercept(ASSyntax.createLiteral("car", ASSyntax.createAtom(car),
				ASSyntax.createNumber(capacity)));
		logger.info("Reseting position of " + car);
		removePerceptsByUnif(car,
				ASSyntax.createLiteral("at", ASSyntax.createVar()));
		removePerceptsByUnif(ASSyntax.createLiteral("started"));
	}

	public boolean arrive(String car, int floor) throws RemoteException {
		if (directions.get(car) == null) {
			directions.put(car, new boolean[callback.getTotalFloors()]);
		}
		logger.info(car + " arrives at " + floor);

		removePerceptsByUnif(car,
				ASSyntax.createLiteral("at", ASSyntax.createVar()));

		// Inform the car regarding its new location
		addPercept(car,
				ASSyntax.createLiteral("at", ASSyntax.createNumber(floor)));
		// At this point, the car should have updated the direction and
		// destination accordingly
		// hang();
		boolean dir = getDirection(car, floor);
		System.out.println("Removing request at " + floor + " to go toward "
				+ (dir ? "up" : "down"));
		removePercept(ASSyntax.createLiteral("request",
				ASSyntax.createNumber(floor),
				ASSyntax.createAtom(dir ? "up" : "down")));
		removePercept(car,
				ASSyntax.createLiteral("request", ASSyntax.createNumber(floor)));
		return dir;
	}

	private boolean getDirection(String car, int floorNumber) {
		if (floorNumber == 1)
			return true;
		if (floorNumber == directions.get(car).length)
			return false;
		boolean dir;
		dir = (directions.get(car))[floorNumber - 1];
		return dir;
	}

	boolean running;

	public void requestCar(int floor, boolean direction) throws RemoteException {
		running = true;
		logger.info("Car requested at floor" + floor + " to go "
				+ (direction ? "up" : "down"));
		addPercept(ASSyntax.createLiteral("request",
				ASSyntax.createNumber(floor),
				ASSyntax.createAtom(direction ? "up" : "down")));
		addPercept(ASSyntax.createLiteral("started"));
		addPercept(ASSyntax.createLiteral("top",
				ASSyntax.createNumber(getCallback().getTotalFloors())));
		hang();
	}

	public void setNextDestination(String car) throws RemoteException {
		logger.info("Setting next destination for " + car);
		int floor = destinations.get(car).intValue();
		if (floor > 0) {
			logger.info("Going to floor " + floor);
		} else
			logger.info(car + " will stop now.");
		getCallback().setDestination(car, floor);
	}

	private void setDirection(String car, int floorNumber, boolean direction) {
		(directions.get(car))[floorNumber - 1] = direction;
	}

	private void setDestination(String car, int floorNumber) {
		try {
			destinations.put(car, new Integer(floorNumber));
			setNextDestination(car);
			if (directions.get(car) == null) {
				int totalFloors = callback.getTotalFloors();
				logger.info("There are " + totalFloors + " floors.");
				directions.put(car, new boolean[totalFloors]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void hang() {
		StepHandler.step(500);
	}

	public void requestFloor(String car, int floor) throws RemoteException {
		addPercept(car,
				ASSyntax.createLiteral("request", ASSyntax.createNumber(floor)));
	}

}
