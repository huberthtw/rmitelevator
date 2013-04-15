package rmit.elevator.controller;

import org.intranet.elevator.model.Floor;
import org.intranet.elevator.model.operate.controller.Assignment;
import org.intranet.elevator.model.operate.controller.Direction;

public class CarRequestAssignment extends Assignment {
	CarController carCon;
	public CarRequestAssignment(Floor floor, Direction dir, CarController carCon) {
		super(floor, dir);
		// TODO Auto-generated constructor stub
		this.carCon=carCon;
	}
	public boolean equals(Object o){
		if (o instanceof CarRequestAssignment){
			return super.equals((Assignment)o) && carCon.equals(((CarRequestAssignment)o).getCarController());
		}else return false;
	}
	public CarController getCarController() {
		// TODO Auto-generated method stub
		return carCon;
	}
}
