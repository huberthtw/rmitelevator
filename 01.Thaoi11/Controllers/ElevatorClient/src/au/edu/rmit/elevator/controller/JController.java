package au.edu.rmit.elevator.controller;

import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.CarRequestPanel;
import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.FloorRequestPanel;
import org.intranet.elevator.model.operate.controller.Controller;
import org.intranet.elevator.model.operate.controller.Direction;
import org.intranet.sim.event.EventQueue;

import au.edu.rmit.elevator.common.*;

public class JController implements Controller {

	ElevatorService service;
	ElevatorCallbackImpl callback = new ElevatorCallbackImpl();
	ElevatorCallback callbackStub;
	Logger logger = Logger.getLogger("JController");
	int floors = 10;

	static int instance;

	public JController() {
		logger.info("There are " + (instance++) + " instance.");
	}

	@Override
	public void initialize(EventQueue eQ) {
		// TODO Auto-generated method stub
		try {
			if (callbackStub == null) {
				callbackStub = (ElevatorCallback) UnicastRemoteObject
						.exportObject(callback, 0);
				Registry registry = LocateRegistry.getRegistry(8081);
				service = (ElevatorService) registry.lookup("elevator");
				logger.info("Connected to Elevator Service");
				logger.info("Registering with elevator service");
				service.register(callbackStub);
				logger.info("Registered with elevator service. Ready for action");
			}
			service.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		cars.clear();
		logger.info("Initializing");
	}

	private ArrayList<Car> cars = new ArrayList<Car>();

	// Maps that store where the cars are going and their direction once they
	// get to a floor

	@Override
	public void requestCar(Floor newFloor, Direction d) {
		try {
			service.requestCar(newFloor.getFloorNumber(), d.isUp());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void addCar(Car car, float stoppingDistance) {
		for (int i = 0; i < cars.size(); i++) {
			if (((Car) cars.get(i)).getName().equals(car.getName())) {
				cars.remove(i);
				break;
			}
		}
		cars.add(car);
		try {
			car.getFloorRequestPanel().addListener(
					new SimplePanelListener(car.getName()));
			service.addCar(car.getName(), car.getCapacity(), stoppingDistance);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class SimplePanelListener implements FloorRequestPanel.Listener {
		private String carName;

		public SimplePanelListener(String carName) {
			this.carName = carName;
		}

		public void floorRequested(Floor floor) {
			// Forward the request to the corresponding car_driver
			try {
				service.requestFloor(carName, floor.getFloorNumber());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean arrive(Car car) {
		try {
			floors = car.getFloorRequestPanel().getServicedFloors().size();
			return service.arrive(car.getName(), car.getLocation()
					.getFloorNumber());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void setNextDestination(Car car) {
		try {
			floors = car.getFloorRequestPanel().getServicedFloors().size();
			service.setNextDestination(car.getName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "JElevator Client";
	}

	class ElevatorCallbackImpl implements ElevatorCallback {

		@Override
		public int getTotalFloors() throws RemoteException {
			return floors;
		}

		@Override
		public void setDestination(String carName, int floor)
				throws RemoteException {
			Car car = getCarByName(carName);
			if (car == null)
				return;
			for (Object o : car.getFloorRequestPanel().getServicedFloors()) {
				Floor f = (Floor) o;
				if (f.getFloorNumber() == floor) {
					car.setDestination(f);
					return;
				}
			}
		}

		private Car getCarByName(String name) {
			for (Car car : cars) {
				if (car.getName().equals(name)) {
					return car;
				}
			}
			return null;
		}
	}
}
