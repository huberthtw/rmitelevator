package rmit.elevator.planner;

import au.rmit.ract.planning.pathplanning.ai.heuristics.DistanceHeuristics;
import au.rmit.ract.planning.pathplanning.entity.SearchDomain;
import au.rmit.ract.planning.pathplanning.entity.State;

public class ElevatorHeuristics implements DistanceHeuristics{

	@Override
	public float h(SearchDomain arg0, State arg1, State arg2) {
		// TODO Auto-generated method stub
		return arg0.hCost(arg1, arg2);
	}

	@Override
	public boolean updateH(SearchDomain arg0, State arg1, State arg2, float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
