package remote.test;

import org.intranet.elevator.model.Car;
import org.intranet.elevator.model.FloorRequestPanel;

import rmit.elevator.controller.CarController;
/**
 * @author Neil McKellar and Chris Dailey
 * SOON : Still confusing, keep refactoring.
 */
public class RemoteController extends CarController
{
  private RemoteCarController carCon;
  public RemoteController(Car c, float stoppingDist)
  {
    super(c,stoppingDist);
    carCon=new RemoteCarController(c);
  }
  public RemoteController(Car c, float stoppingDist, AStarSearch search) {
	// TODO Auto-generated constructor stub
	    super(c,stoppingDist,search);
	    carCon=new RemoteCarController(c);
  }
  public RemoteController(Car c, float stoppingDist, FloorRequestPanel.Listener listener) {
		// TODO Auto-generated constructor stub
	    super(c,stoppingDist,listener);
	    carCon=new RemoteCarController(c);
  }
  public RemoteCarController getController(){
	  return carCon;
  }
}