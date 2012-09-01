// Internal action code for project Elevator Simulator in Jason

package elevator;

import org.intranet.elevator.ElevatorSimulationApplication;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class run extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'elevator.run'");
        new Thread(new Runnable(){
			public void run() {
				ElevatorSimulationApplication.main(null);				
			}        	
        }).start();
       
        return true;
    }
}
