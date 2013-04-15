package remote.test;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanedController implements Serializable {
	private String name;
	private ArrayList<Integer> assignment=new ArrayList<Integer>();
	public PlanedController(String car){
		name=car;
	}
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	public void addAssignment(RemoteAssignment ass) {
		// TODO Auto-generated method stub
		assignment.add(ass.getDestination());
	}
	public ArrayList<Integer> getList(){
		return assignment;
	}
}
