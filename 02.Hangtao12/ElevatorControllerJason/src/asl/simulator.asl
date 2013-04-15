// Agent simulator in project Elevator Simulator in Jason

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

//+!start : true <- elevator.run.

+!start :true <-
//.create_agent("coordinator","src/asl/coordinator.asl");
elevator.start_server(8081).
