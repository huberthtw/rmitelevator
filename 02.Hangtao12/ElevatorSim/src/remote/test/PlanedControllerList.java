package remote.test;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanedControllerList implements Serializable {
	ArrayList<PlanedController> controllers=new ArrayList<PlanedController>();
	public void addController(PlanedController car){
		controllers.add(car);
	}
	public void addAssignment(RemoteCarController car,RemoteAssignment ass){
		for (PlanedController controller:controllers){
			if (car.getName().equals(controller.getName())){
				controller.addAssignment(ass);
				break;
			}
		}
	}
	public ArrayList<PlanedController> getList() {
		// TODO Auto-generated method stub
		return controllers;
	}
}
