// Agent coordinator in project Elevator Simulator in Jason

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").

+car(Car,Capacity): true
<- .print("New car added");
	.create_agent(Car,"src/asl/car_driver.asl");
	+created(Car).
		

+request(Floor,Direction):true
<-.print("Someone at floor ",Floor," want to go ",Direction);
acknowledge.