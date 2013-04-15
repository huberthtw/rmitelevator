How to run the projects:
1.Original Elevator Simulation
**run "org.intranet.elevator.ElevatorSimulationApplication" in project "ElevatorSim"
**choose "File">>"New...">>"Random Rider Insertion"
**click "Real-Time"
**choose "Default MetaController" or "SimpleController"

2.Elevator AStarSearch Controller Simulation
**run "org.intranet.elevator.ElevatorSimulationApplication" in project "ElevatorSim"
**choose "File">>"New...">>"Random Rider Insertion"
**click "Real-Time"
**choose "MyController"

3.Elevator Simulation with Server Side AStarSearch
**run Server:"remote.test.server.EleSimServer" in project "ElevatorSim"
**run Client:"org.intranet.elevator.ElevatorSimulationApplication" in project "ElevatorSim"
**choose "File">>"New...">>"Random Rider Insertion"
**click "Real-Time"
**choose "JClientController"

4.Original Jason Elevator Controller Simulation
**Modify src/asl/coordinator.asl in Project "Elevator Simulation in Jason"
	change car agent to "car_driver.asl"
	add double slash before "elevator.javaAStar"
**run Jason Project:"JElevator.mas2j" in Project "Elevator Simulation in Jason"
**run Client Project:"org.intranet.elevator.ElevatorSimulationApplication" in Project "ElevatorClient"
**choose "File">>"New...">>"Random Rider Insertion"
**click "Real-Time"
**choose "JElevator Client"

5.Jason Elevator Controller Simulation with AStarSearch
**run Jason Project:"JElevator.mas2j" in Project "Elevator Simulation in Jason"
**run Client Project:"org.intranet.elevator.ElevatorSimulationApplication" in Project "ElevatorClient"
**choose "File">>"New...">>"Random Rider Insertion"
**click "Real-Time"
**choose "JElevator Client"

The important file: 
!note: car means elevator.

1.Original Elevator Simulation
package org.intranet.elevator.model.operate.controller
	MetaController.java, SimpleController.java:the controller to control the elevators
	CarController.java:the controller for the car
	CarAssignment.java:the task list for the car
	Assignment.java:the task for the car

2.Elevator AStarSearch Controller Simulation
package rmit.elevator.controller
	MyController.java:the AStarSearch controller to control the elevators
	CarController.java:the controller for the car
	CarRequestAssignment.java:the task for the car which is requested by someone in the car
package rmit.elevator.planner
	AStar.java
	ElevatorDomain.java
	ElevatorHeuristics.java
	ElevatorNode.java
	ElevatorState.java

3.Elevator Simulation with Server Side AStarSearch
Server:	remote.test.server.EleSimServer.java
Client:	rmit.elevator.controller.JClientController.java
package remote.test
	AStarSearch.java:the interface for communication
	corresponding version:
	RemoteCarController:	represent car in AStarSearch
	RemoteController:	CarController
	RemoteAssignment:	Assignment,CarRequestAssignment
package rmit.elevator.planner
	AStar.java
	RemoteDomain.java
	RemoteNode.java
	RemoteState.java

4.Original Jason Elevator Controller Simulation
ElevatorClient:
	au.edu.rmit.elevator.controller.JController.java
Elevator Simulation in Jason:
	src/asl/
		car_driver.asl, simple_driver.asl:the car agents
	src/java/
		au.edu.rmit.elevator.ServerEnvironment.java

5.Jason Elevator Controller Simulation with AStarSearch
Elevator Simulation in Jason:
	src/asl/
		aStar_driver.asl:the car agents
		coordinator.asl
	src/java/
		au.edu.rmit.elevator.AStarServerEnvironment.java
		elevator.javaAStar.java:called by the agent to start AStarSearch