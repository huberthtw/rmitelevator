// Agent car_driver in project Elevator Simulator in Jason

/* Initial beliefs and rules */

next(N):-destinations(N,A) & not (destinations(M,B) & (B<A)).

//direction(up).

//at_bottom(1).

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").


-started:true
<-
.print("End of game");
-destinations(_,_);
-request(_,_).

+started
: true
<-
+at(1)[source(percept)]. //This is not really set by the environment, need to find a way through

+started:true
<-
.print("Game started").

-at(X) : destinations(X,N) & not (request(X,D) | request(X))
<-.print("Moved away from ",X);
-destinations(X,N).

+destinnations(X,N).
-destinnations(X,N):true
<-remove_destinations(X,N).

-at(X) : true
<-.print("Moved away from ",X).

+at(X):not destinations(_,_)
<-
.print("I'm stopping now");
-+stop(0);
stop.

+at(X) : destinations(X,N) & not (request(X,D) | request(X))
<-
.print("I'm now at ",X);
remove_destinations(X,N);
!determine_next_destination;
go.

+at(X) : true
<-
.print("I'm now at ",X);
!determine_next_destination;
go.

+search_done(N):stop(0)
<-.print("Search Done!");
!determine_next_destination;
go.

-search_done(N).

//Determine next destination
//+!determine_next_destination:next(N) & destinations(N,X) & not (request(N) | request(N,D))
//<--destinations(N,X);
//.print("remove destination",N);
//!determine_next_destination.

//+!determine_next_destination:nearest(N)
+!determine_next_destination:next(N) & (request(N) | request(N,D))
<-
-+stop(1);
print_destinations;
.print("set destination at ",N);
set_destination(N).

+!determine_next_destination:true
<-.print("Cannot determine the next floor");
//<-.print("Cannot determine the nearest floor");
-+stop(0);
stop.

+request(Floor):not destinations(Floor,N)
<-.print("I'm already aware of the need to go to ",Floor);
elevator.javaAStar.

-request(Floor):destinations(Floor,N)
<-
-destinations(Floor,N).

//+request(Floor,Direction):true
//<-
//elevator.javaAStar;
//.
+request(Floor,Direction).

-request(Floor,Direction):destinations(Floor,N) & not request(Floor)
<-
//.print("There is no need to go ",Direction," at floor ",Floor);
-destinations(Floor,N).

//-request(Floor,Direction):true
//<-
//.print("There is no need to go ",Direction," at floor ",Floor).


