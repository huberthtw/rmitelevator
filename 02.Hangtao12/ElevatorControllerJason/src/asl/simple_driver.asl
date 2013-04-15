// Agent car_driver in project Elevator Simulator in Jason

/* Initial beliefs and rules */

turn(0).

direction(up).

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").


-started:true
<-
.print("End of game");
-turn(_);
+turn(0).

+started
: true
<-
-+at(1)[source(percept)]. //This is not really set by the environment, need to find a way through

+started:true
<-
.print("Game started").

-at(X) : true
<-.print("Moved away from ",X).

+at(1): turn(Y)
<-
-turn(Y);
+turn(Y+1);
!move(1).

+at(X) : true
<-
.print("I'm now at ",X);
!move(X);
acknowledge.

+!move(X): turn(2)
<-stop.

+!move(X): true
<-.print("Move from",X);
!check_direction;
!move_by_direction.

+!move_by_direction:at(X) & direction(up)
<-set_destination(X+1);
set_direction(X+1,up).

+!move_by_direction:at(X) & direction(down)
<-set_destination(X-1);
set_direction(X-1,down).

//Change the direction we are going
+!check_direction: at(X) & top(X)
<-
.print("Will go down now");
-direction(_);
+direction(down).

+!check_direction: at(X) & X==1
<-
.print("Will go up now");
-direction(_);
+direction(up).

+!check_direction. //Default case, nothing to do

+request(Floor):true
<-.print("Someone inside me want to go to floor ",Floor).
