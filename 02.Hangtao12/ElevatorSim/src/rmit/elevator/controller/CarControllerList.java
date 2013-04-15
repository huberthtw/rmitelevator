package rmit.elevator.controller;

import java.util.ArrayList;
import java.util.List;

import rmit.elevator.controller.CarController;

public class CarControllerList {
	  private List<CarController> carControllers = new ArrayList<CarController>();
	  public CarControllerList(List<CarController> carControllers){
		  this.carControllers.addAll(carControllers);
	  }
	  public List<CarController> getList(){
		  return carControllers;
	  }
}
