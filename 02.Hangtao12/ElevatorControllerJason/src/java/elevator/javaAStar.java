package elevator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import rmit.elevator.controller.RemoteAssignment;
import rmit.elevator.controller.RemoteCarController;
import rmit.elevator.planner.AStar;
import rmit.elevator.planner.RemoteState;

import au.edu.rmit.elevator.AStarServerEnvironment;
import au.rmit.ract.planning.pathplanning.entity.ComputedPlan;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;


public class javaAStar extends DefaultInternalAction{
	static HashSet<RemoteCarController> carNames=new HashSet<RemoteCarController>();
	private int no=0;
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
			throws Exception {
		try{
			Logger logger = ts.getAg().getLogger();
			AStarServerEnvironment environment=AStarServerEnvironment.getInstance();
			List<RemoteCarController> carControllers=new ArrayList<RemoteCarController>();
			HashSet<RemoteAssignment> assignments=new HashSet<RemoteAssignment>();
			HashMap<RemoteCarController,Integer> lastFloor= new HashMap<RemoteCarController,Integer>();
			String agName=ts.getAg().toString();
			List<Literal> percepts=environment.getPercepts(agName);
			//logger.info("AGENTNAME"+agName);
			//List<Literal> percepts=environment.getPercepts("coordinator");
			if (percepts!=null && !percepts.isEmpty()){
				for (Literal pcpt:percepts){
					if (pcpt!=null) {
						ListTerm listTerm=pcpt.getAsListOfTerms();
						if (listTerm.get(0).toString().equals("request")){
							List<NumberTermImpl> numList=(List<NumberTermImpl>)(listTerm.get(1));
							if (numList.size()==1) continue;
							RemoteAssignment newAss=new RemoteAssignment((int)(numList.get(0).solve()));
							assignments.add(newAss);
							//logger.info(ts.getAg().toString()+":"+listTerm.toString());
							continue;
						}
						if (listTerm.get(0).toString().equals("inside_request")){
							List<Term> numList=(List<Term>)(listTerm.get(1));
							String carN=((StringTerm)(numList.get(0))).getString();
							//logger.info("!!!!!!!!!!!!!!!!!!!!!!"+carN);
							for (RemoteCarController carCon:carNames){
								if (carCon.getName().equals(carN)){
									RemoteAssignment newAss=new RemoteAssignment(carCon,(int)((NumberTermImpl)(numList.get(1))).solve());
									assignments.add(newAss);
									//logger.info(ts.getAg().toString()+":"+listTerm.toString());
									break;
								}
							}
						}
					}
				}
			}
			for (RemoteCarController carCon:carNames){
				String car=carCon.getName();
				Integer floor=environment.getDestination(car);
				if (floor!=null) carCon.setDestination(floor);
				else carCon.setDestination(0);
				carControllers.add(carCon);
				/*
				logger.info("Agent:"+car+"!!!");
				List<Literal> carPercepts=environment.getPercepts(car);
				//logger.info(carPercepts.toString());
				if (carPercepts!=null && !carPercepts.isEmpty()){
					for (Literal carPcpt:carPercepts){
						if (carPcpt!=null) {
							ListTerm carListTerm=carPcpt.getAsListOfTerms();
							if (carListTerm.get(0).toString().equals("request")){				
								List<NumberTermImpl> carNumList=(List<NumberTermImpl>)(carListTerm.get(1));
								if (carNumList.size()==2) {
									logger.info(car+":"+carListTerm.toString());
									continue;
								}
								RemoteAssignment carNewAss=new RemoteAssignment(carCon,(int)(carNumList.get(0).solve()));
								assignments.add(carNewAss);
								logger.info(car+":assignment:"+carNewAss);
							}else logger.info(car+":"+carListTerm.toString());
						}
					}
				}*/
			}
			
			//logger.info("Car"+carControllers.toString());
			logger.info("Assignment"+assignments.toString());
			ComputedPlan path=AStar.AStarSearch(carControllers,assignments);
			if (path==null){
				logger.info("Search Failed!!!");
				return false;
			}
			//logger.info(path.toString());
			RemoteState tempState;
			
			for (RemoteCarController cCon:carControllers){
				environment.removePerceptsByUnif(cCon.getName(),
					ASSyntax.createLiteral("destinations", ASSyntax.createVar(),ASSyntax.createVar()));
				lastFloor.put(cCon,0);
			}			
			environment.removePerceptsByUnif(ASSyntax.createLiteral("search_done",ASSyntax.createVar()));

			for (int step=1;path!=null && step<path.getLength();step++){
				tempState=(RemoteState) path.getStep(step);
				int last=lastFloor.get(tempState.getCarController());
				String name=tempState.getCarController().getName();
				int floor=tempState.getAssignment().getDestination();
				if (floor==last) continue;
				lastFloor.put(tempState.getCarController(), floor);
				environment.addPercept(name,
						ASSyntax.createLiteral("destinations",
						ASSyntax.createNumber(floor),ASSyntax.createNumber(step)));
				logger.info(name+"destinations:("+String.valueOf(floor)+","+String.valueOf(step)+")");
				
			}
			//logger.info("DONE");
			environment.addPercept(ASSyntax.createLiteral("search_done",ASSyntax.createNumber(no++)));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public static void addCar(String car){
		for (RemoteCarController carName:carNames){
			if (carName.getName().equals(car)) return;
		}
		carNames.add(new RemoteCarController(car));
	}
	public static void clear(){
		carNames.clear();
	}
}
