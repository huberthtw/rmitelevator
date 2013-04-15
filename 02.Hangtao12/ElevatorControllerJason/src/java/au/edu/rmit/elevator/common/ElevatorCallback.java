package au.edu.rmit.elevator.common;

import java.rmi.*;

public interface ElevatorCallback extends Remote {
	int getTotalFloors() throws RemoteException;

	void setDestination(String car, int floor) throws RemoteException;
}
