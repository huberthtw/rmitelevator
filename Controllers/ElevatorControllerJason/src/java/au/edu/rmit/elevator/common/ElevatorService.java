package au.edu.rmit.elevator.common;

import java.rmi.*;

public interface ElevatorService extends Remote {
	void register(ElevatorCallback callback) throws RemoteException;

	void initialize() throws RemoteException;

	void addCar(String car, int capacity, float distance)
			throws RemoteException;

	boolean arrive(String car, int floor) throws RemoteException;

	void requestCar(int floor, boolean direction) throws RemoteException;

	void requestFloor(String car, int floor) throws RemoteException;

	void setNextDestination(String car) throws RemoteException;
}
