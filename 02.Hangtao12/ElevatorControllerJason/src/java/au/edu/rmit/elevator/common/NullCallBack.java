package au.edu.rmit.elevator.common;

import java.rmi.RemoteException;

public class NullCallBack implements ElevatorCallback {

	public int getTotalFloors() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setDestination(String car, int floor) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
