// Internal action code for project Elevator Simulator in Jason

package elevator;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.rmi.*;
import java.util.logging.Logger;

import org.intranet.elevator.ElevatorSimulationApplication;

import au.edu.rmit.elevator.ServerEnvironment;
import au.edu.rmit.elevator.common.*;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class start_server extends DefaultInternalAction {

	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args)
			throws Exception {
		// execute the internal action

		Logger logger = ts.getAg().getLogger();
		logger.info("executing internal action 'elevator.start_server");

		int port = 8080;
		if (args[0].isNumeric()) {
			port = (int) ((NumberTerm) args[0]).solve();
		}

		try {
			ElevatorService service = ServerEnvironment.getInstance();
			ElevatorService stub = (ElevatorService) UnicastRemoteObject
					.exportObject(service, 0);
			logger.info("Exporting to registry");
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind("elevator", stub);
			logger.info("Jason Elevator Service bound on port "+port);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
